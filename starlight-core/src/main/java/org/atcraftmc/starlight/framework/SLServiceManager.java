package org.atcraftmc.starlight.framework;

import me.gb2022.modular.pack.IPackage;
import me.gb2022.modular.service.Service;
import me.gb2022.modular.service.ServiceLayer;
import org.atcraftmc.qlib.config.ConfigContainer;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.shared.service.AbstractServiceManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import java.util.HashMap;

public final class SLServiceManager extends AbstractServiceManager {
    public static final SLServiceManager INSTANCE = new SLServiceManager();

    static <I extends Service> Class<I> get(String id, Class<Class<I>> type) {
        return INSTANCE.getService(id, type);
    }

    static <I extends Service> void register(Class<I> service) {
        INSTANCE.registerService(null, service);
    }

    static void unregister(Class<? extends Service> service) {
        INSTANCE.unregisterService(service);
    }

    public static void unregisterAll(ServiceLayer layer) {
        INSTANCE.unregisterAllServices(layer);
    }


    public static HashMap<String, Class<? extends Service>> all() {
        return (INSTANCE).services;
    }


    @Override
    public <E extends Service> void exportService(E object, Class<E> type) {
        Bukkit.getServicesManager().register(type, object, Starlight.instance(), ServicePriority.High);
    }


    @Override
    public void unregisterExportedService(Class<? extends Service> type) {
        Bukkit.getServicesManager().unregister(type, Starlight.instance());
    }

    @Override
    public <E extends Service> E createImplementation(IPackage<?, ?, Service> p, Class<E> service, String id) {
        return AbstractServiceManager.createImplementation(
                service,
                ConfigContainer.getInstance().entry(p == null ? "starlight-core" : p.getId(), id)
        );
    }
}
