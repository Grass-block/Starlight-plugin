package org.atcraftmc.starlight.security;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.modular.Registrations;
import me.gb2022.modular.module.ApplicationModule;
import org.atcraftmc.starlight.core.permission.PermissionService;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.permissions.Permission;

@ApplicationModule(id = "guest-mode")
@AutoRegister(Registrations.SERVER_EVENT)
public final class GuestMode extends BukkitAbstractModule {
    @Inject("-starlight.guest.bypass")
    public Permission bypassGuestPermission;

    private boolean testPermission(Player player) {
        if (player.hasPermission(this.bypassGuestPermission)) {
            return false;
        }
        if (!this.config().value("affect-world").list(String.class).contains(player.getWorld().getName())) {
            return false;
        }
        MessageAccessor.send(this.language(), player, "guest");
        return true;
    }

    @Override
    public void enable() {
        PermissionService.update();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (testPermission(event.getPlayer())) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBreak(final BlockBreakEvent event) {
        if (testPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteractEntity(final PlayerInteractEntityEvent event) {
        if (testPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(final PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("//pos")) {
            return;
        }

        if (testPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
