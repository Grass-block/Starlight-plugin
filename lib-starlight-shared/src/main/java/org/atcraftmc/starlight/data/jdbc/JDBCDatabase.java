package org.atcraftmc.starlight.data.jdbc;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.data.jdbc.source.WrappedDataSource;
import org.atcraftmc.starlight.shared.JDBCService;
import org.atcraftmc.starlight.shared.jdbc.TagMapService;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class JDBCDatabase extends WrappedDataSource {
    public static final Logger LOGGER = SLPluginEnvironment.createLogger("JDBC-DataSource");

    protected final JDBCService context;
    protected JDBCDrivers.JDBCDriver driver = null;

    public JDBCDatabase(DataSource dataSource, JDBCService context) {
        super(dataSource);
        this.context = context;
    }

    public static JDBCDatabase simple(DataSource dataSource, JDBCService service) {
        return new SimpleDatasource(dataSource, service);
    }

    public static JDBCDatabase phantom(JDBCDatabase dataSource, JDBCService service) {
        return new PhantomDatasource(dataSource, service);
    }

    public abstract SqlSessionFactory getSessionFactory();

    public abstract TagMapService getFlexibleMetaMap();

    public abstract void recordColumnRegisterFor(String table, String column) throws SQLException;

    public abstract boolean isColumnRegistered(String table, String column);

    public abstract Connection getSharedConnection();

    public void close() {
    }

    public JDBCDrivers.JDBCDriver getDriver() {
        if (this.driver == null) {
            this.driver = JDBCDrivers.JDBCDriver.resolve(this);
        }
        return driver;
    }

    public JDBCService getContext() {
        return this.context;
    }

    private static final class SimpleDatasource extends JDBCDatabase {
        private final Map<String, Set<String>> columns = new ConcurrentHashMap<>();
        private final SqlSessionFactory sessionFactory;
        private final TagMapService flexibleMetaMap;
        private Connection sharedConnection = null;

        public SimpleDatasource(DataSource dataSource, JDBCService service) {
            super(dataSource, service);

            var configuration = new MybatisConfiguration();
            var builder = new MybatisSqlSessionFactoryBuilder();
            var env = new Environment("default", new JdbcTransactionFactory(), dataSource);

            configuration.setEnvironment(env);

            try {
                this.sessionFactory = builder.build(configuration);
                this.flexibleMetaMap = new TagMapService("_sl_flexible_meta");
                this.flexibleMetaMap.initService(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public SqlSessionFactory getSessionFactory() {
            return this.sessionFactory;
        }

        @Override
        public TagMapService getFlexibleMetaMap() {
            return this.flexibleMetaMap;
        }

        private Set<String> getColumnMetaFor(String table) {
            var uuid = UUID.nameUUIDFromBytes(table.getBytes(StandardCharsets.UTF_8));
            return this.columns.computeIfAbsent(table, (s) -> {
                try {
                    return new HashSet<>(this.flexibleMetaMap.get(uuid));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        @Override
        public void recordColumnRegisterFor(String table, String column) throws SQLException {
            var uuid = UUID.nameUUIDFromBytes(table.getBytes(StandardCharsets.UTF_8));
            getColumnMetaFor(table).add(column);
            this.flexibleMetaMap.add(uuid, column);
        }

        @Override
        public boolean isColumnRegistered(String table, String column) {
            return getColumnMetaFor(table).contains(column);
        }

        @Override
        public Connection getSharedConnection() {
            if (this.sharedConnection == null) {
                try {
                    this.sharedConnection = getConnection();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            return this.sharedConnection;
        }

        @Override
        public void close() {
            var driver = getDriver();

            if (this.sharedConnection != null) {
                try {
                    this.sharedConnection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                if (driver != null && driver.isEmbedded(this)) {
                    LOGGER.info("{} - Vacuum completed.", driver.getName());
                    driver.cleanupAndClose(this);
                }
            } catch (Exception e) {
                LOGGER.warn("Unable to free database, this may cause BUG.");
            }

            if (this.dataSource instanceof Closeable c) {
                try {
                    c.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static final class PhantomDatasource extends JDBCDatabase {
        private final JDBCDatabase delegate;

        public PhantomDatasource(JDBCDatabase dataSource, JDBCService service) {
            super(dataSource, service);
            this.delegate = dataSource;
        }

        @Override
        public SqlSessionFactory getSessionFactory() {
            return delegate.getSessionFactory();
        }

        @Override
        public TagMapService getFlexibleMetaMap() {
            return delegate.getFlexibleMetaMap();
        }

        @Override
        public void recordColumnRegisterFor(String table, String column) throws SQLException {
            delegate.recordColumnRegisterFor(table, column);
        }

        @Override
        public boolean isColumnRegistered(String table, String column) {
            return delegate.isColumnRegistered(table, column);
        }

        @Override
        public Connection getSharedConnection() {
            return delegate.getSharedConnection();
        }
    }
}
