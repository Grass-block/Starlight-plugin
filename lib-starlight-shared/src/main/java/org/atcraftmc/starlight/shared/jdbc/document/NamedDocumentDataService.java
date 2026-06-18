package org.atcraftmc.starlight.shared.jdbc.document;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.gson.JsonObject;
import me.gb2022.commons.math.SHA;
import org.atcraftmc.starlight.data.jdbc.JDBCUtil;
import org.atcraftmc.starlight.data.jdbc.source.SQLMappedDataSource;
import org.atcraftmc.starlight.data.jdbc.source.SQLMapper;
import org.atcraftmc.starlight.shared.JDBCService;
import org.atcraftmc.starlight.shared.jdbc.JDBCDataService;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

public final class NamedDocumentDataService extends JDBCDataService implements RemovalListener<String, DocumentEntry> {
    private final Cache<String, DocumentEntry> cache = CacheBuilder.newBuilder()
            .removalListener(this)
            .expireAfterAccess(Duration.ofSeconds(30))
            .build();
    private final String tableName;

    public NamedDocumentDataService(String tableName) {
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
                    hash_key char(40) PRIMARY KEY,
                    name varchar(1024) NOT NULL UNIQUE,
                    data text default '{}'
                )
                """;
        return conn.prepareStatement(sql);
    }

    @Override
    public void onRemoval(@NotNull RemovalNotification<String, DocumentEntry> notification) {
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
                    setData(k, v);
                }
            });
        }
    }

    public List<String> getAllNames() {
        var ids = new ArrayList<String>();

        try (var conn = this.datasource.getConnection(); var ps = conn.prepareStatement("SELECT name FROM _table_")) {

            try (var rs = ps.executeQuery()) {

                while (rs.next()) {
                    ids.add(rs.getString(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ids;
    }

    public void delete(String... names) {
        try (var conn = this.datasource.getConnection(); var delete = conn.prepareStatement("DELETE FROM _table_ WHERE hash_key = ?")) {


            for (var id : names) {
                var key = SHA.getSHA1(id, false);

                this.cache.invalidate(key);
                delete.setString(1, key);
                delete.addBatch();
            }

            delete.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public JsonObject get(String name) {
        try {
            return cache.get(name, () -> getData(name)).getObject();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    //raw
    private DocumentEntry getData(String name) throws SQLException {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("select data from _table_ where hash_key=?")) {
            ps.setString(1, SHA.getSHA1(name, false));

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DocumentEntry(rs.getString("data"));
                }

                return new DocumentEntry();
            }
        }
    }


    private boolean setData(String name, DocumentEntry data) {
        try {
            return add(name, data);
        } catch (SQLException e) {
            if (!JDBCUtil.isUniqueViolation(e)) {
                throw new RuntimeException(e);
            }

            try {
                return update(name, data);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private boolean add(String name, DocumentEntry data) throws SQLException {
        var key = SHA.getSHA1(name, false);

        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("INSERT INTO _table_ (hash_key, name, data) VALUES (?,?,?)")) {
            ps.setString(1, key);
            ps.setString(2, name);
            ps.setObject(3, data.serialize());

            return ps.executeUpdate() > 0;
        }
    }

    private boolean update(String name, DocumentEntry data) throws SQLException {
        var key = SHA.getSHA1(name, false);

        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("UPDATE _table_ SET data=? where hash_key = ?")) {
            ps.setObject(1, data.serialize());
            ps.setString(2, key);

            return ps.executeUpdate() > 0;
        }
    }
}
