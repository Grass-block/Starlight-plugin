package org.atcraftmc.starlight.core;

import me.gb2022.commons.reflect.method.MethodHandle;
import me.gb2022.commons.reflect.method.MethodHandleRO0;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.language.LocaleMapping;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.atcraftmc.starlight.api.event.ClientLocaleChangeEvent;
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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@ApplicationService(id = "locale-service", impl = BukkitLocaleService.class, export = true)
public final class BukkitLocaleService extends LocaleService<CommandSender> implements BukkitService {
    @ServiceInject
    public static final ServiceHolder<BukkitLocaleService> INSTANCE = new ServiceHolder<>();
    private static final UUID CONSOLE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private final Set<UUID> checked = new HashSet<>();
    @SuppressWarnings("Convert2MethodRef")
    MethodHandleRO0<Player, String> GET_LOCALE = MethodHandle.select((ctx) -> {
        ctx.attempt(() -> Player.class.getMethod("getLocale"), (p) -> p.getLocale());
        ctx.dummy((p) -> LocaleMapping.minecraft(Locale.getDefault()));
    });

    public static BukkitLocaleService getInstance() {
        return INSTANCE.get();
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
        StarlightCommandManager.getInstance().register(org.atcraftmc.starlight.core.LocaleService.LANGUAGE_COMMAND);
        BukkitUtil.registerEventListener(this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(Starlight.instance(), "starlight:locale");
        Bukkit.getMessenger().registerIncomingPluginChannel(
                Starlight.instance(),
                "starlight:locale",
                (channel, player, message) -> testLocale(
                        player.getUniqueId(),
                        MinecraftLocale.minecraft(new String(
                                message,
                                StandardCharsets.UTF_8
                        )), true
                )
        );
    }

    @Override
    public void disable() throws Exception {
        StarlightCommandManager.getInstance().unregister(org.atcraftmc.starlight.core.LocaleService.LANGUAGE_COMMAND);
        BukkitUtil.unregisterEventListener(this);

        Bukkit.getMessenger().unregisterIncomingPluginChannel(Starlight.instance(), "starlight:locale");
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(Starlight.instance(), "starlight:locale");
    }

    private void checkLocale(Player player, MinecraftLocale locale) {
        var preset = StarlightBukkitCore.instance().language().item("starlight-core.locale.preset");
        this.testLocale(player.getUniqueId(), locale, true);
        var display = org.atcraftmc.starlight.core.LocaleService.remapLanguageNames(getLocale(player).minecraft());

        QLib.task().entity(player).delay(3, () -> QLib.audience(player).sendMessage(preset.message(display)));

        this.checked.add(player.getUniqueId());

        BukkitUtil.callEvent(new ClientLocaleChangeEvent(player, locale));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        QLib.task().entity(player).delay(15, () -> {
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
