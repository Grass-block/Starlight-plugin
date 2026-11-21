package me.gb2022.commons.jdbc.db;

import me.gb2022.commons.jdbc.JDBCUtil;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.util.dependency.GradleDependency;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

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
        private final String driverClass;
        private final GradleDependency dependency;

        protected JDBCDriver(String name) {
            this.name = name;
            this.driverClass = getClass().getName();
            this.dependency = null;
        }

        public JDBCDriver(String name, String driverClass, String dependency) {
            this.name = name;
            this.driverClass = driverClass;
            this.dependency = GradleDependency.fromGradle(dependency);
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
                var c_driver = findDriverClass();

                LOGGER.info("loading {} -> {} ---- SUCCESS", this.name, c_driver.getName());
                return;
            } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                LOGGER.info("loading {} -> {} ---- DOWNLOADING", this.name, this.dependency);
            } catch (LinkageError e) {
                LOGGER.warn("loading {} -> {} ---- EXCEPTION({})", this.name, this.driverClass, e.getMessage());
                LOGGER.catching(e);
                return;
            }

            try {
                if (this.dependency == null) {
                    throw new IllegalStateException("No dependency configured for JDBC driver " + this.name);
                }
                var libraryManager = SLPluginEnvironment.getLibraryManager();

                libraryManager.resolveDependencies(List.of(this.dependency));
                libraryManager.injectLibraries(SLPluginEnvironment.getPlugin());

                var c_driver = findDriverClass();

                LOGGER.info("loading {} -> {} ---- SUCCESS", this.name, c_driver.getName());
            } catch (Exception | LinkageError e) {
                LOGGER.warn("loading {} -> ? ---- EXCEPTION({})", this.name, e.getMessage());
                LOGGER.catching(e);
            }
        }

        private Class<?> findDriverClass() throws ClassNotFoundException {
            return Class.forName(this.driverClass, true, SLPluginEnvironment.getPlugin().classLoader());
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
    }
}

final class UnknownDriver extends JDBCDrivers.JDBCDriver {
    public UnknownDriver() {
        super("_custom_");
    }

    @Override
    public boolean matches(DataSource ds) {
        return false;
    }
}

final class H2Driver extends JDBCDrivers.JDBCDriver {

    public H2Driver() {
        super("h2", "org.h2.Driver", "com.h2database:h2:2.3.232");
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
        super("sqlite", "org.sqlite.JDBC", "org.xerial:sqlite-jdbc:3.47.1.0");
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
        super("derby", "org.apache.derby.jdbc.EmbeddedDriver", "org.apache.derby:derby:10.14.2.0");
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
        super("hsqldb", "org.hsqldb.jdbc.JDBCDriver", "org.hsqldb:hsqldb:2.7.4");
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
        super(name, driverClass(name), dependency(name));
    }

    private static String driverClass(String name) {
        return switch (name) {
            case "mariadb" -> "org.mariadb.jdbc.Driver";
            case "oceanbase" -> "com.oceanbase.jdbc.Driver";
            default -> "com.mysql.cj.jdbc.Driver";
        };
    }

    private static String dependency(String name) {
        return switch (name) {
            case "mariadb" -> "org.mariadb.jdbc:mariadb-java-client:3.5.1";
            case "oceanbase" -> "com.oceanbase:oceanbase-client:2.4.12";
            default -> "com.mysql:mysql-connector-j:9.1.0";
        };
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
        super(name, "oracle.jdbc.OracleDriver", "com.oracle.database.jdbc:ojdbc11:23.5.0.24.07");
    }

    @Override
    public boolean matches(DataSource ds) {
        return JDBCUtil.withConnection(ds, conn -> conn.getMetaData().getDatabaseProductName().toLowerCase().contains("oracle"));
    }
}

final class SQLServerDriver extends JDBCDrivers.JDBCDriver {

    public SQLServerDriver(String name) {
        super(name, "com.microsoft.sqlserver.jdbc.SQLServerDriver", "com.microsoft.sqlserver:mssql-jdbc:12.8.1.jre11");
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
        super(name, "org.postgresql.Driver", "org.postgresql:postgresql:42.7.4");
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


