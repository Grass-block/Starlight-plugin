package org.atcraftmc.starlight.worldguard;

import com.google.gson.JsonObject;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.Service;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.shared.JDBCService;
import org.atcraftmc.starlight.shared.jdbc.JDBCData;
import org.atcraftmc.starlight.shared.jdbc.document.NamedDocumentDataService;
import org.atcraftmc.starlight.worldguard.api.RegionKey;
import org.bukkit.Bukkit;

import java.util.ArrayList;

//todo: untested
@ApplicationService(id = "wg-extra-info-v2", impl = WGPlotInfoService.class, export = true)
public final class WGPlotInfoService implements Service {
    @ServiceInject
    public static final ServiceHolder<WGPlotInfoService> INSTANCE = new ServiceHolder<>();
    private final NamedDocumentDataService dataService = new NamedDocumentDataService("sl_plot_info");

    public static WGPlotInfoService instance() {
        return INSTANCE.get();
    }

    @Override
    public void enable() {
        this.dataService.initService(JDBCService.dataSource(JDBCData.SL_LOCAL));
        QLib.task().async().timer("wg-extra-v2:purge-timer", 10, 5 * 60 * 20, this::purge);
    }

    @Override
    public void disable() {
        QLib.task().async().cancel("wg-extra-v2:purge-cancel");
    }

    public void purge() {
        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        var delete = new ArrayList<String>();

        if (container == null) {
            return;
        }

        for (var k : this.dataService.getAllNames()) {
            var rk = RegionKey.fromDatabaseId(k);
            var world = Bukkit.getWorld(rk.getWorldId());

            if (world == null) {
                continue;
            }

            var regionManager = container.get(BukkitAdapter.adapt(world));

            if (regionManager == null) {
                continue;
            }

            if (regionManager.getRegion(rk.getRegionId()) != null) {
                continue;
            }

            delete.add(k);
        }

        this.dataService.delete(delete.toArray(new String[0]));
    }

    public NamedDocumentDataService getStorage() {
        return dataService;
    }

    public JsonObject getData(RegionKey key) {
        return this.dataService.get(key.toDatabaseId());
    }
}
