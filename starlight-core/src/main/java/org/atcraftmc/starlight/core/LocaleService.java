package org.atcraftmc.starlight.core;

import com.google.gson.JsonParser;
import me.gb2022.commons.reflect.method.MethodHandle;
import me.gb2022.commons.reflect.method.MethodHandleRO0;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceInject;
import me.gb2022.gluon.service.ServiceLayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.AbstractCommand;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.LocaleMapping;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.qlib.texts.ComponentBlock;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.api.event.ClientLocaleChangeEvent;
import org.atcraftmc.starlight.core.command.CoreCommand;
import org.atcraftmc.starlight.core.command.StarlightCommandManager;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.atcraftmc.starlight.core.ui.InventoryUI;
import org.atcraftmc.starlight.core.ui.TextRenderer;
import org.atcraftmc.starlight.core.ui.UI;
import org.atcraftmc.starlight.core.ui.providing.GUIProvider;
import org.atcraftmc.starlight.core.ui.view.InventoryUIView;
import org.atcraftmc.starlight.shared.jdbc.document.DocumentField;
import org.atcraftmc.starlight.framework.BukkitService;
import org.atcraftmc.starlight.shared.AbstractLocaleService;
import org.atcraftmc.starlight.shared.jdbc.JDBCData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.ServicePriority;

import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@ApplicationService(id = "locale", layer = ServiceLayer.FRAMEWORK)
public interface LocaleService extends BukkitService {
    DocumentField<String> TESTED_LOCALE = DocumentField.string("locale-tested", "unknown");
    DocumentField<String> CUSTOM_LOCALE = DocumentField.string("locale-custom", "auto");

    Map<UUID, String> LOCALE_CACHE = new HashMap<>();
    @SuppressWarnings("Convert2MethodRef")
    MethodHandleRO0<Player, String> GET_LOCALE = MethodHandle.select((ctx) -> {
        ctx.attempt(() -> Player.class.getMethod("getLocale"), (p) -> p.getLocale());
        ctx.dummy((p) -> LocaleMapping.minecraft(Locale.getDefault()));
    });
    AbstractCommand LANGUAGE_COMMAND = new LanguageDecideCommand();
    Listener LISTENER = new BukkitListener();

    @ServiceInject
    static void start() {
        StarlightCommandManager.getInstance().register(LANGUAGE_COMMAND);
        BukkitUtil.registerEventListener(LISTENER);

        Bukkit.getServicesManager().register(BukkitAdapter.class, new BukkitAdapter(), Starlight.instance(), ServicePriority.High);
    }

    @ServiceInject
    static void stop() {
        StarlightCommandManager.getInstance().unregister(LANGUAGE_COMMAND);
        BukkitUtil.unregisterEventListener(LISTENER);

        Bukkit.getServicesManager().unregister(BukkitAdapter.class);
    }

    @SuppressWarnings("deprecation")//because other server software still uses it.
    static MinecraftLocale locale(Object sender) {
        if (sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender) {
            return MinecraftLocale.locale(Locale.getDefault());
        }
        if (!(sender instanceof OfflinePlayer p)) {
            return MinecraftLocale.locale(Locale.getDefault());
        }

        if (LOCALE_CACHE.containsKey(p.getUniqueId())) {
            return MinecraftLocale.minecraft(LOCALE_CACHE.get(p.getUniqueId()));
        }

        var locale = getUserLocale(p);
        LOCALE_CACHE.put(p.getUniqueId(), locale);
        return MinecraftLocale.minecraft(locale);
    }

    static void setCustomLanguage(OfflinePlayer user, String value) {
        CUSTOM_LOCALE.set(JDBCData.PLAYER_SHARED, user.getUniqueId(), value);
        LOCALE_CACHE.put(user.getUniqueId(), getUserLocale(user));
    }

    static String getUserLocale(OfflinePlayer user) {
        try {
            var custom = CUSTOM_LOCALE.get(JDBCData.PLAYER_SHARED, user.getUniqueId());

            if (!Objects.equals(custom, "auto")) {
                return custom;
            }

            var tested = TESTED_LOCALE.get(JDBCData.PLAYER_SHARED, user.getUniqueId());

            if (!Objects.equals(tested, "unknown")) {
                return tested;
            }
        } catch (Exception ignored) {
        }

        if (!(user instanceof Player p)) {
            return MinecraftLocale.locale(Locale.getDefault()).minecraft();
        }

        return saveGetMCPlayerLocale(p);
    }

    static String saveGetMCPlayerLocale(Player player) {
        return GET_LOCALE.invoke(player);
    }

    enum LanguageIcon {

        ENGLISH(
                "en_us",
                "English",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q1ZWQ0YzQzMWQxZjA5NWE0MzI0Y2Y2Y2Y1MmRkMTNmNTc2YjFjYjk0M2NmYjQzY2Y4NmVkZTRiIn19fQ=="
        ),

        SIMPLIFIED_CHINESE(
                "zh_cn",
                "简体中文",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M4ZjQzYmFmNjg3NzA0Y2I2Y2Q2NzNkYTZjNmUwNDU4MTRkNmQ3NmE5NGE0Y2ZiNmNmNjk5NjQ4MiJ9fX0="
        ),

        TRADITIONAL_CHINESE(
                "zh_tw",
                "繁體中文",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ5YzY4ZTQ2NGFkN2Y3NzI4NTkzMTE4YzM4N2FjY2M4ZDU5YTM4YWE4NTA4M2I5OTc3OTM5OWFlZCJ9fX0="
        ),

        JAPANESE(
                "ja_jp",
                "日本語",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM4ZDNjYjYxYjE3YmMzY2Y2NDY4ZTM4YzI2NmM5NGFkNmYzYjM3YmNlNmQzOWQ4OGM2YmNlYjIifX19"
        ),

        FRENCH(
                "fr_fr",
                "Français",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmFkODc4YmM2OWY5ZjY5YWEzMTE5NmU4OTM0OTY2MDFkODc0NzQ3Y2JlYjMwNDY2MTJlZWE0In19fQ=="
        ),

        RUSSIAN(
                "ru_ru",
                "Русский",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTZiN2VkYjEzMGE4YjJmZmY4YWE4ZjViYjYxZGE0MWQ3MjNlN2ZmMjQ5YjY5NjRkYmE0NjY0NSJ9fX0="
        );

        private final String locale;
        private final String displayName;
        private final String texture;

        LanguageIcon(
                String locale,
                String displayName,
                String texture
        ) {
            this.locale = locale;
            this.displayName = displayName;
            this.texture = texture;
        }

        public static void icon(InventoryUI builder, int pos, LanguageIcon icon) {
            var item = new ItemStack(Material.PLAYER_HEAD);
            var meta = (SkullMeta) item.getItemMeta();
            var profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            var textures = profile.getTextures();

            textures.setSkin(textureUrl(icon.texture()));

            profile.setTextures(textures);
            meta.setOwnerProfile(profile);
            item.setItemMeta(meta);

            UI.buildComponent(builder, pos, (o) -> {
                o.icon(item);
                o.name(TextRenderer.literal(Component.text(icon.displayName())
                                                    .color(NamedTextColor.AQUA)
                                                    .decoration(TextDecoration.ITALIC, false)));
                o.operation(UI.command((p) -> "locale " + icon.locale))
                        .operation(UI.SOUND_CLICK);
            });
        }

        private static URL textureUrl(String base64) {

            try {
                var decoded = new String(Base64.getDecoder().decode(base64));
                var json = JsonParser.parseString(decoded).getAsJsonObject();
                var url = json.getAsJsonObject("textures")
                        .getAsJsonObject("SKIN")
                        .get("url")
                        .getAsString();

                return URI.create(url).toURL();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String locale() {
            return locale;
        }

        public String displayName() {
            return displayName;
        }

        public String texture() {
            return texture;
        }
    }

    class BukkitLocaleService extends AbstractLocaleService<CommandSender> implements Listener {

        @Override
        public void enable() throws Exception {
            Bukkit.getMessenger().registerOutgoingPluginChannel(Starlight.instance(), "locale");
            Bukkit.getMessenger().registerIncomingPluginChannel(
                    Starlight.instance(),
                    "locale",
                    (channel, player, message) -> onLanguageUpdated(
                            player,
                            MinecraftLocale.minecraft(
                                    new String(
                                            message,
                                            StandardCharsets.UTF_8
                                    ))
                    )
            );
        }

        @Override
        public void disable() throws Exception {
            Bukkit.getMessenger().unregisterIncomingPluginChannel(Starlight.instance(), "locale");
            Bukkit.getMessenger().unregisterOutgoingPluginChannel(Starlight.instance(), "locale");
        }

        @Override
        public boolean isNativeAudience(CommandSender audience) {
            if (audience instanceof ConsoleCommandSender) {
                return true;
            }
            if (audience instanceof BlockCommandSender) {
                return true;
            }

            return !(audience instanceof OfflinePlayer);
        }

        @Override
        public UUID getIdentifier(CommandSender pointer) {
            if (!(pointer instanceof Player p)) {
                throw new UnsupportedOperationException("Cannot get uid of " + pointer.getClass().getName());
            }
            return p.getUniqueId();
        }

        @Override
        public MinecraftLocale getLocaleNatively(CommandSender pointer) {
            if (!(pointer instanceof Player p)) {
                throw new UnsupportedOperationException("Cannot get locale of " + pointer.getClass().getName());
            }
            return MinecraftLocale.minecraft(GET_LOCALE.invoke(p));
        }

        @Override
        public void onLanguageUpdated(CommandSender audience, MinecraftLocale locale) {
            BukkitUtil.callEvent(new ClientLocaleChangeEvent((Player) audience, locale));
        }

        @Override
        public String getConfigNamespace() {
            return "starlight-core";
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            this.invalidateCachedLocale(event.getPlayer().getUniqueId());
        }

        @EventHandler
        public void onLocaleChange(PlayerLocaleChangeEvent event) {
            var cfg = SLPluginEnvironment.getPlugin().config();

            if (!cfg.value("starlight-core.locale.as-control").bool()) {
                //return;
            }

            this.checkClientLocale(event.getPlayer(), event.getLocale());
            QLib.task().global().delay(60, () -> {
                var natived = getLocaleNatively(event.getPlayer()).minecraft();
                this.checkClientLocale(event.getPlayer(), natived);
            });
        }
    }

    final class BukkitListener implements Listener {
        @EventHandler
        public void onLocaleChange(PlayerLocaleChangeEvent event) {
            _check(event);
            QLib.task().global().delay(
                    60,
                    () -> _check(new PlayerLocaleChangeEvent(
                            event.getPlayer(),
                            saveGetMCPlayerLocale(event.getPlayer())
                    ))
            );
        }


        private void _check(PlayerLocaleChangeEvent event) {
            var preset = Starlight.instance().language().item("starlight-core.locale.preset");
            var locale = "zh_cn";
            try {
                locale = saveGetMCPlayerLocale(event.getPlayer());
            } catch (Exception e) {
                locale = LocaleMapping.minecraft(Locale.getDefault());
            }

            boolean isValidChange = true;

            var uuid = event.getPlayer().getUniqueId();
            var custom = CUSTOM_LOCALE.get(JDBCData.PLAYER_SHARED, uuid);
            var cache = TESTED_LOCALE.get(JDBCData.PLAYER_SHARED, uuid);

            if (Objects.equals(locale, "en_us")) {
                if (!Objects.equals(cache, "unknown")) {
                    locale = cache;
                    isValidChange = false;
                }

                if (!Objects.equals(custom, "auto")) {
                    locale = custom;
                    isValidChange = false;
                }
            }

            if (Objects.equals(custom, "auto")) {
                TESTED_LOCALE.set(JDBCData.PLAYER_SHARED, uuid, locale);
                if (isValidChange) {
                    var block = (preset.component(locale(event.getPlayer()), locale));
                    TextSender.sendMessage(event.getPlayer(), block);
                    LOCALE_CACHE.put(uuid, locale);
                }
            } else {
                if (!Objects.equals(cache, "unknown")) {
                    locale = cache;
                }
            }

            var loc = MinecraftLocale.minecraft(locale);
            BukkitUtil.callEvent(new ClientLocaleChangeEvent(event.getPlayer(), loc));
        }
    }

    final class BukkitAdapter {
        public void setCustomLanguage(Player user, String value) {
            LocaleService.setCustomLanguage(user, value);
        }

        public String getUserLocale(Player user) {
            return LocaleService.getUserLocale(user);
        }

        public MinecraftLocale locale(CommandSender sender) {
            return LocaleService.locale(sender);
        }
    }

    @SuppressWarnings("deprecation")//because other server still uses getLocale()
    @BukkitCommand(name = "locale", permission = "+quark.locale", playerOnly = true)
    final class LanguageDecideCommand extends CoreCommand {
        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, List.of(LocaleMapping.MINECRAFT_KNOWN_LANGUAGES));
            suggestion.suggest(0, "gui", "auto");
        }

        @Override
        public void execute(CommandExecution context) {
            var data = context.requireArgumentAt(0);
            if (Objects.equals(data, "auto")) {
                data = saveGetMCPlayerLocale(context.requireSenderAsPlayer());
            }
            if (Objects.equals(data, "gui")) {
                //GUI.open(context.requireSenderAsPlayer());
            }

            setCustomLanguage(context.requireSenderAsPlayer(), data);

            Starlight.lang().item("starlight-core.locale.set").send(QLib.audience(context.getSender()), data);
            BukkitUtil.callEvent(new ClientLocaleChangeEvent((Player) context.getSender(), locale(context.getSender())));
        }
    }

    final class LocaleUI implements GUIProvider<InventoryUI> {
        @Override
        public InventoryUI create() {
            return new InventoryUI(54, TextRenderer.literal(Component.text("语言 / Language")));
        }

        @Override
        public void render(InventoryUI builder, InventoryUIView view, Object... args) {
            LanguageIcon.icon(builder, 11, LanguageIcon.SIMPLIFIED_CHINESE);
            LanguageIcon.icon(builder, 13, LanguageIcon.TRADITIONAL_CHINESE);
            LanguageIcon.icon(builder, 15, LanguageIcon.ENGLISH);
            LanguageIcon.icon(builder, 29, LanguageIcon.FRENCH);
            LanguageIcon.icon(builder, 31, LanguageIcon.JAPANESE);
            LanguageIcon.icon(builder, 33, LanguageIcon.RUSSIAN);

            //close button
            UI.buildComponent(builder, 53, (b) -> {
                b.icon(UI.icon(Material.REDSTONE));
                b.name(TextRenderer.data(Starlight.lang().item("common", "ui", "close")));
                b.operation(UI.SOUND_CLICK);
                b.operation(UI.close());
            });
        }
    }
}
