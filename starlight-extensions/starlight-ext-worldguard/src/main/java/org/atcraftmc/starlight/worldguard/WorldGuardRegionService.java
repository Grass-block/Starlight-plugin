package org.atcraftmc.starlight.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.Service;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.util.StandaloneCommand;
import org.atcraftmc.starlight.worldguard.data.RegionKey;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;
import java.util.UUID;

@ApplicationService(id = "wg-region-service")
public interface WorldGuardRegionService extends Service {
    PlotCommand COMMAND = new PlotCommand();

    @ServiceInject
    static void checkServiceCompatibility() throws APIIncompatibleException {
        Compatibility.requirePlugin("WorldGuard");
        Compatibility.requirePlugin("WorldEdit");
    }

    @ServiceInject
    static void start() {
        StarlightBukkitCore.instance().getCommandManager().register(COMMAND);
    }

    @ServiceInject
    static void stop() {
        StarlightBukkitCore.instance().getCommandManager().unregister(COMMAND);
    }

    static Optional<ProtectedRegion> getRegion(RegionKey key) {
        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        var wgWorld = BukkitAdapter.adapt(key.world());
        var rm = container.get(wgWorld);

        if (rm == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(rm.getRegion(key.id()));
    }

    static boolean canAccess(Player player, org.bukkit.entity.Player ep) {
        return WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(
                WorldGuardPlugin.inst().wrapPlayer(ep),
                player.getWorld()
        );
    }

    static boolean isGlobalAccessOpenedTo(World world, Player player) {
        var bp = Bukkit.getPlayer(player.getUniqueId());

        if (canAccess(player, bp)) {
            return true;
        }

        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        var wgWorld = BukkitAdapter.adapt(world);
        var rm = container.get(wgWorld);

        if (rm == null) {
            return true;
        }

        var region = rm.getRegion("__global__");

        if (region == null) {
            return true;
        }

        return region.getFlag(Flags.BUILD) == StateFlag.State.ALLOW;
    }

    static Optional<ProtectedRegion> getSingleRegion(ApplicableRegionSet set) {
        ProtectedRegion result = null;

        for (var region : set) {
            // 跳过 __global__
            if (region.getId().equalsIgnoreCase("__global__")) {
                continue;
            }

            if (result != null) {
                // 命中多个 Region，直接判定失败
                return Optional.empty();
            }

            result = region;
        }

        return Optional.ofNullable(result);
    }

    static Optional<ProtectedRegion> getRegionAt(World world, double x, double y, double z) {
        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        var wgWorld = BukkitAdapter.adapt(world);
        var rm = container.get(wgWorld);

        if (rm == null) {
            return Optional.empty();
        }

        var set1 = rm.getApplicableRegions(BlockVector3.at(x, y, z));

        return getSingleRegion(set1);
    }

    static boolean canAccess(ProtectedRegion region, UUID uuid) {
        return region.getMembers().contains(uuid) || region.getOwners().contains(uuid);
    }

    @Override
    default void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requirePlugin("WorldGuard");
        Compatibility.requirePlugin("WorldEdit");
    }

    @BukkitCommand(name = "plot")
    final class PlotCommand extends StandaloneCommand {
    }
}
