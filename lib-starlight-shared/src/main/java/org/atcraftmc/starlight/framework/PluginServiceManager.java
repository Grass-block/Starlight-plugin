package org.atcraftmc.starlight.framework;

import me.gb2022.modular.ModularApplicationContext;
import me.gb2022.modular.service.Service;
import me.gb2022.modular.service.ServiceContainer;
import me.gb2022.modular.service.ServiceManager;
import org.atcraftmc.qlib.PluginConcept;
import org.atcraftmc.qlib.config.ConfigContainer;

public class PluginServiceManager extends ServiceManager {
    public PluginServiceManager(ModularApplicationContext context) {
        super(context);
    }

    @Override
    public Service createImplementation(ServiceContainer c, Class<Service> clazz) {
        var cid = this.context().holder(PluginConcept.class).id();
        var cfg = ConfigContainer.getInstance().entry(c.owner() == null ? cid : c.owner().meta().id(), c.meta().id());

        return createImplementation(clazz, cfg);
    }
}
