package org.atcraftmc.starlight.util;

import me.gb2022.gluon.LogProvider;
import me.gb2022.gluon.module.AppModule;
import me.gb2022.gluon.module.ModuleContainer;
import me.gb2022.gluon.service.ServiceContainer;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;

public final class SLLogProvider implements LogProvider {

    @Override
    public Logger createLogger(String s) {
        return SLPluginEnvironment.createLogger(s);
    }

    @Override
    public Logger createLogger(ModuleContainer moduleContainer) {
        return SLPluginEnvironment.createLogger(moduleContainer.getReference().getSimpleName());
    }

    @Override
    public Logger createLogger(ServiceContainer serviceContainer) {
        return SLPluginEnvironment.createLogger(serviceContainer.getHandle().getSimpleName());
    }
}
