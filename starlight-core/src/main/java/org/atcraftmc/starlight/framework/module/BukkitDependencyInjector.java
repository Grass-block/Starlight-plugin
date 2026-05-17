package org.atcraftmc.starlight.framework.module;

import com.sun.source.util.Plugin;
import org.atcraftmc.starlight.core.data.BanEntryService;
import org.atcraftmc.starlight.core.data.WaypointService;
import org.atcraftmc.starlight.core.data.region.SimpleRegionService;
import org.atcraftmc.starlight.core.permission.PermissionService;
import org.atcraftmc.starlight.shared.jdbc.flex.FlexibleMapService;
import org.atcraftmc.starlight.shared.JDBCService;
import org.atcraftmc.starlight.util.PluginDependencyInjector;
import org.bukkit.permissions.Permission;

public final class BukkitDependencyInjector extends PluginDependencyInjector {
    public void init() {
        registerInjector(Permission.class, (p, m) -> PermissionService.createPermissionObject(p[0]));
        registerInjector(Plugin.class, (p, m) -> m.owner(Plugin.class));

        registerInjector(SimpleRegionService.class, (a, m) -> {
            var service = new SimpleRegionService(a[1]);
            service.initService(JDBCService.dataSource(a[0]));
            return service;
        });
        registerInjector(WaypointService.class, (a, m) -> {
            var service = new WaypointService(a[1]);
            service.initService(JDBCService.dataSource(a[0]));
            return service;
        });
        registerInjector(BanEntryService.class, (a, m) -> {
            var service = new BanEntryService(a[1]);
            service.initService(JDBCService.dataSource(a[0]));
            return service;
        });
        registerInjector(FlexibleMapService.class, (a, m) -> {
            var service = new FlexibleMapService(a[1]);
            service.initService(JDBCService.dataSource(a[0]));
            return service;
        });
    }
}
