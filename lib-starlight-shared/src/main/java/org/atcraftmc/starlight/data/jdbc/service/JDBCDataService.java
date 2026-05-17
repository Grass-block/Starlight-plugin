package org.atcraftmc.starlight.data.jdbc.service;

import org.atcraftmc.starlight.shared.service.JDBCService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public abstract class JDBCDataService {
    protected DataSource datasource;

    public void init(DataSource datasource, JDBCService service) {
        this.datasource = datasource;
        service.registerInstance(this);

        try (var c = datasource.getConnection(); var stmt = this.attemptCreateTable(c)) {
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

    public void onClosing(){}

    public PreparedStatement attemptCreateTable(Connection conn) throws SQLException {
        return null;
    }

    public void tick(AtomicLong ticks) {
    }
}
