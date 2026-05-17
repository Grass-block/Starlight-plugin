package org.atcraftmc.starlight.data.jdbc.source;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.data.jdbc.JDBCDrivers;
import org.atcraftmc.starlight.data.jdbc.service.TagMapService;
import org.atcraftmc.starlight.shared.service.JDBCService;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

public abstract class JDBCDataSource extends WrappedDataSource {
    Logger LOGGER = SLPluginEnvironment.createLogger("JDBC-DataSource");

    private JDBCDrivers.JDBCDriver driver = null;

    public JDBCDataSource(DataSource dataSource) {
        super(dataSource);
    }

    public static JDBCDataSource simple(DataSource dataSource,JDBCService service) {
        return new SimpleDatasource(dataSource,service);
    }

    public static JDBCDataSource phantom(JDBCDataSource dataSource) {
        return new PhantomDatasource(dataSource);
    }

    public abstract SqlSessionFactory getSessionFactory();

    public abstract TagMapService getFlexibleMetaMap();

    public abstract void recordColumnRegisterFor(String table, String column) throws SQLException;

    public abstract boolean isColumnRegistered(String table, String column);

    public void close() {
    }

    public JDBCDrivers.JDBCDriver getDriver() {
        if (this.driver == null) {
            this.driver = JDBCDrivers.JDBCDriver.resolve(this);
        }
        return driver;
    }

    private static final class SimpleDatasource extends JDBCDataSource {
        private final JDBCService service;
        private final Map<String, Set<String>> columns = new HashMap<>();
        private final SqlSessionFactory sessionFactory;
        private final TagMapService flexibleMetaMap;

        public SimpleDatasource(DataSource dataSource, JDBCService service) {
            super(dataSource);
            this.service = service;

            var configuration = new MybatisConfiguration();
            var builder = new MybatisSqlSessionFactoryBuilder();
            var env = new Environment("default", new JdbcTransactionFactory(), dataSource);

            configuration.setEnvironment(env);

            try {
                this.sessionFactory = builder.build(configuration);
                this.flexibleMetaMap = new TagMapService("_sl_flexible_meta");
                this.flexibleMetaMap.init(this,this.service);
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
        public void close() {
            var driver = getDriver();

            if (driver != null && driver.isEmbedded(this)) {
                LOGGER.info("{} - Vacuum completed.",driver.getName());
                driver.cleanupAndClose(this);
            }

            if(this.dataSource instanceof Closeable c) {
                try {
                    c.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static final class PhantomDatasource extends JDBCDataSource {
        private final JDBCDataSource phantomDatasource;

        public PhantomDatasource(JDBCDataSource dataSource) {
            super(dataSource);
            this.phantomDatasource = dataSource;
        }

        @Override
        public SqlSessionFactory getSessionFactory() {
            return phantomDatasource.getSessionFactory();
        }

        @Override
        public TagMapService getFlexibleMetaMap() {
            return phantomDatasource.getFlexibleMetaMap();
        }

        @Override
        public void recordColumnRegisterFor(String table, String column) throws SQLException {
            phantomDatasource.recordColumnRegisterFor(table, column);
        }

        @Override
        public boolean isColumnRegistered(String table, String column) {
            return phantomDatasource.isColumnRegistered(table, column);
        }
    }
}
