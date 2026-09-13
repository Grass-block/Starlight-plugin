package me.gb2022.commons.jdbc.trait;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public interface NameQuery<V> extends DataServiceAttachment<V> {
    default Optional<V> byName(String name) throws SQLException {
        try (var c = this.getGenerateQuerySource().getConnection(); var p = c.prepareStatement("SELECT * FROM _table_ WHERE name = ?")) {
            p.setString(1, name);

            try (var rs = p.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(decode(rs));
                }
                return Optional.empty();
            }
        }
    }

    default Set<String> listNames() throws SQLException {
        try (var c = this.getGenerateQuerySource().getConnection(); var p = c.prepareStatement("SELECT name FROM _table_")) {
            return queryNames(p);
        }
    }

    default boolean existName(String name) throws SQLException {
        try (var c = this.getGenerateQuerySource().getConnection(); var p = c.prepareStatement("SELECT 42 FROM _table_ where name = ?")) {
            p.setString(1, name);

            try (var rs = p.executeQuery()) {
                return rs.next();
            }
        }
    }

    default boolean delete(String name) throws SQLException {
        try (var c = this.getGenerateQuerySource().getConnection(); var p = c.prepareStatement("DELETE FROM _table_ WHERE name = ?")) {
            p.setString(1, name);
            onUpdate();
            return p.executeUpdate() > 0;
        }
    }

    default Set<String> queryNames(PreparedStatement ps) throws SQLException {
        var result = new HashSet<String>();
        try (var rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getString("name"));
            }
        }

        return result;
    }
}
