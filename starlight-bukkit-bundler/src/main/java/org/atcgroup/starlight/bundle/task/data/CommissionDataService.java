package org.atcgroup.starlight.bundle.task.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.gson.JsonParser;
import me.gb2022.commons.jdbc.TableNamedDataService;
import me.gb2022.commons.jdbc.source.SQLMapper;
import org.bukkit.entity.Player;

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

public final class CommissionDataService extends TableNamedDataService implements RemovalListener<UUID,Commission> {
    private final Cache<UUID,Commission> cache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5)).build();
    private final CommissionRegistry registry;

    public CommissionDataService(String tableName, CommissionRegistry registry) {
        super(tableName);
        this.registry = registry;
    }

    @Override
    public void initMapper(SQLMapper mapper) {
        super.initMapper(mapper);
        mapper.replaceSQL("_commission_", this.getTableName());
    }

    @Override
    public PreparedStatement createTable(Connection conn) throws SQLException {
        var createTableSQL = """
                CREATE TABLE IF NOT EXISTS _commission_ (
                    uuid CHAR(36) PRIMARY KEY,
                    com_type VARCHAR(128) NOT NULL,
                    status INT NOT NULL,
    
                    display_name VARCHAR(128) NOT NULL,
                    creator CHAR(36),
                    description VARCHAR(16384) NOT NULL,
                    participants VARCHAR(16384) NOT NULL,
                    participant_limit INT NOT NULL,
    
                    metadata VARCHAR(16384) NOT NULL,
                    rewards_claimed VARCHAR(16384) NOT NULL
                );
                """;

        return conn.prepareStatement(createTableSQL);
    }

    public boolean add(Commission commission) {
        this.cache.put(commission.getUuid(), commission);
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement(
                "INSERT INTO _commission_ (uuid, com_type, status, display_name, creator, description, participants, participant_limit, metadata, rewards_claimed) VALUES (?,?,?,?,?,?,?,?,?,?)")) {
            ps.setString(1, commission.getUuid().toString());
            ps.setString(2, this.registry.id(commission.getClass()));
            ps.setInt(3, commission.getStatus().getId());
            ps.setString(4, commission.getName());
            ps.setString(5, commission.getCreator().toString());
            ps.setString(6, commission.getDesc());
            ps.setString(7, serializeStringList(commission.getParticipants()));
            ps.setInt(8, (int) commission.getParticipantLimit());
            ps.setString(9, commission.getMetaData().toString());
            ps.setString(10, serializeStringList(commission.getRewardClaimed()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(Commission commission) {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement(
                "UPDATE _commission_ SET com_type=?, status=?, display_name=?, creator=?, description=?, participants=?, participant_limit=?, metadata=?, rewards_claimed=? WHERE uuid=?")) {
            ps.setString(1, this.registry.id(commission.getClass()));
            ps.setInt(2, commission.getStatus().getId());
            ps.setString(3, commission.getName());
            ps.setString(4, commission.getCreator().toString());
            ps.setString(5, commission.getDesc());
            ps.setString(6, serializeStringList(commission.getParticipants()));
            ps.setInt(7, (int) commission.getParticipantLimit());
            ps.setString(8, commission.getMetaData().toString());
            ps.setString(9, serializeStringList(commission.getRewardClaimed()));
            ps.setString(10, commission.getUuid().toString());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(UUID uuid) {
        this.cache.asMap().remove(uuid);
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("DELETE FROM _commission_ WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Commission> byCreator(UUID creator) {
        return query("SELECT * FROM _commission_ WHERE creator=?", ps -> ps.setString(1, creator.toString()));
    }

    public Set<Commission> byParticipant(UUID participant) {
        return query("SELECT * FROM _commission_ WHERE participants LIKE ?", ps -> ps.setString(1, "%" + participant + "%"));
    }

    public Set<Commission> byType(String type) {
        return query("SELECT * FROM _commission_ WHERE com_type=?", ps -> ps.setString(1, type));
    }

    public Optional<Commission> byUUID(UUID uuid) {
        return query("SELECT * FROM _commission_ WHERE uuid=?", ps -> ps.setString(1, uuid.toString())).stream().findFirst();
    }

    public boolean updateStatus(UUID uuid, CommissionStatus status) {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("UPDATE _commission_ SET status=? WHERE uuid=?")) {
            ps.setInt(1, status.getId());
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

    public boolean addRewardClaimed(UUID uuid, UUID player) {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement(
                "UPDATE _commission_ SET rewards_claimed = CONCAT(rewards_claimed, ?) WHERE uuid=? AND rewards_claimed NOT LIKE ?")) {
            ps.setString(1, player + ";");
            ps.setString(2, uuid.toString());
            ps.setString(3, "%;" + player + ";%");

            var updated = ps.executeUpdate() > 0;

            if (updated) {
                var cached = this.cache.getIfPresent(uuid);
                if (cached != null) {
                    cached.getRewardClaimed().add(player);
                }
            }

            return updated;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean removeRewardClaimed(UUID uuid, UUID player) {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement(
                "UPDATE _commission_ SET rewards_claimed = REPLACE(rewards_claimed, ?, '') WHERE uuid=? AND rewards_claimed LIKE ?")) {
            ps.setString(1, player + ";");
            ps.setString(2, uuid.toString());
            ps.setString(3, "%;" + player + ";%");

            var updated = ps.executeUpdate() > 0;

            if (updated) {
                var cached = this.cache.getIfPresent(uuid);
                if (cached != null) {
                    cached.getRewardClaimed().remove(player);
                }
            }

            return updated;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Commission decode(ResultSet rs) throws SQLException {
        var commission = (Commission) this.registry.create(
                UUID.fromString(rs.getString("uuid")),
                rs.getString("com_type"),
                UUID.fromString(rs.getString("creator"))
        );

        var name = rs.getString("display_name");
        var desc = rs.getString("description");
        var limit = rs.getInt("participant_limit");
        var metaData = JsonParser.parseString(rs.getString("metadata")).getAsJsonObject();

        commission.init(name, desc, limit, metaData);
        commission.setStatus(CommissionStatus.fromId(rs.getInt("status")));
        commission.getParticipants().clear();
        commission.getParticipants().addAll(deserializeStringList(rs.getString("participants")));
        commission.getRewardClaimed().clear();
        commission.getRewardClaimed().addAll(deserializeStringList(rs.getString("rewards_claimed")));

        return commission;
    }

    private Set<Commission> query(String sql, SQLBinder binder) {
        try (var c = this.datasource.getConnection(); var ps = c.prepareStatement(sql)) {
            binder.bind(ps);

            var result = new HashSet<Commission>();

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

    public CommissionRegistry getRegistry() {
        return registry;
    }

    @Override
    public void onRemoval(RemovalNotification<UUID, Commission> notification) {
        if (notification.getValue() != null) {
            update(notification.getValue());
        }
    }

    public void join(UUID uuid, Player player){
        var commission = by
    }

    @FunctionalInterface
    private interface SQLBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }
}
