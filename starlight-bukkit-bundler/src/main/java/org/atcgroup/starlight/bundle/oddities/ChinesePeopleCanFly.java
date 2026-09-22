package org.atcgroup.starlight.bundle.oddities;

import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.starlight.api.event.ClientLocaleChangeEvent;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationModule(id = "chinese-can-fly", defaultEnable = false)
public final class ChinesePeopleCanFly extends BukkitAbstractModule {
    private final Map<UUID, Boolean> recordedFlyMode = new HashMap<>();

    @EventHandler
    public void onLocaleChange(ClientLocaleChangeEvent event) {
        var b1 = event.getLocale() == MinecraftLocale.ZH_CN;
        var b2 = event.getLocale() == MinecraftLocale.ZH_HK;
        var b3 = event.getLocale() == MinecraftLocale.ZH_TW;
        var b4 = event.getLocale() == MinecraftLocale.LZH;

        if (b1 || b2 || b3 || b4) {
            if (!this.recordedFlyMode.containsKey(event.getPlayer().getUniqueId())) {
                this.recordedFlyMode.put(event.getPlayer().getUniqueId(), event.getPlayer().getAllowFlight());
            }

            event.getPlayer().setAllowFlight(true);

            //中国人能飞!
            language().item("fly-hint").send(QLib.audience(event.getPlayer()));
        } else {
            if (!this.recordedFlyMode.containsKey(event.getPlayer().getUniqueId())) {
                event.getPlayer().setAllowFlight(this.recordedFlyMode.get(event.getPlayer().getUniqueId()));
                this.recordedFlyMode.remove(event.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.recordedFlyMode.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public void disable() throws Exception {
        for (var player : Bukkit.getOnlinePlayers()) {
            if (!this.recordedFlyMode.containsKey(player.getUniqueId())) {
                player.setAllowFlight(this.recordedFlyMode.get(player.getUniqueId()));
                this.recordedFlyMode.remove(player.getUniqueId());
            }
        }
    }
}
