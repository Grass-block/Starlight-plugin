package org.atcraftmc.starlight.core.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.atcraftmc.starlight.data.jdbc.source.SQLMapper;
import org.atcraftmc.starlight.shared.jdbc.JDBCDataService;
import org.atcraftmc.starlight.data.jdbc.source.SQLMappedDataSource;
import org.atcraftmc.starlight.shared.JDBCService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public final class BanEntryService extends JDBCDataService {
    private final Cache<UUID, Boolean> stateCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5)).build();
    private final Cache<UUID, List<BanEntry>> entryCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5)).build();
    private final String tableName;

    public BanEntryService(String table) {
        this.tableName = table;
    }

    @Override
    public void init(DataSource datasource, JDBCService service) {
        super.init(new SQLMappedDataSource(datasource, SQLMapper.single("_ban_", this.tableName)), service);
    }

    public BanEntry decode(ResultSet rs) throws SQLException {
        return new BanEntry(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("target")),
                rs.getLong("expire"),
                rs.getString("reason"),
                rs.getString("operator")
        );
    }

    public void add(BanEntry data) {
        try (var c = this.datasource.getConnection(); var p = c.prepareStatement(
                "insert into _ban_ (id,target,expire,reason,operator,valid) VALUES (?, ?, ?, ?, ?, true)")) {
            p.setString(1, data.getBanId().toString());
            p.setString(2, data.getTarget().toString());
            p.setLong(3, data.getExpires());
            p.setString(4, data.getReason());
            p.setString(5, data.getOperator());

            p.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.stateCache.invalidate(data.getTarget());
        this.entryCache.invalidate(data.getBanId());
    }

    @Override
    public PreparedStatement createTable(Connection conn) throws SQLException {
        var sql = """
                CREATE TABLE _ban_ (
                    id char(36) PRIMARY KEY,
                    target char(36),
                    expire long,
                    reason varchar(512),
                    operator varchar(128),
                    valid boolean
                )
                """;

        return conn.prepareStatement(sql);
    }


    public boolean isBanned(UUID uuid) {
        try {
            return this.stateCache.get(uuid, () -> {
                try (var c = this.datasource.getConnection(); var s = c.prepareStatement(
                        "SELECT id from _ban_ where target=? and expire>? and valid = true")) {
                    s.setString(1, uuid.toString());
                    s.setLong(2, System.currentTimeMillis());

                    try (var rs = s.executeQuery()) {
                        return rs.next();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    public List<BanEntry> get(UUID uuid) {
        var res = new ArrayList<BanEntry>();

        try (var c = this.datasource.getConnection(); var s = c.prepareStatement("SELECT * from _ban_ where target=?")) {
            s.setString(1, uuid.toString());

            try (var rs = s.executeQuery()) {
                while (rs.next()) {
                    res.add(this.decode(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    public List<BanEntry> getValid(UUID uuid) {
        try {
            return this.entryCache.get(uuid, () -> {
                var res = new ArrayList<BanEntry>();

                try (var c = this.datasource.getConnection(); var s = c.prepareStatement(
                        "SELECT * from _ban_ where target=? and expire>? and valid = true")) {
                    s.setString(1, uuid.toString());
                    s.setLong(2, System.currentTimeMillis());

                    try (var rs = s.executeQuery()) {
                        while (rs.next()) {
                            res.add(this.decode(rs));
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                return res;
            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void pardon(UUID uniqueId) {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("UPDATE _ban_ SET valid = false WHERE target = ?")) {
            ps.setString(1, uniqueId.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.stateCache.invalidate(uniqueId);
    }
}
