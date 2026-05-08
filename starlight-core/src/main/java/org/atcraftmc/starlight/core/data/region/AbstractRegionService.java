package org.atcraftmc.starlight.core.data.region;

import me.gb2022.gluon.Debug;
import org.atcraftmc.starlight.data.jdbc.JDBCUtil;
import org.atcraftmc.starlight.shared.data.JDBCBasedDataService;
import org.atcraftmc.starlight.util.BsonCodec;
import org.bson.BsonDocument;
import org.bukkit.Location;
import org.joml.Vector3d;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public abstract class AbstractRegionService<R extends Region> extends JDBCBasedDataService<R> implements RegionDataProvider<R> {
    private final ConcurrentHashMap<String, WorldRegionMonitorCache<R>> caches = new ConcurrentHashMap<>();

    public AbstractRegionService(String table) {
        super(table);
    }

    @Override
    public PreparedStatement attemptCreateTable(Connection conn) throws SQLException {
        var createTableSQL = """
                CREATE TABLE IF NOT EXISTS _region_ (
                    uuid CHAR(36) PRIMARY KEY,
                    owner CHAR(36),
                    name VARCHAR(128) NOT NULL,
                    world VARCHAR(128) NOT NULL,
                    x0 DOUBLE NOT NULL,
                    y0 DOUBLE NOT NULL,
                    z0 DOUBLE NOT NULL,
                    x1 DOUBLE NOT NULL,
                    y1 DOUBLE NOT NULL,
                    z1 DOUBLE NOT NULL,
                    meta VARCHAR(16384) NOT NULL
                );
                """;

        return conn.prepareStatement(createTableSQL);
    }

    @Override
    public Set<R> load(String worldId, int wx0, int wz0, int wx1, int wz1) {
        var sql = """
                SELECT * FROM _region_ WHERE world = ? AND(
                    ((x0 <= ? AND ? <= x1) OR (x0 <= ? AND ? <= x1) OR (z0<=? AND ?<=z1) OR (z0<=?AND?<=z1) )
                    OR ( x0 >= ? AND x1<=? AND z0>=? AND z1<=? )
                )
                """;

        try (var ps = this.connection.prepareStatement(sql)) {
            ps.setString(1, worldId);
            ps.setInt(2, wx0);
            ps.setInt(3, wx0);
            ps.setInt(4, wx1);
            ps.setInt(5, wx1);
            ps.setInt(6, wz0);
            ps.setInt(7, wz0);
            ps.setInt(8, wz1);
            ps.setInt(9, wz1);
            ps.setInt(10, wx0);
            ps.setInt(11, wx1);
            ps.setInt(12, wz0);
            ps.setInt(13, wz1);

            var result = new HashSet<R>();

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(decode(rs));
                }
            }

            Debug.log().info("%s:[%s/%s - %s/%s] -> %d".formatted(worldId, wx0, wz0, wx1, wz1, result.size()));

            return result;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean set(R region) {
        region.serializeMetadata(region.getExtraMetadata());

        try {
            return _add(region);
        } catch (SQLException e) {
            if (!JDBCUtil.isUniqueViolation(e)) {
                throw new RuntimeException(e);
            }

            try {
                return _update(region);
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

    private boolean _update(Region data) throws SQLException {
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


    public abstract R create(UUID id, UUID owner, String name, String world, Vector3d p1, Vector3d p2, BsonDocument meta);


    @Override
    public R decode(ResultSet rs) throws SQLException {
        return create(
                UUID.fromString(rs.getString("uuid")),
                UUID.fromString(rs.getString("owner")),
                rs.getString("name"),
                rs.getString("world"),
                new Vector3d(rs.getDouble("x0"), rs.getDouble("y0"), rs.getDouble("z0")),
                new Vector3d(rs.getDouble("x1"), rs.getDouble("y1"), rs.getDouble("z1")),
                BsonCodec.string(rs.getString("meta"))
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


    // position - calculated cache can't edit
    public Set<R> getIntersected(Location loc) {
        var world = loc.getWorld().getName();
        var cache = this.getCache(world);

        var result = new HashSet<R>();

        for (var uuid : cache.getRegionContained(loc.getBlockX(), loc.getBlockZ())) {
            var r = cache.getRegion(uuid).getRegion();

            if (r.asAABB().isVectorInside(new Vector3d(loc.getX(), loc.getY(), loc.getZ()))) {
                result.add(r);
            }
        }

        return result;
    }

    public Set<R> getIntersected(WorldAABB region) {
        var world = region.getWorldId();
        var cache = this.getCache(world);

        var result = new HashSet<R>();

        for (var uuid : cache.getRegionContained(region)) {
            var r = cache.getRegion(uuid).getRegion();

            if (r.asAABB().intersects(region.asAABB())) {
                result.add(r);
            }
        }

        return result;
    }

    public void invalidateCache() {
        for (var rc : this.caches.values()) {
            rc.invalidate();
        }
    }


    public boolean rename(UUID owner, String origin, String dest) throws SQLException {
        try (var p = this.connection.prepareStatement("UPDATE _region_ SET name = ? WHERE name = ? AND owner = ?")) {
            p.setString(1, dest);
            p.setString(2, origin);
            p.setString(3, owner.toString());

            invalidateCache();

            return p.executeUpdate() > 0;
        }
    }

    public boolean delete(String name) throws SQLException {
        try (var p = connection.prepareStatement("DELETE FROM _region_ WHERE name = ?")) {
            p.setString(1, name);
            invalidateCache();
            return p.executeUpdate() > 0;
        }
    }


    public boolean existName(String name) throws SQLException {
        try (var p = connection.prepareStatement("SELECT 42 FROM _region_ WHERE name = ?")) {
            p.setString(1, name);
            try (var rs = p.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existUUID(UUID uuid) {
        try (var p = connection.prepareStatement("SELECT 42 FROM _region_ WHERE uuid = ?")) {
            p.setString(1, uuid.toString());
            try (var rs = p.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public WorldRegionMonitorCache<R> getCache(String world) {
        return this.caches.computeIfAbsent(world, (k) -> new WorldRegionMonitorCache<R>(world, this));
    }

    public Set<WorldAABB> queryRegions(PreparedStatement ps) throws SQLException {
        var result = new HashSet<WorldAABB>();

        try (var rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(decode(rs));
            }
        }

        return result;
    }

    public Optional<WorldAABB> byName(String name) throws SQLException {
        try (var p = connection.prepareStatement("SELECT * FROM _region_ WHERE name = ?")) {
            p.setString(1, name);

            try (var rs = p.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(decode(rs));
                }
                return Optional.empty();
            }
        }
    }


    public boolean isAnyHit(Location location) throws SQLException {
        var world = location.getWorld().getName();
        var x = location.getX();
        var y = location.getY();
        var z = location.getZ();

        var sql = "SELECT uuid FROM _region_ WHERE ? >= x0 AND ? <= x1 AND ? >= y0 AND ? <= y1 AND ? >= z0 AND ? <= z1 AND world = ? LIMIT 1";

        try (var ps = this.connection.prepareStatement(sql)) {
            renderBoundCall(world, x, y, z, ps);

            try (var rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void renderBoundCall(String world, double x, double y, double z, PreparedStatement ps) throws SQLException {
        ps.setDouble(1, x);
        ps.setDouble(2, x);
        ps.setDouble(3, y);
        ps.setDouble(4, y);
        ps.setDouble(5, z);
        ps.setDouble(6, z);
        ps.setString(7, world);
    }


    @Override
    public String getTableNamePlaceholder() {
        return "_region_";
    }
}
