package org.atcraftmc.starlight.data.jdbc.service;

import org.atcraftmc.starlight.data.jdbc.SQLMapper;
import org.atcraftmc.starlight.data.jdbc.source.JDBCDataSource;
import org.atcraftmc.starlight.data.jdbc.source.SQLMappedDataSource;
import org.atcraftmc.starlight.data.jdbc.source.WrappedDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class TagMapService extends JDBCDataService implements TagMap {
    private final String tableName;
    private WrappedDataSource payloadHandlerA;
    private WrappedDataSource payloadHandlerB;
    private WrappedDataSource payloadHandlerC;
    private WrappedDataSource payloadHandlerD;

    public TagMapService(String tableName) {
        this.tableName = tableName;
    }

    static SQLMappedDataSource channel(DataSource source, String payload, String tableName) {
        return new SQLMappedDataSource(source, SQLMapper.create((m) -> {
            m.replaceSQL("_payload_", payload);
            m.replaceSQL("_tagmap_", tableName);
        }));
    }

    @Override
    public void init(JDBCDataSource datasource) {
        super.init(datasource);
        this.datasource = new SQLMappedDataSource(datasource, SQLMapper.single("_tagmap_", this.tableName));
        this.payloadHandlerA = channel(datasource, "payload_a", this.tableName);
        this.payloadHandlerB = channel(datasource, "payload_b", this.tableName);
        this.payloadHandlerC = channel(datasource, "payload_c", this.tableName);
        this.payloadHandlerD = channel(datasource, "payload_d", this.tableName);
    }

    @Override
    public PreparedStatement attemptCreateTable(Connection conn) throws SQLException {
        var sql = """
                CREATE TABLE IF NOT EXISTS _tagmap_ (
                 uuid varchar(36) PRIMARY KEY UNIQUE,
                 payload_a varchar(4096) default ';',
                 payload_b varchar(4096) default ';',
                 payload_c varchar(4096) default ';',
                 payload_d varchar(4096) default ';'
                )
                """;

        return conn.prepareStatement(sql);
    }

    private int getSlot(String tag) {
        if (tag.contains(";")) {
            throw new IllegalArgumentException("Invalid tag format: " + tag);
        }

        return Math.abs(tag.hashCode()) % 4;
    }

    private WrappedDataSource getPayloadHandlerFor(String data) {
        return switch (getSlot(data)) {
            case 0 -> this.payloadHandlerA;
            case 1 -> this.payloadHandlerB;
            case 2 -> this.payloadHandlerC;
            case 3 -> this.payloadHandlerD;
            default -> throw new IllegalArgumentException("Invalid slot: " + getSlot(data));
        };
    }

    @Override
    public Set<String> get(UUID uuid) throws SQLException {
        var result = new HashSet<String>();

        try (var c = this.datasource.getConnection(); var p = c.prepareStatement("SELECT * FROM _tagmap_ WHERE uuid = ?")) {
            p.setString(1, uuid.toString());
            try (var set = p.executeQuery()) {
                if (!set.next()) {
                    return Set.of();
                }

                result.addAll(Arrays.asList(set.getString("payload_a").split(";")));
                result.addAll(Arrays.asList(set.getString("payload_b").split(";")));
                result.addAll(Arrays.asList(set.getString("payload_c").split(";")));
                result.addAll(Arrays.asList(set.getString("payload_d").split(";")));
            }
        }
        result.removeIf(String::isEmpty);
        return result;
    }

    @Override
    public boolean has(UUID uuid, String data) throws SQLException {
        var sql = "SELECT uuid FROM _tagmap_ WHERE uuid = ? AND _payload_ LIKE ?";

        try (var p = this.getPayloadHandlerFor(data).getConnection().prepareStatement(sql)) {
            p.setString(1, uuid.toString());
            p.setString(2, ";" + data + ";");

            try (var rs = p.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean hasEntry(UUID uuid) throws SQLException {
        var sql = "SELECT uuid FROM _tagmap_ WHERE uuid = ?";

        try (var p = this.datasource.getConnection().prepareStatement(sql)) {
            p.setString(1, uuid.toString());

            try (var rs = p.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void add(UUID uuid, String data) throws SQLException {
        if (has(uuid, data)) {
            return;
        }

        if (!hasEntry(uuid)) {
            var sql = "INSERT INTO _tagmap_ (uuid,payload_a,payload_b,payload_c,payload_d) values ( ?,';_init_;',';_init_;',';_init_;',';_init_;' )";
            try (var p = this.datasource.getConnection().prepareStatement(sql)) {
                p.setString(1, uuid.toString());
                p.executeUpdate();
            }
        }

        var sql = """
                UPDATE _tagmap_
                SET _payload_ = CONCAT(_payload_, ?)
                WHERE uuid = ?;
                """;

        try (var p = this.getPayloadHandlerFor(data).getConnection().prepareStatement(sql)) {
            p.setString(1, data + ";");
            //p.setString(2, ";" + data + ";");
            p.setString(2, uuid.toString());
            p.executeUpdate();
        }
    }

    @Override
    public void delete(UUID uuid, String data) throws SQLException {
        var sql = """
                UPDATE _tagmap_
                SET _payload_ = REPLACE(_payload_, ?, '')
                WHERE _payload_ LIKE ? AND uuid = ?;
                """;

        try (var p = this.getPayloadHandlerFor(data).getConnection().prepareStatement(sql)) {
            p.setString(1, data);
            p.setString(2, data + ";");
            p.setString(3, uuid.toString());
            p.executeUpdate();
        }
    }
}