package org.atcraftmc.starlight.core;

import me.gb2022.commons.reflect.method.MethodHandle;
import me.gb2022.commons.reflect.method.MethodHandleRO0;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.AbstractCommand;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.LocaleMapping;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.atcraftmc.starlight.api.event.ClientLocaleChangeEvent;
import org.atcraftmc.starlight.core.command.CoreCommand;
import org.atcraftmc.starlight.core.command.StarlightCommandManager;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.atcraftmc.starlight.framework.BukkitService;
import org.atcraftmc.starlight.shared.LocaleService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.nio.charset.StandardCharsets;
import java.util.*;

@ApplicationService(id = "locale-service", impl = BukkitLocaleService.class, export = true)
@SuppressWarnings({"Convert2MethodRef", "deprecation"})//For optional func and not-std languages
public final class BukkitLocaleService extends LocaleService<CommandSender> implements BukkitService {
    @ServiceInject
    public static final ServiceHolder<BukkitLocaleService> INSTANCE = new ServiceHolder<>();

    public static final AbstractCommand LANGUAGE_COMMAND = new LanguageDecideCommand();
    public static final UUID CONSOLE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final String ID = "starlight:locale";
    public static final MethodHandleRO0<Player, String> GET_LOCALE = MethodHandle.select((ctx) -> {
        ctx.attempt(() -> Player.class.getMethod("getLocale"), (p) -> p.getLocale());
        ctx.dummy((p) -> LocaleMapping.minecraft(Locale.getDefault()));
    });


    private final Set<UUID> checked = new HashSet<>();

    public static BukkitLocaleService instance() {
        return INSTANCE.get();
    }

    static void setCustomLanguage(Player user, String value) {
        var locale = "auto".equals(value) ? null : MinecraftLocale.minecraft(value);
        BukkitLocaleService.instance().setCustomLocale(user, locale);
    }

    static String remapLanguageNames(String id) {
        return switch (id) {
            case "zh_cn" -> "zh-Hans(简体中文)";
            case "zh_tw" -> "zh-Hant-TW(繁体中文)";
            case "zh_hk" -> "zh-Hant-HK(繁体中文-香港地区)";
            default -> id;
        };
    }

    @Override
    public UUID getIdentifier(CommandSender audience) {
        if (audience instanceof Player p) {
            return p.getUniqueId();
        }

        return CONSOLE_UUID;
    }

    @Override
    public MinecraftLocale getLocaleNatively(CommandSender audience) {
        if (audience instanceof Player p) {
            //We use deprecated locale getter here to support the WTF languages in minecraft.
            return MinecraftLocale.minecraft(GET_LOCALE.invoke(p));
        }

        return MinecraftLocale.locale(Locale.getDefault());
    }

    @Override
    public void enable() throws Exception {
        StarlightCommandManager.getInstance().register(LANGUAGE_COMMAND);
        BukkitUtil.registerEventListener(this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(Starlight.instance(), ID);
        Bukkit.getMessenger().registerIncomingPluginChannel(Starlight.instance(), ID, (channel, player, message) -> testLocale(player.getUniqueId(), MinecraftLocale.minecraft(new String(message, StandardCharsets.UTF_8)), true));

        PlayerReadyService.instance().registerWait(ID);

        for (var player : Bukkit.getOnlinePlayers()) {
            checkLocale(player, getLocaleNatively(player));
        }
    }

    @Override
    public void disable() throws Exception {
        StarlightCommandManager.getInstance().unregister(LANGUAGE_COMMAND);
        BukkitUtil.unregisterEventListener(this);

        Bukkit.getMessenger().unregisterIncomingPluginChannel(Starlight.instance(), ID);
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(Starlight.instance(), ID);

        PlayerReadyService.instance().unregisterWait(ID);
    }

    private void checkLocale(Player player, MinecraftLocale locale) {
        var preset = StarlightBukkitCore.instance().language().item("starlight-core.locale.preset");
        this.testLocale(player.getUniqueId(), locale, true);
        var display = remapLanguageNames(getLocale(player).minecraft());
        this.checked.add(player.getUniqueId());

        BukkitUtil.callEvent(new ClientLocaleChangeEvent(player, locale));
        QLib.task().entity(player).delay(2, () -> {
            QLib.audience(player).sendMessage(preset.message(display));
            PlayerReadyService.instance().complete(player.getUniqueId(), ID);
        });

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        QLib.task().entity(player).delay(10, () -> {
            if (this.checked.contains(player.getUniqueId())) {
                return;
            }
            checkLocale(player, getLocaleNatively(player));
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.checked.remove(event.getPlayer().getUniqueId());
        this.cache.invalidate(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerLocaleChange(PlayerLocaleChangeEvent event) {
        checkLocale(event.getPlayer(), MinecraftLocale.minecraft(event.getLocale()));
    }
}

@BukkitCommand(name = "locale", permission = "+starlight.locale", aliases = {"language", "lang"}, playerOnly = true)
final class LanguageDecideCommand extends CoreCommand {
    @Override
    public void suggest(CommandSuggestion suggestion) {
        suggestion.suggest(0, List.of(LocaleMapping.MINECRAFT_KNOWN_LANGUAGES));
        suggestion.suggest(0, "gui", "auto");
    }

    @Override
    public void execute(CommandExecution context) {
        var data = context.requireArgumentAt(0);
        if (Objects.equals(data, "gui")) {
            //todo: GUI.open(context.requireSenderAsPlayer());
        }

        BukkitLocaleService.setCustomLanguage(context.requireSenderAsPlayer(), data);
        StarlightBukkitCore.lang().item("starlight-core.locale.set").send(QLib.audience(context.getSender()), BukkitLocaleService.remapLanguageNames(data));
        BukkitUtil.callEvent(new ClientLocaleChangeEvent((Player) context.getSender(), BukkitLocaleService.instance().getLocale(context.getSender())));
    }
}
