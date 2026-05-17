package org.atcraftmc.starlight.shared;

import me.gb2022.gluon.service.*;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.config.Configurations;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.data.jdbc.JDBCDatabase;
import org.atcraftmc.starlight.data.jdbc.JDBCDatasourceManager;
import org.atcraftmc.starlight.data.jdbc.JDBCDrivers;
import org.atcraftmc.starlight.shared.jdbc.JDBCData;
import org.atcraftmc.starlight.shared.jdbc.JDBCDataService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationService(id = "jdbc", layer = ServiceLayer.FRAMEWORK, impl = JDBCService.class)
public final class JDBCService implements Service {
    public static final Logger LOGGER = SLPluginEnvironment.createLogger("JDBCService");

    @ServiceInject
    public static final ServiceHolder<JDBCService> INSTANCE = new ServiceHolder<>();

    private final JDBCDatasourceManager datasourceManager = new JDBCDatasourceManager();
    private final Set<JDBCDataService> instances = new HashSet<>();
    private final AtomicLong ticks = new AtomicLong(0L);//second

    public static JDBCService getInstance() {
        return INSTANCE.get();
    }

    public static JDBCDatabase dataSource(String id) {
        return getInstance().getDataSource(id).orElseThrow();
    }

    public void registerInstance(JDBCDataService instance) {
        this.instances.add(instance);
    }

    public void tick() {
        this.ticks.incrementAndGet();
        for (var data : this.instances) {
            data.tick(this.ticks);
        }
    }

    @Override
    public void enable() throws Exception {
        JDBCDrivers.loadAllDrivers();

        var configs = Configurations.groupedYML("database", Set.of("database/sl-default.yml", "database/sl-shared.yml"));

        configs.forEach((k, d) -> this.datasourceManager.create(d, this));

        JDBCData.PLAYER_LOCAL.initService(getDataSource(JDBCData.SL_LOCAL).orElseThrow());
        JDBCData.PLAYER_LOCAL_L.initService(getDataSource(JDBCData.SL_LOCAL).orElseThrow());
        JDBCData.PLAYER_SHARED.initService(getDataSource(JDBCData.SL_SHARED).orElseThrow());
        JDBCData.PLAYER_SHARED_L.initService(getDataSource(JDBCData.SL_SHARED).orElseThrow());
    }

    @Override
    public void disable() throws Exception {
        this.instances.forEach(JDBCDataService::onClosing);
        this.datasourceManager.getDataSources().forEach((k, v) -> v.close());
    }

    public JDBCDatasourceManager getDatasourceManager() {
        return datasourceManager;
    }

    public Optional<JDBCDatabase> getDataSource(String id) {
        return this.datasourceManager.getDataSource(id);
    }
}
