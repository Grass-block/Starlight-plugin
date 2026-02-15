package org.atcraftmc.starlight.worldguard;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.NullExtent;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldguard.WorldGuard;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.starlight.foundation.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;

import java.util.Objects;

@ApplicationModule(id = "wg-we-check")
public final class WorldGuardWECheck extends BukkitAbstractModule {

    @Override
    public void enable() throws Exception {
        WorldEdit.getInstance().getEventBus().register(this);
    }

    @Override
    public void disable() throws Exception {
        WorldEdit.getInstance().getEventBus().unregister(this);
    }

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requirePlugin("WorldGuard");
        Compatibility.requirePlugin("WorldEdit");
    }


    @Subscribe(priority = EventHandler.Priority.VERY_EARLY)
    public void onEditSession(EditSessionEvent event) {
        var player = ((Player) event.getActor());
        var session = WorldEdit.getInstance().getSessionManager().get(player);

        if (player == null) {
            return;
        }

        var bp = Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId()));
        var bw = bp.getWorld();

        try {
            if (!session.isSelectionDefined(player.getWorld())) {
                return;
            }

            if (WorldGuardRegionService.isGlobalAccessOpenedTo(bw, player)) {
                return;
            }

            Region selection;
            try {
                selection = session.getSelection(player.getWorld());
            } catch (IncompleteRegionException e) {
                return;
            }

            var min = selection.getMinimumPoint();
            var max = selection.getMaximumPoint();

            var world = player.getWorld();
            var container = WorldGuard.getInstance().getPlatform().getRegionContainer();

            var rm = container.get(world);

            if (rm == null) {
                return;
            }

            var r1 = WorldGuardRegionService.getRegionAt(bw, min.getX(), min.getY(), min.getZ());
            var r2 = WorldGuardRegionService.getRegionAt(bw, max.getX(), max.getY(), max.getZ());

            if (r1.isEmpty() || r2.isEmpty() || !r1.get().getId().equals(r2.get().getId())) {
                // 取消选区
                event.setExtent(new NullExtent());
                language().item("region-limit").send(bp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
