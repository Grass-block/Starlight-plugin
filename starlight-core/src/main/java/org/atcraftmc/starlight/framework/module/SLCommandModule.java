package org.atcraftmc.starlight.framework.module;

import org.atcraftmc.qlib.command.AbstractCommand;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.starlight.framework.ModuleCommandHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.atcraftmc.starlight.core.command.ModuleCommand;
import org.atcraftmc.starlight.core.command.PluginCommandExecutor;

public abstract class SLCommandModule extends BukkitAbstractModule implements PluginCommandExecutor {
    private final AbstractCommand commandAdapter = new AdapterCommand<>(this);

    @Override
    public void enable() throws Exception {
        this.handle().getAttachment(ModuleCommandHolder.class).registerCommand(this.commandAdapter);
    }

    @Override
    public void disable() throws Exception {
        this.handle().getAttachment(ModuleCommandHolder.class).unregisterCommand(this.commandAdapter);
    }

    public Command getCoveredCommand() {
        return null;
    }

    public final Command getCovered() {
        return this.commandAdapter.getCovered();
    }

    public final void sendExceptionMessage(CommandSender sender) {
        this.commandAdapter.sendExceptionMessage(sender);
    }

    public final void sendPermissionMessage(CommandSender sender) {
        this.commandAdapter.sendPermissionMessage(sender, "(ServerOperator)");
    }

    public final void sendPlayerOnlyMessage(CommandSender sender) {
        this.commandAdapter.sendPlayerOnlyMessage(sender);
    }

    public static final class AdapterCommand<T extends SLCommandModule> extends ModuleCommand<T> {
        public AdapterCommand(T module) {
            super(module);
            this.init(module);
            this.setExecutor(module);
        }

        @Override
        public void init(T module) {
            this.setExecutor(module);
        }

        @Override
        public BukkitCommand getDescriptor() {
            return this.getModule().getClass().getAnnotation(BukkitCommand.class);
        }

        @Override
        public Command getCoveredCommand() {
            return this.getModule().getCoveredCommand();
        }
    }
}
