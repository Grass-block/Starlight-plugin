package org.atcraftmc.starlight.data.jdbc.service;

import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

public interface TagMap {
    Set<String> get(UUID uuid) throws SQLException;

    boolean has(UUID uuid, String data) throws SQLException;

    boolean hasEntry(UUID uuid) throws SQLException;

    void add(UUID uuid, String data) throws SQLException;

    void delete(UUID uuid, String data) throws SQLException;
}
