package org.atcraftmc.starlight.data.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface JDBCUtil {
    static <T> T withConnection(DataSource ds, SQLFunction<Connection, T> func) {
        try (Connection conn = ds.getConnection()) {
            return func.apply(conn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static DBMode getConnectionMode(DataSource ds) {
        try (var c = ds.getConnection()) {
            var meta = c.getMetaData();

            if (!meta.getDatabaseProductName().equalsIgnoreCase("H2")) {
                return DBMode.OTHER;
            }

            String url = meta.getURL().toLowerCase();

            if (url.contains(":tcp:") || url.contains(":ssl:")) {
                return DBMode.H2_REMOTE;
            }

            return DBMode.H2_EMBEDDED;

        } catch (SQLException e) {
            return DBMode.OTHER;
        }
    }

    static void h2_shutdownAndCleanup(DataSource source) {
        try (var stmt = source.getConnection().createStatement()) {
            stmt.execute("SHUTDOWN DEFRAG");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void sqlite_vacuum(DataSource ds) {
        withConnection(ds, conn -> {
            boolean autoCommit = conn.getAutoCommit();
            try {
                if (!autoCommit) {
                    conn.setAutoCommit(true);
                }

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("VACUUM");
                }

            } finally {
                if (!autoCommit) {
                    conn.setAutoCommit(false);
                }
            }
            return null;
        });
    }

    static boolean isUniqueViolation(SQLException e) {
        String state = e.getSQLState();
        if (state != null) {
            if ("23505".equals(state)) {
                return true;      // 标准
            }
            if (state.startsWith("23")) {
                return true;     // 宽松兜底
            }
        }

        int code = e.getErrorCode();
        return code == 1062; // MySQL
    }

    enum DBMode {
        H2_EMBEDDED,
        H2_REMOTE,
        OTHER
    }

    @FunctionalInterface
    interface SQLFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
