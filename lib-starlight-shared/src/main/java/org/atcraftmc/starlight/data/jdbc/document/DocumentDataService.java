package org.atcraftmc.starlight.data.jdbc.document;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.gson.JsonObject;
import org.atcraftmc.starlight.data.jdbc.JDBCUtil;
import org.atcraftmc.starlight.data.jdbc.SQLMapper;
import org.atcraftmc.starlight.data.jdbc.service.JDBCDataService;
import org.atcraftmc.starlight.data.jdbc.source.SQLMappedDataSource;
import org.atcraftmc.starlight.shared.service.JDBCService;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

//todo: add dynamic save
public final class DocumentDataService extends JDBCDataService implements RemovalListener<UUID, DocumentEntry> {
    private final Cache<UUID, DocumentEntry> cache = CacheBuilder.newBuilder()
            .removalListener(this)
            .expireAfterAccess(Duration.ofSeconds(30))
            .build();
    private final String tableName;

    public DocumentDataService(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void init(DataSource datasource, JDBCService service) {
        super.init(new SQLMappedDataSource(datasource, SQLMapper.single("_table_", this.tableName)), service);
    }

    @Override
    public PreparedStatement attemptCreateTable(Connection conn) throws SQLException {
        var sql = """
                CREATE TABLE IF NOT EXISTS _table_ (
                    uuid char(36) PRIMARY KEY,
                    data text default '{}'
                )
                """;
        return conn.prepareStatement(sql);
    }

    @Override
    public void onRemoval(@NotNull RemovalNotification<UUID, DocumentEntry> notification) {
        setData(notification.getKey(), notification.getValue());
    }

    @Override
    public void onClosing() {
        this.cache.asMap().forEach(this::setData);
    }

    @Override
    public void tick(AtomicLong ticks) {
        if (ticks.get() % 10 == 0) {
            this.cache.asMap().forEach((k, v) -> {
                if (v.dirty()) {
                    System.out.println(v.getRawDOM());
                    setData(k, v);
                }
            });
        }
    }

    public JsonObject get(UUID uuid) {
        try {
            return cache.get(uuid, () -> getData(uuid)).getObject();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    //raw
    private DocumentEntry getData(UUID uuid) throws SQLException {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("select data from _table_ where uuid=?")) {
            ps.setString(1, uuid.toString());

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DocumentEntry(rs.getString("data"));
                }

                return new DocumentEntry();
            }
        }
    }

    private boolean setData(UUID uuid, DocumentEntry data) {
        try {
            return add(uuid, data);
        } catch (SQLException e) {
            if (!JDBCUtil.isUniqueViolation(e)) {
                throw new RuntimeException(e);
            }

            try {
                return update(uuid, data);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private boolean add(UUID uuid, DocumentEntry data) throws SQLException {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("INSERT INTO _table_ (uuid, data) VALUES (?,?)")) {
            ps.setString(1, uuid.toString());
            ps.setObject(2, data.serialize());

            return ps.executeUpdate() > 0;
        }
    }

    private boolean update(UUID uuid, DocumentEntry data) throws SQLException {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("UPDATE _table_ SET data=? where uuid = ?")) {
            ps.setObject(1, data.serialize());
            ps.setString(2, uuid.toString());

            return ps.executeUpdate() > 0;
        }
    }
}
