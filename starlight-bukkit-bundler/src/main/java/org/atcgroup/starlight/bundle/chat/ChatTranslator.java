package org.atcgroup.starlight.bundle.chat;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.audience.PointedAudience;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.language.Language;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.starlight.api.event.ForwardedChatEvent;
import org.atcraftmc.starlight.config.Configurations;
import org.atcraftmc.starlight.core.ComponentSerializer;
import org.atcraftmc.starlight.core.command.CommandProvider;
import org.atcraftmc.starlight.core.command.ModuleCommand;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.Blocking;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ApplicationModule(id = "chat-translator", description = "enable users to translate chat lines to their language.")
@CommandProvider({ChatTranslator.TranslateChatCommand.class, ChatTranslator.TranslateCommand.class})
@AutoRegister(Registrations.SERVER_EVENT)
public final class ChatTranslator extends BukkitAbstractModule {
    private final Cache<String, TranslateRecord> translateRecords = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5)).build();
    private Translator translator;

    @Inject
    private Logger logger;

    @Override
    public void enable() throws Exception {
        var config = Configurations.standalone("translate-service");
        var active = config.getString("active");

        if (active == null || !config.contains(active)) {
            this.logger.error("No active translate service for '{}'.", active);
            return;
        }

        var service = Objects.requireNonNull(config.getConfigurationSection(active));
        var type = Objects.requireNonNull(service.getString("type"));

        this.translator = switch (type) {
            case "google-public" -> new GoogleTranslator();
            case "deepl" -> new DeepLXTranslator(service);
            case "tencent" -> new TencentCloudTranslator(service);
            default -> throw new IllegalStateException("Unexpected Translator: " + type);
        };

        this.logger.info("Using '{}'[{}] as translate service.", active, type);
    }

    //todo: add auto-translate mode
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(ForwardedChatEvent event) {
        var raw = ComponentSerializer.plain(event.getMessage());
        var uuid = UUID.randomUUID().toString();
        var template = Language.format(Objects.requireNonNull(config().value("append").string()), uuid);

        this.translateRecords.put(uuid, new TranslateRecord(raw));

        event.getRenderer().postFix(QLib.textBuilder().buildComponent(template));
    }

    private void renderTranslation(PointedAudience audience, String raw, String translated, MinecraftLocale locale) {
        var temp = this.config().value("template").list(String.class);
        var template = String.join("\n", temp);
        var targetRaw = config().value("substring-raw").bool() ? raw.substring(0, Math.min(raw.length(), 15)) + "..." : raw;
        var ui = Language.format(this.language().inline(template, audience.locale()), targetRaw, locale, translated);

        audience.sendMessage(QLib.textBuilder().buildComponent(ui));
    }

    @BukkitCommand(name = "translate-chat", permission = "+starlight.translate.chat")
    public static final class TranslateChatCommand extends ModuleCommand<ChatTranslator> {
        @Override
        public void execute(CommandExecution context) {
            if (this.getModule().translator == null) {
                QLib.audience(context.getSender()).sendMessage(getLanguage().item("translator-not-configured").message());
                return;
            }

            QLib.task().async().run(() -> {
                var parent = this.getModule();
                var audience = ((PointedAudience) QLib.audience(context.getSender()));
                var locale = audience.locale();
                var messageUUID = context.requireArgumentAt(0);

                var rec = parent.translateRecords.getIfPresent(messageUUID);

                if (rec == null) {
                    //not found or message record out of time
                    audience.sendMessage(this.getLanguage().item("not-found").message());
                    return;
                }

                var raw = rec.getRawMessage();
                var translated = rec.getMessage(locale, parent.translator);

                parent.renderTranslation(audience, raw, translated, locale);
            });
        }
    }

    @BukkitCommand(name = "translate", permission = "-starlight.translate.custom")
    public static final class TranslateCommand extends ModuleCommand<ChatTranslator> {
        @Override
        public void execute(CommandExecution context) {
            if (this.getModule().translator == null) {
                QLib.audience(context.getSender()).sendMessage(getLanguage().item("translator-not-configured").message());
                return;
            }

            QLib.task().async().run(() -> {
                var parent = this.getModule();
                var audience = ((PointedAudience) QLib.audience(context.getSender()));
                var locale = audience.locale();
                var message = context.requireRemainAsParagraph(0, true);
                var translated = parent.translator.translate(message, locale);

                parent.renderTranslation(audience, message, translated, locale);
            });
        }
    }

    private static final class TranslateRecord {
        private final Map<MinecraftLocale, String> translatedMessages = new HashMap<>();
        private final String rawMessage;

        private TranslateRecord(String rawMessage) {
            this.rawMessage = rawMessage;
        }

        public String getMessage(MinecraftLocale locale, Translator translator) {
            if (this.translatedMessages.containsKey(locale)) {
                return this.translatedMessages.get(locale);
            }

            var message = translator.translate(this.rawMessage, locale);

            this.translatedMessages.put(locale, message);

            return message;
        }

        public String getRawMessage() {
            return rawMessage;
        }
    }


    /**
     * Blocking API: invoke from an asynchronous chat/task context. Failures throw IllegalStateException.
     */
    @Blocking
    public interface Translator {
        Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);
        HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

        static String send(HttpRequest request) {
            try {
                var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (response.statusCode() < 200 || response.statusCode() >= 300) {
                    throw new IllegalStateException("Translation API returned HTTP " + response.statusCode());
                }
                return response.body();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Translation request interrupted", e);
            } catch (IOException e) {
                throw new IllegalStateException("Translation request failed", e);
            }
        }

        static String encode(String value) {
            return URLEncoder.encode(value, StandardCharsets.UTF_8);
        }

        String translate(String message, MinecraftLocale target);
    }
}


/**
 * Google web translation endpoint (not the paid Google Cloud Translation API).
 */
final class GoogleTranslator implements ChatTranslator.Translator {
    private final String endpoint;

    public GoogleTranslator() {
        this("https://translate.googleapis.com/translate_a/single");
    }

    public GoogleTranslator(String endpoint) {
        this.endpoint = Objects.requireNonNull(endpoint);
    }

    private static String googleLanguage(MinecraftLocale target) {
        var id = Objects.requireNonNull(target, "target").minecraft().toLowerCase(Locale.ROOT);
        return switch (id) {
            case "zh_cn", "lzh" -> "zh-CN";
            case "zh_tw", "zh_hk" -> "zh-TW";
            case "no_no", "nn_no" -> "no";
            case "tl_ph" -> "tl";
            default -> MinecraftLocale.root(id);
        };
    }

    @Override
    public String translate(String message, MinecraftLocale target) {
        Objects.requireNonNull(message, "message");
        var language = googleLanguage(target);
        if (message.isBlank()) {
            return message;
        }

        var uri = URI.create(this.endpoint + (this.endpoint.contains("?") ? "&" : "?") + "client=gtx&sl=auto&dt=t&tl=" + ChatTranslator.Translator.encode(language) + "&q=" + ChatTranslator.Translator.encode(message));
        var request = HttpRequest.newBuilder(uri).timeout(REQUEST_TIMEOUT).GET().build();
        var body = ChatTranslator.Translator.send(request);
        try {
            var root = JsonParser.parseString(body).getAsJsonArray();
            var segments = root.get(0).getAsJsonArray();
            var translated = new StringBuilder();
            for (var segment : segments) {
                var text = segment.getAsJsonArray().get(0);
                if (!text.isJsonNull()) {
                    translated.append(text.getAsString());
                }
            }
            if (translated.isEmpty()) {
                throw new IllegalStateException("GoogleTranslate returned no translation");
            }
            return translated.toString();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Invalid GoogleTranslate response", e);
        }
    }
}

/**
 * Standard DeepLX POST API. Pass the full endpoint, e.g. http://localhost:1188/translate.
 */
final class DeepLXTranslator implements ChatTranslator.Translator {
    private final URI endpoint;
    private final String token;

    public DeepLXTranslator(ConfigurationSection config) {
        this.endpoint = URI.create(Objects.requireNonNull(config.getString("deepl-endpoint")));
        this.token = Objects.requireNonNull(config.getString("deepl-token"));
    }

    private static String deepLXLanguage(MinecraftLocale target) {
        var id = Objects.requireNonNull(target, "target").minecraft().toLowerCase(Locale.ROOT);
        return switch (id) {
            case "en_gb" -> "EN-GB";
            case "en_us", "en_ca", "en_au", "en_nz" -> "EN-US";
            case "pt_br" -> "PT-BR";
            case "pt_pt" -> "PT-PT";
            case "no_no", "nn_no" -> "NB";
            case "zh_cn", "zh_tw", "zh_hk", "lzh" -> "ZH";
            default -> MinecraftLocale.root(id).toUpperCase(Locale.ROOT);
        };
    }

    @Override
    public String translate(String message, MinecraftLocale target) {
        Objects.requireNonNull(message, "message");
        var language = deepLXLanguage(target);
        if (message.isBlank()) {
            return message;
        }

        var payload = new JsonObject();
        payload.addProperty("text", message);
        payload.addProperty("source_lang", "auto");
        payload.addProperty("target_lang", language);
        var builder = HttpRequest.newBuilder(this.endpoint).timeout(REQUEST_TIMEOUT).header("Content-Type", "application/json; charset=utf-8").POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8));
        if (!this.token.isBlank()) {
            builder.header("Authorization", "Bearer " + this.token);
        }
        var body = ChatTranslator.Translator.send(builder.build());
        try {
            var root = JsonParser.parseString(body).getAsJsonObject();
            if (root.has("code") && root.get("code").getAsInt() != 200) {
                throw new IllegalStateException("DeepLX API error: " + root.get("code"));
            }
            var data = root.get("data");
            if (data == null || !data.isJsonPrimitive() || !data.getAsJsonPrimitive().isString() || data.getAsString().isEmpty()) {
                throw new IllegalStateException("DeepLX returned no translation");
            }
            return data.getAsString();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Invalid or unsuccessful DeepLX response", e);
        }
    }
}

/**
 * Tencent Cloud TextTranslate API, signed with the TC3-HMAC-SHA256 scheme.
 */
final class TencentCloudTranslator implements ChatTranslator.Translator {
    private static final String HOST = "tmt.tencentcloudapi.com";
    private static final String SERVICE = "tmt";
    private static final String VERSION = "2018-03-21";
    private static final DateTimeFormatter UTC_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);

    private final String secretId;
    private final String secretKey;
    private final String region;

    TencentCloudTranslator(ConfigurationSection config) {
        this.secretId = config.getString("tencent-secret-id");
        this.secretKey = config.getString("tencent-secret-key");
        this.region = config.getString("tencent-region");

        Objects.requireNonNull(secretId);
        Objects.requireNonNull(secretKey);
        Objects.requireNonNull(region);

        if (secretId.isBlank() || secretKey.isBlank()) {
            throw new IllegalArgumentException("Tencent Cloud SecretId and SecretKey must be configured");
        }
    }

    @Override
    public String translate(String message, MinecraftLocale target) {
        Objects.requireNonNull(message, "message");
        if (message.isBlank()) {
            return message;
        }

        var payload = new JsonObject();
        payload.addProperty("SourceText", message);
        payload.addProperty("Source", "auto");
        payload.addProperty("Target", targetLanguage(target));
        payload.addProperty("ProjectId", 0);
        var body = payload.toString();
        var timestamp = Instant.now().getEpochSecond();
        var date = UTC_DATE.format(Instant.ofEpochSecond(timestamp));
        var action = "TextTranslate";
        var canonicalHeaders = "content-type:application/json; charset=utf-8\nhost:%s\nx-tc-action:%s\n"
                .formatted(HOST, action.toLowerCase(Locale.ROOT));
        var signedHeaders = "content-type;host;x-tc-action";
        var hashedBody = sha256Hex(body);
        var canonicalRequest = "POST\n/\n\n%s\n%s\n%s".formatted(canonicalHeaders, signedHeaders, hashedBody);
        var credentialScope = "%s/%s/tc3_request".formatted(date, SERVICE);
        var stringToSign = "TC3-HMAC-SHA256\n%d\n%s\n%s"
                .formatted(timestamp, credentialScope, sha256Hex(canonicalRequest));
        var secretDate = hmacSha256("TC3%s".formatted(this.secretKey).getBytes(StandardCharsets.UTF_8), date);
        var secretService = hmacSha256(secretDate, SERVICE);
        var secretSigning = hmacSha256(secretService, "tc3_request");
        var signature = HexFormat.of().formatHex(hmacSha256(secretSigning, stringToSign));
        var authorization = "TC3-HMAC-SHA256 Credential=%s/%s, SignedHeaders=%s, Signature=%s"
                .formatted(this.secretId, credentialScope, signedHeaders, signature);

        var request = HttpRequest.newBuilder(URI.create("https://%s".formatted(HOST)))
                .timeout(ChatTranslator.Translator.REQUEST_TIMEOUT)
                .header("Authorization", authorization)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("X-TC-Action", action)
                .header("X-TC-Version", VERSION)
                .header("X-TC-Timestamp", Long.toString(timestamp))
                .header("X-TC-Region", this.region)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        var response = JsonParser.parseString(ChatTranslator.Translator.send(request)).getAsJsonObject().getAsJsonObject("Response");
        if (response == null) {
            throw new IllegalStateException("Tencent Cloud response is missing Response");
        }
        if (response.has("Error")) {
            var error = response.getAsJsonObject("Error");
            throw new IllegalStateException("Tencent Cloud API error %s: %s"
                    .formatted(error.get("Code").getAsString(), error.get("Message").getAsString()));
        }
        var translated = response.get("TargetText");
        if (translated == null || translated.isJsonNull()) {
            throw new IllegalStateException("Tencent Cloud response is missing TargetText");
        }
        return translated.getAsString();
    }

    private static String targetLanguage(MinecraftLocale target) {
        var id = Objects.requireNonNull(target, "target").minecraft().toLowerCase(Locale.ROOT);
        return switch (id) {
            case "zh_cn", "zh_tw", "zh_hk", "lzh" -> "zh";
            case "en_gb", "en_us", "en_ca", "en_au", "en_nz" -> "en";
            case "ja_jp" -> "ja";
            case "fr_fr", "fr_ca" -> "fr";
            case "ru_ru" -> "ru";
            case "ko_kr" -> "ko";
            case "es_es", "es_mx", "es_ar", "es_cl", "es_ec", "es_uy", "es_ve" -> "es";
            case "de_de", "de_at", "de_ch" -> "de";
            case "it_it" -> "it";
            case "pt_br", "pt_pt" -> "pt";
            case "th_th" -> "th";
            case "vi_vn" -> "vi";
            case "id_id" -> "id";
            case "ms_my" -> "ms";
            case "tr_tr" -> "tr";
            case "ar_sa" -> "ar";
            default -> throw new IllegalArgumentException("Tencent Cloud does not support locale: %s".formatted(id));
        };
    }

    private static byte[] hmacSha256(byte[] key, String value) {
        try {
            var mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign Tencent Cloud request", e);
        }
    }

    private static String sha256Hex(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash Tencent Cloud request", e);
        }
    }
}
