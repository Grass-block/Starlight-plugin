package org.atcraftmc.starlight.core.data;

import org.atcraftmc.starlight.data.jdbc.SQLMapper;
import org.atcraftmc.starlight.data.jdbc.service.JDBCDataService;
import org.atcraftmc.starlight.data.jdbc.source.SQLMappedDataSource;
import org.atcraftmc.starlight.data.jdbc.source.WrappedDataSource;
import org.atcraftmc.starlight.shared.data.JDBCBasedDataService;
import org.atcraftmc.starlight.shared.service.JDBCService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public final class WaypointService extends JDBCDataService {
    private final String tableName;
    
    public WaypointService(String table) {
        this.tableName = table;
    }

    @Override
    public void init(DataSource datasource, JDBCService service) {
        super.init(new SQLMappedDataSource(datasource, SQLMapper.single("_waypoint_", this.tableName)), service);
    }

    public Waypoint decode(ResultSet rs) throws SQLException {
        var uuid = UUID.fromString(rs.getString("uuid"));
        var name = rs.getString("name");
        var world = rs.getString("world");
        var x = rs.getDouble("x");
        var y = rs.getDouble("y");
        var z = rs.getDouble("z");
        var yaw = rs.getFloat("yaw");
        var pitch = rs.getFloat("pitch");
        var owner = UUID.fromString(rs.getString("owner"));
        var allowed = new HashSet<>(Arrays.asList(rs.getString("allowed").split(":")));

        return new Waypoint(uuid, name, world, x, y, z, yaw, pitch, owner, allowed);
    }
    
    public void encode(PreparedStatement ps, Waypoint data) throws SQLException {
        ps.setString(1, data.getUuid().toString());
        ps.setString(2, data.getName());
        ps.setString(3, data.getWorld());
        ps.setDouble(4, data.getX());
        ps.setDouble(5, data.getY());
        ps.setDouble(6, data.getZ());
        ps.setFloat(7, data.getYaw());
        ps.setFloat(8, data.getPitch());
        ps.setString(9, data.getOwner().toString());
        ps.setString(10, String.join(":", data.getAllowed()));
    }

    @Override
    public PreparedStatement attemptCreateTable(Connection conn) throws SQLException {
        var createTableSQL = """
                CREATE TABLE IF NOT EXISTS _waypoint_ (
                    uuid VARCHAR(36) PRIMARY KEY,
                    name VARCHAR(128) NOT NULL UNIQUE,
                    world VARCHAR(128) NOT NULL,
                    x DOUBLE NOT NULL,
                    y DOUBLE NOT NULL,
                    z DOUBLE NOT NULL,
                    yaw FLOAT NOT NULL,
                    PITCH FLOAT NOT NULL,
                    owner VARCHAR(36) NOT NULL,
                    allowed VARCHAR(1024) NOT NULL
                );
                """;

        return conn.prepareStatement(createTableSQL);
    }

    public boolean add(Waypoint waypoint) throws SQLException {
        if (existName(waypoint.getName())) {
            throw new SQLException("名称已存在: " + waypoint.getName());
        }

        try (var c = this.datasource.getConnection();var p = c.prepareStatement(
                "INSERT INTO _waypoint_ (uuid, name, world, x, y, z, yaw, pitch, owner, allowed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            encode(p, waypoint);
            return p.executeUpdate() > 0;
        }
    }

    public boolean rename(String origin, String dest) throws SQLException {
        try (var c = this.datasource.getConnection();var p = c.prepareStatement("UPDATE _waypoint_ SET name = ? WHERE name = ?")) {
            p.setString(1, dest);
            p.setString(2, origin);
            return p.executeUpdate() > 0;
        }
    }

    public boolean delete(String name) throws SQLException {
        try (var c = this.datasource.getConnection();var p = c.prepareStatement("DELETE FROM _waypoint_ WHERE name = ?")) {
            p.setString(1, name);
            return p.executeUpdate() > 0;
        }
    }

    public Set<String> queryNames(PreparedStatement ps) throws SQLException {
        var result = new HashSet<String>();
        try (var rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getString("name"));
            }
        }

        return result;
    }

    public boolean existName(String name) throws SQLException {
        try (var c = this.datasource.getConnection();var p = c.prepareStatement("SELECT uuid FROM _waypoint_ WHERE name = ? LIMIT 1")) {
            p.setString(1, name);
            try (var rs = p.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Set<String> listNameOwned(UUID user) throws SQLException {
        try (var c = this.datasource.getConnection();var ps = c.prepareStatement("SELECT name FROM _waypoint_ WHERE owner=?")) {
            ps.setString(1, user.toString());
            return queryNames(ps);
        }
    }

    public Set<String> listNameAccessible(UUID user) throws SQLException {
        try (var c = this.datasource.getConnection();var ps = c.prepareStatement("SELECT name FROM _waypoint_ WHERE owner=? OR allowed LIKE '%all%' OR allowed LIKE ?")) {
            ps.setString(1, user.toString());
            ps.setString(2, user.toString());
            return queryNames(ps);
        }
    }

    public boolean hasAccess(UUID user, String name) throws SQLException {
        try (var c = this.datasource.getConnection();var ps = c.prepareStatement("SELECT allowed,owner FROM _waypoint_ WHERE name=?")) {
            ps.setString(1, name);

            try (var rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                var a = rs.getString("allowed").contains(user.toString());
                var b = rs.getString("allowed").contains("all");
                var o = rs.getString("owner").equals(user.toString());

                return a || b || o;
            }
        }
    }

    public boolean hasControl(UUID user, String name) throws SQLException {
        try (var c = this.datasource.getConnection();var ps = c.prepareStatement("SELECT owner FROM _waypoint_ WHERE name=?")) {
            ps.setString(1, name);

            try (var rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                return rs.getString("owner").equals(user.toString());
            }
        }
    }

    public Set<Waypoint> queryWaypoints(PreparedStatement ps) throws SQLException {
        var result = new HashSet<Waypoint>();

        try (var rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(decode(rs));
            }
        }

        return result;
    }

    public Set<Waypoint> listOwned(UUID user) throws SQLException {
        try (var c = this.datasource.getConnection();var ps = c.prepareStatement("SELECT * FROM _waypoint_ WHERE owner=?")) {
            ps.setString(1, user.toString());
            return queryWaypoints(ps);
        }
    }

    public Set<Waypoint> listAccessible(UUID user) throws SQLException {
        try (var c = this.datasource.getConnection();var ps = c.prepareStatement("SELECT * FROM _waypoint_ WHERE owner=? OR allowed LIKE '%all%' OR allowed LIKE ?")) {
            ps.setString(1, user.toString());
            ps.setString(2, user.toString());
            return queryWaypoints(ps);
        }
    }

    public boolean update(Waypoint data) throws SQLException {
        String sql = "UPDATE _waypoint_ SET name = ?, world = ?, x = ?, y = ?, z = ?, " +
                "yaw = ?, pitch = ?, owner = ?, allowed = ? WHERE uuid = ?";

        try (var c = this.datasource.getConnection();var ps = c.prepareStatement(sql)) {
            ps.setString(1, data.getName());
            ps.setString(2, data.getWorld());
            ps.setDouble(3, data.getX());
            ps.setDouble(4, data.getY());
            ps.setDouble(5, data.getZ());
            ps.setFloat(6, data.getYaw());
            ps.setFloat(7, data.getPitch());
            ps.setString(8, data.getOwner().toString());
            ps.setString(9, String.join(":", data.getAllowed()));
            ps.setString(10, data.getUuid().toString());
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<Waypoint> byName(String name) throws SQLException {
        try (var c = this.datasource.getConnection();var p = c.prepareStatement("SELECT * FROM _waypoint_ WHERE name = ?")) {
            p.setString(1, name);
            try (var rs = p.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(decode(rs));
                }
                return Optional.empty();
            }
        }
    }
}