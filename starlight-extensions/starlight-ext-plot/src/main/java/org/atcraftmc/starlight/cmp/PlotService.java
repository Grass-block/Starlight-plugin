package org.atcraftmc.starlight.cmp;

import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.starlight.cmp.data.BuildArea;
import org.atcraftmc.starlight.core.data.region.AbstractRegionService;
import org.atcraftmc.starlight.framework.BukkitService;
import org.bson.BsonDocument;
import org.joml.Vector3d;

import java.util.UUID;

//todo:initContext
@ApplicationService(id = "plot-service")
public interface PlotService extends BukkitService {
    ServiceHolder<PlotServiceHandle> HANDLE = new ServiceHolder<>();

    @ServiceInject
    static void start() {
        HANDLE.set(new PlotServiceHandle());
    }

    static PlotServiceHandle getInstance() {
        return HANDLE.get();
    }

    class PlotServiceHandle extends AbstractRegionService<BuildArea> implements PlotService {
        public PlotServiceHandle() {
            super("sl_plots");
        }

        @Override
        public BuildArea create(UUID id, UUID owner, String name, String world, Vector3d p1, Vector3d p2, BsonDocument meta) {
            return new BuildArea(id, owner, name, world, p1, p2, meta);
        }
    }
}
