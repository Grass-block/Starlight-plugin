package org.atcraftmc.starlight.worldguard.api;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.starlight.util.StandaloneCommand;
import org.atcraftmc.starlight.worldguard.WGCommandService;
import org.bukkit.entity.Player;

public abstract class RegionRelatedCommand extends StandaloneCommand {
    @Override
    public final void execute(CommandExecution context) {
        var t = WGCommandService.getManageableRegion(context);

        if (t.isEmpty()) {
            return;
        }

        var player = context.requireSenderAsPlayer();
        var target = t.get();

        this.execute(player, target, context);
    }

    public abstract void execute(Player player, ProtectedRegion region, CommandExecution context);
}
