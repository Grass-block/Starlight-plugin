package org.atcraftmc.starlight.tweaks;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.permissions.Permission;

@AutoRegister({Registrations.SERVER_EVENT})
@ApplicationModule(id = "sit-on-player",description = "Enable player to sit on others by right-click.")
public final class SitOnPlayer extends BukkitAbstractModule {

    @Inject("+starlight.misc.sit")
    public Permission sitPermission;

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player target)) {
            return;
        }

        boolean s = event.getPlayer().hasPermission(this.sitPermission);
        boolean t = target.hasPermission(this.sitPermission);

        if ((!s) && t) {
            return;
        }

        target.addPassenger(event.getPlayer());
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        for (var e : event.getPlayer().getPassengers()) {
            if (!(e instanceof Player target)) {
                continue;
            }
            event.getPlayer().removePassenger(e);
            return;
        }
    }
}
