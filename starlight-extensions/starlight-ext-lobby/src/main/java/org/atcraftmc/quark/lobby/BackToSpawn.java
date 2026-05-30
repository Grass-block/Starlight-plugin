package org.atcraftmc.quark.lobby;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

@AutoRegister(Registrations.SERVER_EVENT)
@ApplicationModule(id = "lobby-back-to-spawn", description = "Teleports players to world spawn on join")
public final class BackToSpawn extends BukkitAbstractModule {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation().add(0.5, 0.5, 0.5));
    }
}
