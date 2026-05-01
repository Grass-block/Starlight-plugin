package org.atcraftmc.starlight.data.jdbc.service;

import org.atcraftmc.starlight.data.jdbc.source.JDBCDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class JDBCDataService {
    protected DataSource datasource;

    public void init(JDBCDataSource datasource) {
        this.datasource = datasource;

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

    public PreparedStatement attemptCreateTable(Connection conn) throws SQLException {
        return null;
    }
}
