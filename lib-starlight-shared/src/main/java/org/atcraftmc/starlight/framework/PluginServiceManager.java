package org.atcraftmc.starlight.framework;

import me.gb2022.gluon.ModularApplicationContext;
import me.gb2022.gluon.service.Service;
import me.gb2022.gluon.service.ServiceContainer;
import me.gb2022.gluon.service.ServiceManager;
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
