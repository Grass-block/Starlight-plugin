package org.atcraftmc.starlight.framework;

import me.gb2022.gluon.module.attachment.AbstractModuleAttachment;
import org.atcraftmc.qlib.command.AbstractCommand;
import org.atcraftmc.starlight.core.command.ModuleCommand;
import org.atcraftmc.starlight.core.command.StarlightCommandManager;
import org.atcraftmc.starlight.framework.module.BukkitModule;

import java.util.HashSet;
import java.util.Set;

public final class ModuleCommandHolder extends AbstractModuleAttachment {
    private final Set<AbstractCommand> commands = new HashSet<>();

    public Set<AbstractCommand> getCommands() {
        return this.commands;
    }

    public void registerCommand(AbstractCommand c) {
        this.commands.add(c);
        if (c instanceof ModuleCommand mc) {
            mc.initContext(this.getModule().getHandle(BukkitModule.class));
        }
        StarlightCommandManager.getInstance().register(c);
    }

    public void unregisterCommand(AbstractCommand c) {
        this.commands.remove(c);
        StarlightCommandManager.getInstance().unregister(c);
    }

    public AbstractCommand getCommand(String id) {
        return StarlightCommandManager.getInstance().getCommand(id);
    }
}
