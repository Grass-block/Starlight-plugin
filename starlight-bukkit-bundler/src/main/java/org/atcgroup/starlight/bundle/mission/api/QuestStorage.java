package org.atcgroup.starlight.bundle.mission.api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.gb2022.commons.jdbc.TableNamedDataService;
import me.gb2022.commons.jdbc.source.SQLMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class QuestStorage extends TableNamedDataService implements RemovalListener<UUID, Quest> {
    private final Cache<UUID, Quest> cache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(3)).build();
    private final QuestRegistry registry;

    public QuestStorage(String table, QuestRegistry registry) {
        super(table);
        this.registry = registry;
    }

    private static String serializeStringList(Set<UUID> list) {
        if (list.isEmpty()) {
            return ";";
        }
        return ";" + list.stream().map(UUID::toString).collect(Collectors.joining(";")) + ";";
    }

    private static Set<UUID> deserializeStringList(String participants) {
        var result = new HashSet<UUID>();

        if (participants == null || participants.isEmpty()) {
            return result;
        }

        for (var part : participants.split(";")) {
            if (part.isBlank()) {
                continue;
            }
            result.add(UUID.fromString(part.trim()));
        }

        return result;
    }

    @Override
    public void initMapper(SQLMapper mapper) {
        super.initMapper(mapper);
        mapper.replaceSQL("_quest_", this.getTableName());
    }

    @Override
    public PreparedStatement createTable(Connection conn) throws SQLException {
        var createTableSQL = """
                CREATE TABLE IF NOT EXISTS _quest_ (
                    uuid CHAR(36) PRIMARY KEY,
                    com_type VARCHAR(128) NOT NULL,
                    creator CHAR(36) NOT NULL,
                    expiration DATE,
                    status INT NOT NULL,
                    participants VARCHAR(16384) NOT NULL,
                    metadata VARCHAR(16384) NOT NULL
                );
                """;

        return conn.prepareStatement(createTableSQL);
    }

    public boolean add(Quest quest) throws SQLException {
        this.cache.put(quest.getUuid(), quest);
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("INSERT INTO _quest_ (uuid, com_type, creator, expiration, status,participants, metadata) VALUES (?,?,?,?,?,?)")) {
            ps.setString(1, quest.getUuid().toString());
            ps.setString(2, this.registry.id(quest.getClass()));
            ps.setString(3, quest.getCreator().toString());
            ps.setDate(4, quest.getExpiration());
            ps.setInt(5, quest.getStatus().getValue());
            ps.setString(6, serializeStringList(quest.getParticipants()));
            ps.setString(7, serialize(quest));
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Quest quest) throws SQLException {
        this.cache.put(quest.getUuid(), quest);
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("UPDATE _quest_ SET com_type=?, creator=?, expiration=?, status=?,participants=?, metadata=? WHERE uuid=?")) {
            ps.setString(1, this.registry.id(quest.getClass()));
            ps.setString(2, quest.getCreator().toString());
            ps.setDate(3, quest.getExpiration());
            ps.setInt(4, quest.getStatus().getValue());
            ps.setString(5, serializeStringList(quest.getParticipants()));
            ps.setString(6, serialize(quest));
            ps.setString(7, quest.getUuid().toString());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(UUID uuid) {
        this.cache.asMap().remove(uuid);
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("DELETE FROM _quest_ WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Quest> byCreator(UUID creator) {
        return query("SELECT * FROM _quest_ WHERE creator=?", ps -> ps.setString(1, creator.toString()));
    }

    public Set<Quest> byType(String type) {
        return query("SELECT * FROM _quest_ WHERE com_type=?", ps -> ps.setString(1, type));
    }

    public Set<Quest> byParticipant(UUID participant) {
        return query("SELECT * FROM _quest_ WHERE participants LIKE ?", ps -> ps.setString(1, "%" + participant + "%"));
    }

    public Optional<Quest> byUUID(UUID uuid) {
        return query("SELECT * FROM _quest_ WHERE uuid=?", ps -> ps.setString(1, uuid.toString())).stream().findFirst();
    }

    public Set<Quest> list() {
        return query("SELECT * FROM _quest_ WHERE status=?", ps -> ps.setInt(1, QuestStatus.OPEN.getValue()));
    }

    public boolean updateStatus(UUID uuid, QuestStatus status) {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("UPDATE _quest_ SET status=? WHERE uuid=?")) {
            ps.setInt(1, status.getValue());
            ps.setString(2, uuid.toString());

            var updated = ps.executeUpdate() > 0;

            var cached = this.cache.getIfPresent(uuid);
            if (cached != null) {
                cached.setStatus(status);
            }

            return updated;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Quest decode(ResultSet rs) throws SQLException {
        var uuid = UUID.fromString(rs.getString("uuid"));
        var creator = UUID.fromString(rs.getString("creator"));
        var expiration = rs.getDate("expiration");
        var status = QuestStatus.fromValue(rs.getInt("status"));
        var metaData = JsonParser.parseString(rs.getString("metadata")).getAsJsonObject();
        var participants = deserializeStringList(rs.getString("participants"));
        return this.registry.create(rs.getString("com_type"), uuid, creator, status, expiration, participants, metaData);
    }

    private Set<Quest> query(String sql, SQLBinder binder) {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement(sql)) {
            binder.bind(ps);

            var result = new HashSet<Quest>();

            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(decode(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public QuestRegistry getRegistry() {
        return registry;
    }

    @FunctionalInterface
    private interface SQLBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private static String serialize(Quest quest) {
        var metadata = new JsonObject();
        quest.serialize(metadata);
        return metadata.toString();
    }

    @Override
    public void onRemoval(RemovalNotification<UUID, Quest> notification) {
        if (notification.getValue() != null) {
            try {
                add(notification.getValue());
            } catch (SQLException e) {
                try {
                    update(notification.getValue());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
