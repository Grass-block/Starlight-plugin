package org.atcraftmc.starlight.framework;

import me.gb2022.gluon.ModularApplicationContext;
import me.gb2022.gluon.service.Service;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public final class BukkitServiceManager extends PluginServiceManager {
    public BukkitServiceManager(ModularApplicationContext context) {
        super(context);
    }

    @Override
    public <E extends Service> void exportService(E object, Class<E> type) {
        Bukkit.getServicesManager().register(type, object, Starlight.instance(), ServicePriority.High);
    }

    @Override
    public void unregisterExportedService(Class<? extends Service> type) {
        Bukkit.getServicesManager().unregister(type, Starlight.instance());
    }
}
