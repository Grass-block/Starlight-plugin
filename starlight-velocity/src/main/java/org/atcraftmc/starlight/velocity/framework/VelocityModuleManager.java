package org.atcraftmc.starlight.velocity.framework;

import me.gb2022.gluon.ModularApplicationContext;
import org.atcraftmc.starlight.framework.PluginModuleManager;
import org.atcraftmc.starlight.util.PluginAutoRegManager;
import org.atcraftmc.starlight.util.PluginDependencyInjector;

public final class VelocityModuleManager extends PluginModuleManager {
    public VelocityModuleManager(ModularApplicationContext context) {
        super(context);
    }

    @Override
    public PluginDependencyInjector createDependencyInjector() {
        return new VelocityDependencyInjector();
    }

    @Override
    public PluginAutoRegManager createAutoRegManager() {
        return new VelocityAutoRegManager();
    }
}
