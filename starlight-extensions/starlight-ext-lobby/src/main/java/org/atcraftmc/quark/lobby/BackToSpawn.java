package org.atcraftmc.quark.lobby;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.modular.Registrations;
import me.gb2022.modular.module.ApplicationModule;
import org.atcraftmc.starlight.framework.module.SLPackageModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

@AutoRegister(Registrations.SERVER_EVENT)
@ApplicationModule(id = "back-to-spawn")
public final class BackToSpawn extends SLPackageModule {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation().add(0.5, 0.5, 0.5));
    }
}
