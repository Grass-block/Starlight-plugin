package me.gb2022.commons.jdbc.trait;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UUIDQuery<V> extends DataServiceAttachment<V> {
    default Optional<V> byUUID(UUID uuid) throws SQLException {
        try (var c = this.getGenerateQuerySource().getConnection(); var p = c.prepareStatement("SELECT * FROM _table_ WHERE uuid = ?")) {
            p.setString(1, uuid.toString());

            try (var rs = p.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(decode(rs));
                }
                return Optional.empty();
            }
        }
    }

    default Set<UUID> listUUIDs() throws SQLException {
        var result = new HashSet<UUID>();
        try (var c = this.getGenerateQuerySource().getConnection(); var p = c.prepareStatement("SELECT uuid FROM _table_")) {
            try (var rs = p.executeQuery()) {
                while (rs.next()) {
                    result.add(UUID.fromString(rs.getString("uuid")));
                }
            }
        }

        return result;
    }

    default boolean existUUID(UUID uuid) throws SQLException {
        try (var c = this.getGenerateQuerySource().getConnection(); var p = c.prepareStatement("SELECT 42 FROM _table_ where uuid = ?")) {
            p.setString(1, uuid.toString());

            try (var rs = p.executeQuery()) {
                return rs.next();
            }
        }
    }

    default boolean delete(UUID uuid) throws SQLException {
        try (var c = this.getGenerateQuerySource().getConnection(); var p = c.prepareStatement("DELETE FROM _table_ WHERE uuid = ?")) {
            p.setString(1, uuid.toString());
            onUpdate();
            return p.executeUpdate() > 0;
        }
    }
}
