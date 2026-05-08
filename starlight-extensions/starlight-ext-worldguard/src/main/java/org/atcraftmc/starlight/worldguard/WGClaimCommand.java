package org.atcraftmc.starlight.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.util.StandaloneCommand;

import java.util.Comparator;


@ApplicationModule(id = "wg-claim")
public final class WGClaimCommand extends BukkitAbstractModule {

    static LanguageItem lang(String id) {
        return SLPluginEnvironment.getPlugin().language().item("starlight-worldguard:wg-claim:" + id);
    }

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requirePlugin("WorldGuard");
    }

    @Override
    public void enable() throws Exception {
        WorldGuardRegionService.COMMAND.registerSubCommand(new ClaimCommand());
        WorldGuardRegionService.COMMAND.registerSubCommand(new UnClaimCommand());
    }

    @Override
    public void disable() throws Exception {
        WorldGuardRegionService.COMMAND.unregisterSubCommand("claim");
        WorldGuardRegionService.COMMAND.unregisterSubCommand("unclaim");
    }

    @BukkitCommand(name = "unclaim", permission = "+starlight.worldguard.claim")
    public static class UnClaimCommand extends StandaloneCommand {

        @Override
        public void suggest(CommandSuggestion suggestion) {
            super.suggest(suggestion);
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

            var owners = target.getOwners();
            owners.removePlayer(player.getUniqueId());
            target.setOwners(owners);

            try {
                regionManager.save();
                lang("rg-unclaim").send(QLib.audience(context.getSender()), target.getId());
            } catch (StorageException e) {
                lang("cmd-error").send(QLib.audience(context.getSender()));
                e.printStackTrace();
            }
        }
    }

    @BukkitCommand(name = "claim", permission = "+starlight.worldguard.claim")
    public static class ClaimCommand extends StandaloneCommand {
        @Override
        public void suggest(CommandSuggestion suggestion) {
            super.suggest(suggestion);
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

            if (target.getOwners().getUniqueIds().contains(player.getUniqueId())) {
                lang("rg-claimed-self").send(QLib.audience(context.getSender()), target.getId());
                return;
            }

            if (!target.getOwners().getUniqueIds().isEmpty()) {
                lang("rg-claimed").send(QLib.audience(context.getSender()), target.getId());
                return;
            }

            var owners = target.getOwners();
            owners.addPlayer(player.getUniqueId());
            target.setOwners(owners);

            try {
                regionManager.save();
                lang("rg-claim").send(QLib.audience(context.getSender()), target.getId());
            } catch (StorageException e) {
                lang("cmd-error").send(QLib.audience(context.getSender()));
                e.printStackTrace();
            }
        }
    }
}
