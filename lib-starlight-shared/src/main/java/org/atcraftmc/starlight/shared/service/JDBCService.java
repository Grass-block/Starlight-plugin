package org.atcraftmc.starlight.shared.service;

import me.gb2022.gluon.service.*;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.data.JDBCPlayerData;
import org.atcraftmc.starlight.data.jdbc.JDBCDatasourceManager;
import org.atcraftmc.starlight.data.jdbc.JDBCDrivers;
import org.atcraftmc.starlight.data.jdbc.service.JDBCDataService;
import org.atcraftmc.starlight.data.jdbc.service.TagMap;
import org.atcraftmc.starlight.data.jdbc.source.JDBCDataSource;
import org.atcraftmc.starlight.shared.Configurations;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationService(id = "jdbc", layer = ServiceLayer.FRAMEWORK, impl = JDBCService.class)
public final class JDBCService implements Service {
    @ServiceInject
    public static final ServiceHolder<JDBCService> INSTANCE = new ServiceHolder<>();
    public static final Logger LOGGER = SLPluginEnvironment.createLogger("JDBCService");
    public static final Map<String, JDBCDatabase> REGISTRY = new HashMap<>();

    public static final String SL_SHARED = "starlight:shared";
    public static final String SL_LOCAL = "starlight:default";

    private final JDBCDatasourceManager datasourceManager = new JDBCDatasourceManager();
    private final Set<JDBCDataService> instances = new HashSet<>();
    private final AtomicLong ticks = new AtomicLong(0L);//second

    public static JDBCService getInstance() {
        return INSTANCE.get();
    }

    public static Optional<JDBCDatabase> getDB(String id) {
        return getInstance().db(id);
    }

    public static JDBCDataSource dataSource(String id) {
        return getInstance().getDataSource(id).orElseThrow();
    }

    public static Connection connection(String id) {
        try {
            return getInstance().getSingleConnection(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerInstance(JDBCDataService instance) {
        this.instances.add(instance);
    }

    public Optional<JDBCDatabase> db(String id) {
        return Optional.of(REGISTRY.computeIfAbsent(id, (k) -> new DataSourceDBHandle(getDataSource(id).orElseThrow())));
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

        Configurations.groupedYML("database", Set.of("database/sl-default.yml", "database/sl-shared.yml"))
                .forEach((k, d) -> this.datasourceManager.create(d, this));

        JDBCData.PLAYER_LOCAL.init(getDataSource(JDBCService.SL_LOCAL).orElseThrow(), this);
        JDBCData.PLAYER_SHARED.init(getDataSource(JDBCService.SL_SHARED).orElseThrow(), this);

        try {
            JDBCPlayerData.PLAYER_LOCAL.init(db(JDBCService.SL_LOCAL).orElseThrow());
            JDBCPlayerData.PLAYER_SHARED.init(db(JDBCService.SL_SHARED).orElseThrow());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disable() throws Exception {
        this.instances.forEach(JDBCDataService::onClosing);
        REGISTRY.forEach((k, v) -> v.close());
        this.datasourceManager.getDataSources().forEach((k, v) -> v.close());
    }

    public JDBCDatasourceManager getDatasourceManager() {
        return datasourceManager;
    }

    public Optional<JDBCDataSource> getDataSource(String id) {
        return this.datasourceManager.getDataSource(id);
    }

    public Connection getSingleConnection(String id) throws SQLException {
        return getDataSource(id).orElseThrow().getConnection();
    }

    public interface JDBCDatabase {

        default void open() {
        }

        default void close() {
        }

        TagMap getFlexibleMetaMap();

        void recordColumnRegisterFor(String table, String column) throws SQLException;

        boolean isColumnRegistered(String table, String column);

        Connection getConnection();
    }

    private static final class DataSourceDBHandle implements JDBCDatabase {
        private final JDBCDataSource dataSource;
        private Connection conn;

        public DataSourceDBHandle(JDBCDataSource dataSource) {
            this.dataSource = dataSource;
            this.open();
        }

        @Override
        public void open() {
            try {
                this.conn = dataSource.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() {
            try {
                this.conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public TagMap getFlexibleMetaMap() {
            return this.dataSource.getFlexibleMetaMap();
        }

        @Override
        public void recordColumnRegisterFor(String table, String column) throws SQLException {
            this.dataSource.recordColumnRegisterFor(table, column);
        }

        @Override
        public boolean isColumnRegistered(String table, String column) {
            return this.dataSource.isColumnRegistered(table, column);
        }

        @Override
        public Connection getConnection() {
            return this.conn;
        }
    }
}
