package org.atcraftmc.starlight.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.util.StandaloneCommand;
import org.atcraftmc.starlight.worldguard.data.RegionKey;

import java.util.Comparator;

@ApplicationModule(id = "wg-custom-name", description = "Allows custom display names for WorldGuard regions")
public final class WGCustomName extends BukkitAbstractModule {
    private final PlotRenameCommand cmd = new PlotRenameCommand();

    static LanguageItem lang(String id) {
        return SLPluginEnvironment.getApplication().language().item("starlight-worldguard:wg-custom-name:" + id);
    }

    @Override
    public void enable() {
        WorldGuardRegionService.COMMAND.registerSubCommand(cmd);

        WGRegionHUD.PIPELINE.addFirst("starlight:custom-name", (world, region, s) -> {
            var key = RegionKey.of(region, world);
            var h = WorldGuardExtraInfoService.getInstance().getDataHandle(key);

            if (h.has("custom-name")) {
                return s.replace("{name}", h.getString("custom-name", "") + "{;}");
            }

            return s;
        });
    }

    @Override
    public void disable() {
        WorldGuardRegionService.COMMAND.unregisterSubCommand(cmd);

        WGRegionHUD.PIPELINE.remove("starlight:custom-name");
    }

    @BukkitCommand(name = "rename", permission = "+starlight.worldguard.rename")
    public static final class PlotRenameCommand extends StandaloneCommand {
        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, "[name...]");
        }

        @Override
        public void execute(CommandExecution context) {
            var player = context.requireSenderAsPlayer();
            var world = player.getWorld();

            var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            var regionManager = container.get(BukkitAdapter.adapt(world));

            if (regionManager == null) {
                lang("no-wg").send(QLib.audience(context.getSender()));
                return;
            }

            var pos = BukkitAdapter.asBlockVector(player.getLocation());
            var regions = regionManager.getApplicableRegions(pos);

            var target = regions.getRegions().stream().max(Comparator.comparingInt(ProtectedRegion::getPriority)).orElse(null);

            if (target == null) {
                lang("no-rg").send(QLib.audience(context.getSender()));
                return;
            }

            if (target.getId().equalsIgnoreCase("__global__")) {
                lang("no-rg").send(QLib.audience(context.getSender()));
                return;
            }

            if (!target.getOwners().getUniqueIds().contains(player.getUniqueId())) {
                lang("rg-not-self").send(QLib.audience(context.getSender()), target.getId());
                return;
            }

            var line = context.requireRemainAsParagraph(0, true);
            var k = RegionKey.of(player, target.getId());

            WorldGuardExtraInfoService.getInstance().getDataHandle(k).editSafe((h) -> h.setString("custom-name", line));

            lang("rg-rename").send(QLib.audience(context.getSender()), line);
        }
    }
}
