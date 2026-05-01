package org.atcraftmc.starlight.data.jdbc;

import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

//todo: auto load dependencies
public interface JDBCDrivers {
    Logger LOGGER = SLPluginEnvironment.createLogger("JDBC-Driver");

    JDBCDriver H2DB = JDBCDriver.registerDriver(new H2Driver());
    JDBCDriver SQLITE = JDBCDriver.registerDriver(new SQLiteDriver());
    JDBCDriver DERBY = JDBCDriver.registerDriver(new DerbyDriver());
    JDBCDriver HSQLDB = JDBCDriver.registerDriver(new HSQLDBDriver());

    JDBCDriver MYSQL = JDBCDriver.registerDriver(new MySQLDriver("mysql"));
    JDBCDriver MARIADB = JDBCDriver.registerDriver(new MySQLDriver("mariadb"));
    JDBCDriver TIDB = JDBCDriver.registerDriver(new MySQLDriver("tidb"));
    JDBCDriver OCEANBASE = JDBCDriver.registerDriver(new MySQLDriver("oceanbase"));
    JDBCDriver POSTGRESQL = JDBCDriver.registerDriver(new PostgreSQLDriver("postgresql"));
    JDBCDriver COCKROACHDB = JDBCDriver.registerDriver(new PostgreSQLDriver("cockroachdb"));
    JDBCDriver ORACLE = JDBCDriver.registerDriver(new OracleDriver("oracle"));
    JDBCDriver SQLSERVER = JDBCDriver.registerDriver(new SQLServerDriver("sqlserver"));

    static void loadAllDrivers() {
        for (var d : JDBCDriver.DRIVERS) {
            d.loadDriver();
        }
    }

    abstract class JDBCDriver {
        private static final List<JDBCDriver> DRIVERS = new ArrayList<>();
        protected final String name;
        protected final ClassLoaderCallback loader;

        public JDBCDriver(String name, ClassLoaderCallback loader) {
            this.name = name;
            this.loader = loader;
        }

        public static JDBCDriver registerDriver(JDBCDriver d) {
            DRIVERS.add(d);
            return d;
        }

        // ---------- resolve ----------
        public static JDBCDriver resolve(DataSource ds) {
            for (JDBCDriver driver : DRIVERS) {
                try {
                    if (driver.matches(ds)) {
                        return driver;
                    }
                } catch (Exception ignored) {
                }
            }
            return new UnknownDriver();
        }

        public final void loadDriver() {
            try {
                this.loader.get();
                LOGGER.info("loading {} -> {} ---- SUCCESS", this.name, this.loader.get().getName());
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                LOGGER.info("loading {} -> {} ---- NOT_FOUND", this.name, e.getMessage());
            } catch (Exception e) {
                LOGGER.warn("loading {} -> ? ---- EXCEPTION({})", this.name, e.getMessage());
                LOGGER.catching(e);
            }
        }

        // ---------- 匹配 ----------
        public abstract boolean matches(DataSource ds);

        // ---------- 是否嵌入 ----------
        public boolean isEmbedded(DataSource ds) {
            return false;
        }

        // ---------- 生命周期 ----------
        public void cleanupAndClose(DataSource ds) {
        }

        public final String getName() {
            return this.name;
        }

        public interface ClassLoaderCallback {
            Class<?> get() throws Exception;
        }
    }
}

final class UnknownDriver extends JDBCDrivers.JDBCDriver {
    public UnknownDriver() {
        super("_custom_", () -> UnknownDriver.class);
    }

    @Override
    public boolean matches(DataSource ds) {
        return false;
    }
}

final class H2Driver extends JDBCDrivers.JDBCDriver {

    public H2Driver() {
        super("h2", () -> Class.forName("org.h2.Driver"));
    }

    @Override
    public boolean matches(DataSource ds) {
        return JDBCUtil.withConnection(ds, conn -> conn.getMetaData().getDatabaseProductName().toLowerCase().contains("h2"));
    }

    @Override
    public boolean isEmbedded(DataSource ds) {
        return JDBCUtil.withConnection(ds, conn -> {
            String url = conn.getMetaData().getURL();
            if (url == null) {
                return false;
            }

            return (url.startsWith("jdbc:h2:mem") || url.startsWith("jdbc:h2:file")) || !url.contains("tcp");
        });
    }

    @Override
    public void cleanupAndClose(DataSource ds) {
        if (isEmbedded(ds)) {
            JDBCUtil.h2_shutdownAndCleanup(ds);
        }
    }
}

final class SQLiteDriver extends JDBCDrivers.JDBCDriver {

    public SQLiteDriver() {
        super("sqlite", () -> Class.forName("org.sqlite.JDBC"));
    }

    @Override
    public boolean matches(DataSource ds) {
        return JDBCUtil.withConnection(ds, conn -> conn.getMetaData().getDatabaseProductName().toLowerCase().contains("sqlite"));
    }

    @Override
    public boolean isEmbedded(DataSource ds) {
        return true;
    }

    @Override
    public void cleanupAndClose(DataSource ds) {
        JDBCUtil.sqlite_vacuum(ds);
    }
}

final class DerbyDriver extends JDBCDrivers.JDBCDriver {

    public DerbyDriver() {
        super("derby", () -> Class.forName("org.apache.derby.jdbc.EmbeddedDriver"));
    }

    @Override
    public boolean matches(DataSource ds) {
        return JDBCUtil.withConnection(ds, conn -> conn.getMetaData().getDatabaseProductName().toLowerCase().contains("derby"));
    }

    @Override
    public boolean isEmbedded(DataSource ds) {
        return true;
    }

    @Override
    public void cleanupAndClose(DataSource ds) {
        // Derby shutdown（经典写法）
        try {
            JDBCUtil.withConnection(ds, conn -> {
                conn.createStatement().execute("SHUTDOWN");
                return null;
            });
        } catch (Exception ignored) {
            // Derby shutdown 会抛异常，属于正常行为
        }
    }
}

final class HSQLDBDriver extends JDBCDrivers.JDBCDriver {

    public HSQLDBDriver() {
        super("hsqldb", () -> Class.forName("org.hsqldb.jdbc.JDBCDriver"));
    }

    @Override
    public boolean matches(DataSource ds) {
        return JDBCUtil.withConnection(ds, conn -> conn.getMetaData().getDatabaseProductName().toLowerCase().contains("hsql"));
    }

    @Override
    public boolean isEmbedded(DataSource ds) {
        return true;
    }

    @Override
    public void cleanupAndClose(DataSource ds) {
        JDBCUtil.withConnection(ds, conn -> {
            conn.createStatement().execute("SHUTDOWN");
            return null;
        });
    }
}

final class MySQLDriver extends JDBCDrivers.JDBCDriver {

    public MySQLDriver(String name) {
        super(name, () -> Class.forName("com.mysql.cj.jdbc.Driver"));
    }

    @Override
    public boolean matches(DataSource ds) {
        return JDBCUtil.withConnection(ds, conn -> {
            String product = conn.getMetaData().getDatabaseProductName().toLowerCase();

            return switch (this.name) {
                case "mysql" -> product.contains("mysql");
                case "mariadb" -> product.contains("mariadb");
                case "tidb" -> product.contains("tidb");
                case "oceanbase" -> product.contains("oceanbase");
                default -> false;
            };
        });
    }
}

final class OracleDriver extends JDBCDrivers.JDBCDriver {

    public OracleDriver(String name) {
        super(name, () -> Class.forName("oracle.jdbc.OracleDriver"));
    }

    @Override
    public boolean matches(DataSource ds) {
        return JDBCUtil.withConnection(ds, conn -> conn.getMetaData().getDatabaseProductName().toLowerCase().contains("oracle"));
    }
}

final class SQLServerDriver extends JDBCDrivers.JDBCDriver {

    public SQLServerDriver(String name) {
        super(name, () -> Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"));
    }

    @Override
    public boolean matches(DataSource ds) {
        return JDBCUtil.withConnection(ds, conn -> {
            String product = conn.getMetaData().getDatabaseProductName().toLowerCase();

            return product.contains("sql server") || product.contains("microsoft");
        });
    }
}

final class PostgreSQLDriver extends JDBCDrivers.JDBCDriver {

    public PostgreSQLDriver(String name) {
        super(name, () -> Class.forName("org.postgresql.Driver"));
    }

    @Override
    public boolean matches(DataSource ds) {
        return JDBCUtil.withConnection(ds, conn -> {
            String product = conn.getMetaData().getDatabaseProductName().toLowerCase();

            return switch (this.name) {
                case "postgresql" -> product.contains("postgresql");
                case "cockroachdb" -> product.contains("cockroach");
                default -> false;
            };
        });
    }
}


