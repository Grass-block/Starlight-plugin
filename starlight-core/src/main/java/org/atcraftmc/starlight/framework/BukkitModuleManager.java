package org.atcraftmc.starlight.framework;

import me.gb2022.modular.ModularApplicationContext;
import me.gb2022.modular.ObjectOperationResult;
import me.gb2022.modular.module.ModuleContainer;
import org.atcraftmc.starlight.framework.module.ModuleServices;

public final class BukkitModuleManager extends PluginModuleManager {
    public BukkitModuleManager(ModularApplicationContext context) {
        super(context);
    }

    @Override
    public void initializeModuleContainer(ModuleContainer handle) {
        super.initializeModuleContainer(handle);
        handle.addAttachment(new ModuleCommandHolder());
    }

    @Override
    public void handlePreEnable(ModuleContainer handle) {
        ModuleServices.onEnable(handle);
    }

    @Override
    public void handlePostDisable(ModuleContainer handle, ObjectOperationResult result) {
        ModuleServices.onDisable(handle);
    }
}
