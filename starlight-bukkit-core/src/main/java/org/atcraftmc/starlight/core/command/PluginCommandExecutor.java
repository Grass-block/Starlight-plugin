package org.atcraftmc.starlight.core.command;

import org.atcraftmc.qlib.command.CommandManager;
import org.atcraftmc.qlib.command.execute.CommandExecutor;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.atcraftmc.starlight.StarlightBukkitCore;

public interface PluginCommandExecutor extends CommandExecutor {
    @Override
    default CommandManager getHandle(){
        return StarlightBukkitCore.instance().getCommandManager();
    }
}
