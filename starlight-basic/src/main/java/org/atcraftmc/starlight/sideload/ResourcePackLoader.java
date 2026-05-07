package org.atcraftmc.starlight.sideload;

import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class ResourcePackLoader extends BukkitAbstractModule {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var audience = QLib.audience(event.getPlayer());
    }
}
