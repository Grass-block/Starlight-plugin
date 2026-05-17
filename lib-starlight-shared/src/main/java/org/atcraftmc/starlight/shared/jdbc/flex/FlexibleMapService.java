package org.atcraftmc.starlight.shared.jdbc.flex;

import org.atcraftmc.starlight.data.jdbc.source.SQLMappedDataSource;
import org.atcraftmc.starlight.data.jdbc.source.SQLMapper;
import org.atcraftmc.starlight.shared.JDBCService;
import org.atcraftmc.starlight.shared.jdbc.JDBCDataService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public final class FlexibleMapService extends JDBCDataService {
    private final UUID sessionUUID = UUID.randomUUID();
    private final String tableName;

    public FlexibleMapService(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void init(DataSource datasource, JDBCService service) {
        super.init(new SQLMappedDataSource(datasource, SQLMapper.single("_table_", this.tableName)), service);
    }

    @Override
    public PreparedStatement createTable(Connection conn) throws SQLException {
        var sql = """
                CREATE TABLE IF NOT EXISTS _table_ (
                    uuid VARCHAR(36) PRIMARY KEY
                );
                """;

        return conn.prepareStatement(sql);
    }


    public void createRow(UUID uuid) throws SQLException {
        try (var c = this.datasource.getConnection(); var p = c.prepareStatement("SELECT uuid from _table_ WHERE uuid = ?")) {
            p.setString(1, uuid.toString());
            try (var s = p.executeQuery()) {
                if (!s.next()) {
                    try (var p2 = this.database.getConnection().prepareStatement("INSERT INTO _table_ (uuid) values (?)")) {
                        p2.setString(1, uuid.toString());
                        p2.executeUpdate();
                    }
                }
            }
        }
    }

    public UUID getSessionUUID() {
        return sessionUUID;
    }

    public void onColumnAdded(String col) throws SQLException {
        this.database.recordColumnRegisterFor(this.tableName, col);
    }

    public boolean hasColumnRegistered(String col) {
        return this.database.isColumnRegistered(this.tableName, col);
    }

    public String getTableName() {
        return this.tableName;
    }

    public interface Codec<I> {
        Codec<String> STRING = new Codec<>() {
            @Override
            public String decode(String data) {
                return data;
            }

            @Override
            public String encode(String data) {
                return data;
            }
        };

        String encode(I data);

        I decode(String data);
    }
}
