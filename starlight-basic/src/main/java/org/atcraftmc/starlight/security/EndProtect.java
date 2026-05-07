package org.atcraftmc.starlight.security;

import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@ApplicationModule(id = "end-protect", defaultEnable = false)
public final class EndProtect extends BukkitAbstractModule {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCrystalPlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getItem() == null || event.getItem().getType() != Material.END_CRYSTAL) {
            return;
        }

        var world = event.getPlayer().getWorld();

        // 只限制末地
        if (world.getEnvironment() != World.Environment.THE_END) {
            return;
        }

        var clicked = event.getClickedBlock();
        if (clicked == null) {
            return;
        }

        var loc = clicked.getLocation();

        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        if (x >= -10 && x <= 10 && z >= -10 && z <= 10) {
            event.setCancelled(true);
            language().item("cristal-prevent").send(QLib.audience(event.getPlayer()));
        }
    }
}
