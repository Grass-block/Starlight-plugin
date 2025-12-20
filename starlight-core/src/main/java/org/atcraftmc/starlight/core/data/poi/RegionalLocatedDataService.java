package org.atcraftmc.starlight.core.data.poi;

import org.atcraftmc.starlight.core.data.JDBCBasedDataService;
import org.joml.Vector3d;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class RegionalLocatedDataService<R extends LocationBasedObject> extends JDBCBasedDataService<R> {
    protected RegionalLocatedDataService(String table) {
        super(table);
    }

    @Override
    public PreparedStatement attemptCreateTable(Connection conn) throws SQLException {
        var sql = """
                CREATE TABLE IF NOT EXISTS _poi_ (
                    uuid char(36) PRIMARY KEY,
                    name varchar(255) NOT NULL UNIQUE,
                    world varchar(32),
                    x double,
                    y double,
                    z double,
                    data varchar(1024)
                )
                """;

        return conn.prepareStatement(sql);
    }


    /*
    public boolean set(R data) {
        //data.serializeMetadata(data.getExtraMetadata());

        try {
            return _add(data);
        } catch (SQLException e) {
            if (!JDBCService.isUniqueViolation(e)) {
                throw new RuntimeException(e);
            }

            try {
                return _update(data);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public boolean update(R data) {
        try {
            return _add(data);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean add(R region) throws SQLException {
        if (existName(region.getName())) {
            throw new SQLException("名称已存在: " + region.getName());
        }

        return _add(region);
    }

    private boolean _add(R data) throws SQLException {
        data.serializeMetadata(data.getExtraMetadata());

        try (var ps = this.connection.prepareStatement(
                "INSERT INTO _region_ (uuid, owner, name, world, x0, y0, z0, x1, y1, z1, meta) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            var p0 = data.getMinPoint();
            var p1 = data.getMaxPoint();

            ps.setString(1, data.getUuid().toString());
            ps.setString(2, data.getOwner().toString());
            ps.setString(3, data.getName());
            ps.setString(4, data.getWorldId());
            ps.setDouble(5, p0.getX());
            ps.setDouble(6, p0.getY());
            ps.setDouble(7, p0.getZ());
            ps.setDouble(8, p1.getX());
            ps.setDouble(9, p1.getY());
            ps.setDouble(10, p1.getZ());
            ps.setString(11, BsonCodec.string(data.getExtraMetadata()));

            invalidateCache();

            return ps.executeUpdate() > 0;
        }
    }

    private boolean _update(R data) throws SQLException {
        data.serializeMetadata(data.getExtraMetadata());

        var p0 = data.getMinPoint();
        var p1 = data.getMaxPoint();

        try (var ps = this.connection.prepareStatement(
                "UPDATE _region_ SET name=?,owner=?, world=?, x0=?, y0=?, z0=?, x1=?, y1=?, z1=?, meta=? where uuid = ?")) {
            ps.setString(1, data.getName());
            ps.setString(2, data.getOwner().toString());
            ps.setString(3, data.getWorldId());
            ps.setDouble(4, p0.getX());
            ps.setDouble(5, p0.getY());
            ps.setDouble(6, p0.getZ());
            ps.setDouble(7, p1.getX());
            ps.setDouble(8, p1.getY());
            ps.setDouble(9, p1.getZ());
            ps.setString(10, data.getExtraMetadata().toString());
            ps.setString(11, data.getUuid().toString());

            invalidateCache();

            return ps.executeUpdate() > 0;
        }
    }

     */


    public abstract R create(UUID id, String name, String world, Vector3d p, String payload);


    @Override
    public R decode(ResultSet rs) throws SQLException {
        return create(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("name"),
                rs.getString("world"),
                new Vector3d(rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")),
                rs.getString("data")
        );
    }


    // name
    public Set<String> _queryNames(PreparedStatement ps) throws SQLException {
        var result = new HashSet<String>();
        try (var rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getString("name"));
            }
        }

        return result;
    }

    public Set<String> listNames() throws SQLException {
        try (var ps = this.connection.prepareStatement("SELECT name FROM _region_")) {
            return _queryNames(ps);
        }
    }

    public Set<String> listNamesByOwner(UUID owner) {
        try (var ps = this.connection.prepareStatement("SELECT name FROM _region_ where owner = ?")) {
            ps.setString(1, owner.toString());
            return _queryNames(ps);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }





    @Override
    public final String getTableNamePlaceholder() {
        return "_poi_";
    }
}
