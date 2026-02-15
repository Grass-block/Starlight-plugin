package org.atcraftmc.starlight.framework.module;

import me.gb2022.gluon.ModularApplicationContext;
import me.gb2022.gluon.ObjectOperationResult;
import me.gb2022.gluon.module.ModuleContainer;
import org.atcraftmc.qlib.command.AbstractCommand;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.api.event.ModuleEvent;
import org.atcraftmc.starlight.foundation.command.CommandProvider;
import org.atcraftmc.starlight.foundation.command.ModuleCommand;
import org.atcraftmc.starlight.foundation.command.StarlightCommandManager;
import org.atcraftmc.starlight.foundation.platform.BukkitUtil;
import org.atcraftmc.starlight.framework.ModuleCommandHolder;
import org.atcraftmc.starlight.framework.PluginModuleManager;
import org.atcraftmc.starlight.util.PluginAutoRegManager;
import org.atcraftmc.starlight.util.PluginDependencyInjector;

public final class BukkitModuleManager extends PluginModuleManager {
    public BukkitModuleManager(ModularApplicationContext context) {
        super(context);
    }

    @SuppressWarnings({"rawtypes"})
    public static void initCommands(ModuleContainer handle) {
        var module = handle.getHandle(BukkitModule.class);

        if (!module.getClass().isAnnotationPresent(CommandProvider.class)) {
            return;
        }

        handle.getAttachment(ModuleCommandHolder.class).getCommands().clear();

        var annotation = module.getClass().getAnnotation(CommandProvider.class);

        for (Class<? extends AbstractCommand> commandClass : annotation.value()) {
            AbstractCommand cmd;
            try {
                cmd = commandClass.getConstructor().newInstance();
                if (cmd instanceof ModuleCommand c) {
                    c.initContext(handle.getHandle(BukkitModule.class));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            StarlightCommandManager.getInstance().register(cmd);
            handle.getAttachment(ModuleCommandHolder.class).getCommands().add(cmd);
        }
    }

    @Override
    public void initializeModuleContainer(ModuleContainer handle) {
        super.initializeModuleContainer(handle);
        handle.addAttachment(new ModuleCommandHolder());
    }

    @Override
    public void handlePreEnable(ModuleContainer handle) {
        super.handlePreEnable(handle);
        initCommands(handle);

        try {
            BukkitUtil.callEvent(new ModuleEvent.PreEnable(handle));
        } catch (Throwable e) {
            this.logger.error("Failed to dispatch event ModuleEvent/PreEnable");
            this.logger.catching(e);
        }
    }

    @Override
    public void handlePostEnable(ModuleContainer handle, ObjectOperationResult result) {
        try {
            BukkitUtil.callEvent(new ModuleEvent.Enable(handle, result));
        } catch (Throwable e) {
            this.logger.error("Failed to dispatch event ModuleEvent/Enable");
            this.logger.catching(e);
        }
    }

    @Override
    public PluginDependencyInjector createDependencyInjector() {
        return new BukkitDependencyInjector();
    }

    @Override
    public PluginAutoRegManager createAutoRegManager() {
        return new BukkitAutoRegManager();
    }

    @Override
    public void handlePostDisable(ModuleContainer handle, ObjectOperationResult result) {
        super.handlePostDisable(handle, result);

        if (handle.getReference().getDeclaredAnnotation(CommandProvider.class) != null) {
            for (AbstractCommand cmd : handle.getAttachment(ModuleCommandHolder.class).getCommands()) {
                Starlight.instance().getCommandManager().unregister(cmd);
            }
        }

        try {
            BukkitUtil.callEvent(new ModuleEvent.Disable(handle, result));
        } catch (Throwable e) {
            this.logger.error("Failed to dispatch event ModuleEvent/Disable");
            this.logger.catching(e);
        }
    }

    @Override
    public void handlePreDisable(ModuleContainer handle) {
        try {
            BukkitUtil.callEvent(new ModuleEvent.PreDisable(handle));
        } catch (Throwable e) {
            this.logger.error("Failed to dispatch event ModuleEvent/PreDisable");
            this.logger.catching(e);
        }
    }
}
