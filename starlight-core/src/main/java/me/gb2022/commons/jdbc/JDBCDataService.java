package me.gb2022.commons.jdbc;

import me.gb2022.commons.jdbc.db.JDBCDatabase;
import org.atcraftmc.starlight.shared.JDBCService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public abstract class JDBCDataService implements GenericQueryDatasourceProvider {
    protected JDBCDatabase database;
    protected DataSource datasource;
    protected DataSource genericSource;

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
            if (e.getMessage().toLowerCase().contains("already exists")) {
                return;
            }
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public final DataSource getGenerateQuerySource() {
        return this.genericSource;
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
