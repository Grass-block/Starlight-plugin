package org.atcraftmc.starlight.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.Service;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.util.StandaloneCommand;
import org.atcraftmc.starlight.worldguard.api.RegionKey;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationService(id = "wg-command")
public interface WGCommandService extends Service {
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

    static LanguageItem lang(String id) {
        return SLPluginEnvironment.getApplication().language().item("starlight-worldguard:wg-command:" + id);
    }

    static void suggestRegions(CommandSuggestion suggestion,int ptr){
        suggestion.suggest(ptr, WGRegionService.getAllKeys().stream().map(RegionKey::toSearchId).collect(Collectors.toSet()));
    }

    static Optional<ProtectedRegion> getManageableRegion(CommandExecution context) {
        var player = context.requireSenderAsPlayer();
        var world = player.getWorld();

        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        var regionManager = container.get(BukkitAdapter.adapt(world));

        if (regionManager == null) {
            lang("no-wg").send(QLib.audience(context.getSender()));
            return Optional.empty();
        }

        var pos = BukkitAdapter.asBlockVector(player.getLocation());
        var regions = regionManager.getApplicableRegions(pos);

        var target = regions.getRegions().stream().max(Comparator.comparingInt(ProtectedRegion::getPriority)).orElse(null);

        if (target == null) {
            lang("no-rg").send(QLib.audience(context.getSender()));
            return Optional.empty();
        }

        if (target.getId().equalsIgnoreCase("__global__")) {
            lang("no-rg").send(QLib.audience(context.getSender()));
            return Optional.empty();
        }

        if (!target.getOwners().getUniqueIds().contains(player.getUniqueId())) {
            lang("rg-not-self").send(QLib.audience(context.getSender()), target.getId());
            return Optional.empty();
        }

        return Optional.of(target);
    }

    @BukkitCommand(name = "plot")
    final class PlotCommand extends StandaloneCommand {
    }
}
