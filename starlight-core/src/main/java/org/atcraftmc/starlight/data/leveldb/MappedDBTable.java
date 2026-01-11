package org.atcraftmc.starlight.data.leveldb;

import org.iq80.leveldb.DB;

import java.nio.charset.StandardCharsets;

public final class MappedDBTable {
    private final String databaseId;
    private final String prefix;
    private DB db = null;

    public MappedDBTable(String databaseId, String prefix) {
        this.databaseId = databaseId;
        this.prefix = prefix;
    }

    public void init() {
        this.db = LevelDBService.getDB(this.databaseId);
    }

    public void checkDB() {
        if (this.db == null) {
            this.init();
            if (this.db == null) {
                throw new IllegalStateException("Failed to initialize DB!!!");
            }
        }
    }

    public String key(String key) {
        return this.prefix + ":" + key;
    }

    public void write(String key, byte[] value) {
        checkDB();
        var k = this.key(key).getBytes(StandardCharsets.UTF_8);

        this.db.put(k, value);
    }

    public byte[] read(String key) {
        checkDB();
        var k = this.key(key).getBytes(StandardCharsets.UTF_8);

        return this.db.get(k);
    }
}
