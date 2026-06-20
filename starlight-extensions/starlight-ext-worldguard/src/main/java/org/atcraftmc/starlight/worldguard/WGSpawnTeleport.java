package org.atcraftmc.starlight.worldguard;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.core.platform.Players;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.util.StandaloneCommand;
import org.atcraftmc.starlight.worldguard.api.RegionKey;
import org.atcraftmc.starlight.worldguard.api.RegionRelatedCommand;
import org.atcraftmc.starlight.worldguard.api.WGSpawnAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.joml.Vector3d;

//todo: untested
@ApplicationModule(id = "wg-spawn-tp")
public final class WGSpawnTeleport extends BukkitAbstractModule {
    private final StandaloneCommand plotTeleportCommand = new PlotTeleportCommand();
    private final RegionRelatedCommand plotTeleportControlCommand = new PlotTeleportPermissionCommand();
    private final RegionRelatedCommand plotSetSpawnCommand = new PlotSpawnCommand();
    private final RegionRelatedCommand plotClearSpawnCommand = new PlotSpawnClearCommand();

    static LanguageItem lang(String id) {
        return SLPluginEnvironment.getApplication().language().item("starlight-worldguard:wg-spawn-tp:" + id);
    }

    @Override
    public void enable() {
        WGCommandService.COMMAND.registerSubCommand(this.plotTeleportCommand);
        WGCommandService.COMMAND.registerSubCommand(this.plotTeleportControlCommand);
        WGCommandService.COMMAND.registerSubCommand(this.plotSetSpawnCommand);
        WGCommandService.COMMAND.registerSubCommand(this.plotClearSpawnCommand);
    }

    @Override
    public void disable() {
        WGCommandService.COMMAND.unregisterSubCommand(this.plotTeleportCommand);
        WGCommandService.COMMAND.unregisterSubCommand(this.plotTeleportControlCommand);
        WGCommandService.COMMAND.unregisterSubCommand(this.plotSetSpawnCommand);
        WGCommandService.COMMAND.unregisterSubCommand(this.plotClearSpawnCommand);
    }

    @BukkitCommand(name = "tp", permission = "+starlight.worldguard.tp")
    public static final class PlotTeleportCommand extends StandaloneCommand {
        @Override
        public void execute(CommandExecution context) {
            var rk = RegionKey.fromSearchId(context.requireArgumentAt(0));
            var player = context.requireSenderAsPlayer();
            var region = WGRegionService.getRegion(rk);

            if (region.isEmpty()) {
                WGCommandService.lang("rg-not-fount").send(QLib.audience(context.getSender()));
                return;
            }

            if (!WGSpawnAPI.allowTP(rk)) {
                if (region.get().getMembers().contains(player.getUniqueId())) {
                    WGCommandService.lang("rg-tp-disallow").send(QLib.audience(context.getSender()));
                    return;
                }
            }

            var spawn = WGSpawnAPI.getSpawnLocation(rk);

            if (spawn.isEmpty()) {
                WGCommandService.lang("rg-tp-unset").send(QLib.audience(context.getSender()));
                return;
            }

            var s = spawn.get();
            var sx = s.x();
            var sy = s.y();
            var sz = s.z();

            Players.teleport(player, new Location(Bukkit.getWorld(rk.getWorldId()), sx, sy, sz));
            WGCommandService.lang("rg-tp-complete").send(QLib.audience(context.getSender()));
        }

        @Override
        public void suggest(CommandSuggestion suggestion) {
            super.suggest(suggestion);
        }
    }

    @BukkitCommand(name = "allow-tp", permission = "+starlight.worldguard.tp.manage")
    public static final class PlotTeleportPermissionCommand extends RegionRelatedCommand {
        @Override
        public void execute(Player player, ProtectedRegion region, CommandExecution context) {
            if (WGSpawnAPI.toggleAllowTP(RegionKey.fromRegion(player, region))) {
                lang("rg-tp-allow-set").send(QLib.audience(context.getSender()));
            } else {
                lang("rg-tp-disallow-set").send(QLib.audience(context.getSender()));
            }
        }
    }

    @BukkitCommand(name = "set-spawn", permission = "+starlight.worldguard.spawn")
    public static final class PlotSpawnCommand extends RegionRelatedCommand {
        @Override
        public void execute(Player player, ProtectedRegion region, CommandExecution context) {
            var location = player.getLocation();
            var vec = new Vector3d(location.getX(), location.getY(), location.getZ());

            if (WGSpawnAPI.setRegionSpawnLocation(player.getWorld(), region, vec)) {
                lang("rg-spawn-set").send(QLib.audience(context.getSender()));
            } else {
                lang("rg-spawn-invalid").send(QLib.audience(context.getSender()));
            }
        }
    }

    @BukkitCommand(name = "clear-spawn", permission = "+starlight.worldguard.spawn")
    public static final class PlotSpawnClearCommand extends RegionRelatedCommand {
        @Override
        public void execute(Player player, ProtectedRegion region, CommandExecution context) {
            var data = WGPlotInfoService.instance().getData(RegionKey.fromRegion(player, region));

            WGSpawnAPI.SPAWN_LOCATION.set(data, WGSpawnAPI.UNKNOWN_POS);
        }
    }
}
