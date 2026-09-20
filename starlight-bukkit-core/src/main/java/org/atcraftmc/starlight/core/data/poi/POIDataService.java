package org.atcraftmc.starlight.core.data.poi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.gb2022.commons.jdbc.trait.NameQuery;
import me.gb2022.commons.jdbc.trait.UUIDQuery;
import me.gb2022.gluon.Debug;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.core.data.chunked.ChunkedDataProvider;
import org.atcraftmc.starlight.core.data.chunked.ChunkedObjectDataService;
import me.gb2022.commons.jdbc.JDBCUtil;
import me.gb2022.commons.jdbc.source.SQLMapper;
import org.joml.Vector3d;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class POIDataService<R extends POIObject> extends ChunkedObjectDataService<R> implements ChunkedDataProvider<R>, NameQuery<R>, UUIDQuery<R> {
    public POIDataService(String tableName) {
        super(tableName);
    }

    @Override
    public final void initMapper(SQLMapper mapper) {
        super.initMapper(mapper);
        mapper.replaceSQL("_poi_", this.getTableName());
    }

    @Override
    public final PreparedStatement createTable(Connection conn) throws SQLException {
        var sql = """
                CREATE TABLE IF NOT EXISTS _poi_ (
                    uuid char(36) PRIMARY KEY,
                    name varchar(255) NOT NULL UNIQUE,
                    world varchar(64),
                    x double,
                    y double,
                    z double,
                    data varchar(1024)
                )
                """;

        return conn.prepareStatement(sql);
    }

    @Override
    public boolean delete(String name) throws SQLException {
        var data = byName(name);

        if(data.isEmpty()) {
            return false;
        }

        data.get().destroy();

        var res = NameQuery.super.delete(name);
        for (var cache:this.getCaches().values()){
            cache.remove(data.get().getUuid());
        }
        return res;
    }

    @Override
    public final Set<R> load(String worldId, int wx0, int wz0, int wx1, int wz1) {
        var sql = """
                SELECT * FROM _poi_ WHERE world = ? AND(x>=? AND x<=?) AND (z>=? AND z<=?)
                """;

        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setString(1, worldId);
            ps.setInt(2, wx0);
            ps.setInt(3, wx1);
            ps.setInt(4, wz0);
            ps.setInt(5, wz1);

            var result = new HashSet<R>();

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(decode(rs));
                }
            }

            Debug.log().info("[POI] %s:[%s/%s - %s/%s] -> %d".formatted(worldId, wx0, wz0, wx1, wz1, result.size()));

            for (var r : result) {
                QLib.task().global().run(r::create);
            }

            return result;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public final R decode(ResultSet rs) throws SQLException {
        return create(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("name"),
                rs.getString("world"),
                new Vector3d(rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z")),
                JsonParser.parseString(rs.getString("data")).getAsJsonObject()
        );
    }

    public abstract R create(UUID id, String name, String world, Vector3d p, JsonObject payload);

    public void move(UUID uuid, String nw, double nx, double ny, double nz) throws SQLException {
        var prev = byUUID(uuid);

        if (prev.isEmpty()) {
            return;
        }

        var previous = prev.get();
        this.getCache(nw).invalidate();

        if (!Objects.equals(previous.world, nw)) {
            this.getCache(previous.world).invalidate();
        }
    }

    public final boolean set(R data) {
        var meta = data.serializeData();

        try {
            return _add(data);
        } catch (SQLException e) {
            if (!JDBCUtil.isUniqueViolation(e)) {
                throw new RuntimeException(e);
            }

            try {
                return _update(data);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public final boolean update(R data) {
        try {
            return _add(data);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public final boolean add(R region) throws SQLException {
        if (existName(region.getName())) {
            throw new SQLException("Name exists: " + region.getName());
        }

        return _add(region);
    }

    private boolean _add(R data) throws SQLException {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement(
                "INSERT INTO _region_ (uuid, name, world, x, y, z, data) VALUES (?,?, ?, ?, ?, ?.?)")) {

            ps.setString(1, data.getUuid().toString());
            ps.setString(2, data.getName());
            ps.setString(3, data.getWorld());
            ps.setDouble(4, data.getX());
            ps.setDouble(5, data.getY());
            ps.setDouble(6, data.getZ());
            ps.setString(7, data.serializeData().toString());

            onUpdate();

            return ps.executeUpdate() > 0;
        }
    }

    private boolean _update(R data) throws SQLException {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement(
                "UPDATE _region_ SET name=?,owner=?, world=?, x=?, y=?, z=?, data=? where uuid = ?")) {

            ps.setString(1, data.getName());
            ps.setString(2, data.getWorld());
            ps.setDouble(3, data.getX());
            ps.setDouble(4, data.getY());
            ps.setDouble(5, data.getZ());
            ps.setString(6, data.serializeData().toString());
            ps.setString(7, data.getUuid().toString());

            invalidateCache();

            return ps.executeUpdate() > 0;
        }
    }


    @Override
    public void handleRemove(R r) {
        r.destroy();
    }
}
