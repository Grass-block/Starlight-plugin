package org.atcraftmc.starlight.worldguard;

import com.google.gson.JsonObject;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.Service;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.shared.JDBCService;
import org.atcraftmc.starlight.shared.jdbc.JDBCData;
import org.atcraftmc.starlight.shared.jdbc.document.NamedDocumentDataService;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;

@ApplicationService(id = "wg-extra-info-v2", impl = WGExtraInfoServiceV2.class, export = true)
public final class WGExtraInfoServiceV2 implements Service {
    @ServiceInject
    public static final ServiceHolder<WGExtraInfoServiceV2> INSTANCE = new ServiceHolder<>();
    private final NamedDocumentDataService dataService = new NamedDocumentDataService("sl_plot_info");

    public static String key(ProtectedRegion region, World world) {
        return world.getName() + "/" + region.getId();
    }

    public static WGExtraInfoServiceV2 instance() {
        return INSTANCE.get();
    }

    @Override
    public void enable() {
        this.dataService.initService(JDBCService.dataSource(JDBCData.SL_LOCAL));
        QLib.task().async().timer("wg-extra-v2:purge-timer", 10, 5 * 60 * 20, this::purge);
    }

    @Override
    public void disable() throws Exception {
        QLib.task().async().cancel("wg-extra-v2:purge-cancel");
    }

    public void purge() {
        var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        var delete = new ArrayList<String>();

        if (container == null) {
            return;
        }

        for (var k : this.dataService.getAllNames()) {
            var split = k.split("/");
            var world = Bukkit.getWorld(split[0]);

            if (world == null) {
                continue;
            }


            var regionManager = container.get(BukkitAdapter.adapt(world));

            if (regionManager == null) {
                continue;
            }

            if (regionManager.getRegion(split[1]) != null) {
                continue;
            }

            delete.add(k);
        }

        this.dataService.delete(delete.toArray(new String[0]));
    }

    public NamedDocumentDataService getStorage() {
        return dataService;
    }

    public JsonObject getData(ProtectedRegion r, World w) {
        return this.dataService.get(key(r, w));
    }
}
