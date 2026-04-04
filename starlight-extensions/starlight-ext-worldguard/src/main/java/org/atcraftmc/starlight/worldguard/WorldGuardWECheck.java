package org.atcraftmc.starlight.worldguard;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.NullExtent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.starlight.foundation.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@ApplicationModule(id = "wg-we-check")
public final class WorldGuardWECheck extends BukkitAbstractModule {

    @Override
    public void enable() {
        WorldEdit.getInstance().getEventBus().register(this);
    }

    @Override
    public void disable() {
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

        if (player == null) {
            return;
        }

        var session = WorldEdit.getInstance().getSessionManager().get(player);

        var bp = Objects.requireNonNull(Bukkit.getPlayer(player.getUniqueId()));
        var bw = bp.getWorld();

        try {
            if (!session.isSelectionDefined(player.getWorld())) {
                return;
            }

            if (WorldGuardRegionService.isGlobalAccessOpenedTo(bw, player)) {
                return;
            }

            var world = player.getWorld();
            var container = WorldGuard.getInstance().getPlatform().getRegionContainer();

            var rm = container.get(world);

            if (rm == null) {
                return;
            }


            var pos = player.getLocation();
            var r = WorldGuardRegionService.getRegionAt(bw, pos.getX(), pos.getY(), pos.getZ());

            if (r.isEmpty()) {
                event.setExtent(new NullExtent());
                language().item("region-warn").send(bp);
                return;
            }

            var region = r.get();

            if (!WorldGuardRegionService.canAccess(region, player.getUniqueId())) {
                event.setExtent(new NullExtent());
                language().item("region-warn").send(bp);
                return;
            }

            var wrap = event.getExtent();
            event.setExtent(new RegionBasedExtent(region, wrap));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static final class RegionBasedExtent implements Extent {
        private final ProtectedRegion region;
        private final Extent wrapped;

        RegionBasedExtent(ProtectedRegion region, Extent wrapped) {
            this.region = region;
            this.wrapped = wrapped;
        }

        private boolean isInvalidPosition(int x, int y, int z) {
            return !this.region.contains(x, y, z);
        }

        @Override
        public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 position, T block) throws WorldEditException {
            var x = position.getBlockX();
            var y = position.getBlockY();
            var z = position.getBlockZ();

            if (isInvalidPosition(x, y, z)) {
                return false;
            }

            return this.wrapped.setBlock(position, block);
        }


        @Nullable
        @Override
        public Entity createEntity(Location location, BaseEntity entity) {
            var x = location.getBlockX();
            var y = location.getBlockY();
            var z = location.getBlockZ();

            if (isInvalidPosition(x, y, z)) {
                return null;
            }

            return this.wrapped.createEntity(location, entity);
        }

        @Nullable
        @Override
        public Operation commit() {
            return this.wrapped.commit();
        }

        @Override
        public BlockState getBlock(BlockVector3 position) {
            var x = position.getBlockX();
            var y = position.getBlockY();
            var z = position.getBlockZ();

            if (isInvalidPosition(x, y, z)) {
                assert BlockTypes.AIR != null;
                return BlockTypes.AIR.getDefaultState();
            }

            return this.wrapped.getBlock(position);
        }

        @Override
        public BaseBlock getFullBlock(BlockVector3 position) {
            return getBlock(position).toBaseBlock();
        }

        @Override
        public BlockVector3 getMinimumPoint() {
            return BlockVector3.ZERO;
        }

        @Override
        public BlockVector3 getMaximumPoint() {
            return BlockVector3.ZERO;
        }

        @Override
        public List<? extends Entity> getEntities(Region region) {
            return this.wrapped.getEntities(region);
        }

        @Override
        public List<? extends Entity> getEntities() {
            return this.wrapped.getEntities();
        }
    }

}
