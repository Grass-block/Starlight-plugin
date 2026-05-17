package org.atcraftmc.starlight.shared.jdbc;

import org.atcraftmc.starlight.data.jdbc.JDBCDatabase;
import org.atcraftmc.starlight.shared.JDBCService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public abstract class JDBCDataService {
    protected JDBCDatabase database;
    protected DataSource datasource;

    public void initService(JDBCDatabase database) {
        this.database = database;
        this.init(database, database.getContext());
    }

    public void init(DataSource datasource, JDBCService service) {
        this.datasource = datasource;
        service.registerInstance(this);

        try (var c = datasource.getConnection(); var stmt = this.createTable(c)) {
            if (stmt == null) {
                return;
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("exist")) {
                return;
            }
            throw new RuntimeException(e);
        }
    }

    public void onClosing() {
    }

    public PreparedStatement createTable(Connection conn) throws SQLException {
        return null;
    }

    public void tick(AtomicLong ticks) {
    }

    public final DataSource getDatasource() {
        return datasource;
    }

    public final JDBCDatabase getDatabase() {
        return database;
    }
}
