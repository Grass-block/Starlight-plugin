package org.atcraftmc.starlight.shared.service;

import me.gb2022.gluon.service.*;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.data.jdbc.JDBCDatasourceManager;
import org.atcraftmc.starlight.data.jdbc.JDBCDrivers;
import org.atcraftmc.starlight.data.jdbc.service.TagMap;
import org.atcraftmc.starlight.data.jdbc.source.JDBCDataSource;
import org.atcraftmc.starlight.shared.Configurations;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ApplicationService(id = "jdbc", layer = ServiceLayer.FOUNDATION, impl = JDBCService.ServiceImpl.class)
public interface JDBCService extends Service {
    @ServiceInject
    ServiceHolder<JDBCService> INSTANCE = new ServiceHolder<>();
    Logger LOGGER = SLPluginEnvironment.createLogger("JDBCService");
    Map<String, JDBCDatabase> REGISTRY = new HashMap<>();
    String SL_SHARED = "starlight:shared";
    String SL_LOCAL = "starlight:default";

    static JDBCService getInstance() {
        return INSTANCE.get();
    }

    static Optional<JDBCDatabase> getDB(String id) {
        return Optional.of(REGISTRY.computeIfAbsent(id, (k) -> new DataSourceDBHandle(dataSource(k))));
    }

    static JDBCDataSource dataSource(String id) {
        return getInstance().getDataSource(id).orElseThrow();
    }

    static Connection connection(String id) {
        try {
            return getInstance().getSingleConnection(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Optional<JDBCDataSource> getDataSource(String id);

    Connection getSingleConnection(String id) throws SQLException;

    interface JDBCDatabase {

        default void open() {
        }

        default void close() {
        }

        TagMap getFlexibleMetaMap();

        void recordColumnRegisterFor(String table, String column) throws SQLException;

        boolean isColumnRegistered(String table, String column);

        Connection getConnection();
    }

    final class ServiceImpl implements JDBCService {
        private final JDBCDatasourceManager datasourceManager = new JDBCDatasourceManager();

        @Override
        public void enable() throws Exception {
            JDBCDrivers.loadAllDrivers();

            Configurations.groupedYML("database", Set.of("database/sl-default.yml", "database/sl-shared.yml"))
                    .forEach((k, d) -> this.datasourceManager.create(d));
        }

        @Override
        public void disable() throws Exception {
            REGISTRY.forEach((k, v) -> v.close());
            this.datasourceManager.getDataSources().forEach((k, v) -> v.close());
        }

        public JDBCDatasourceManager getDatasourceManager() {
            return datasourceManager;
        }

        @Override
        public Optional<JDBCDataSource> getDataSource(String id) {
            return this.datasourceManager.getDataSource(id);
        }

        @Override
        public Connection getSingleConnection(String id) throws SQLException {
            return getDataSource(id).orElseThrow().getConnection();
        }
    }

    final class DataSourceDBHandle implements JDBCDatabase {
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
