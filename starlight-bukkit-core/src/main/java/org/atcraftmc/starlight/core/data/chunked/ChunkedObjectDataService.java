package org.atcraftmc.starlight.core.data.chunked;

import me.gb2022.commons.jdbc.source.SQLMapper;
import me.gb2022.commons.jdbc.TableNamedDataService;
import me.gb2022.commons.jdbc.trait.UUIDQuery;
import org.atcraftmc.starlight.util.UUIDMapped;

import java.util.concurrent.ConcurrentHashMap;

public abstract class ChunkedObjectDataService<V extends UUIDMapped> extends TableNamedDataService implements ChunkedDataProvider<V>, UUIDQuery<V> {
    private final ConcurrentHashMap<String, ChunkMonitorCache<V>> caches = new ConcurrentHashMap<>();

    protected ChunkedObjectDataService(String tableName) {
        super(tableName);
    }

    @Override
    public void initMapper(SQLMapper mapper) {
        super.initMapper(mapper);
        mapper.replaceSQL("_chunked_", this.getTableName());
    }

    @Override
    public void onUpdate() {
        invalidateCache();
    }

    public final void invalidateCache() {
        for (var cache : this.caches.values()) {
            cache.invalidate();
        }
    }

    public final ChunkMonitorCache<V> getCache(String wid) {
        return this.caches.computeIfAbsent(wid, (w) -> new ChunkMonitorCache<>(w, this));
    }

    public ConcurrentHashMap<String, ChunkMonitorCache<V>> getCaches() {
        return caches;
    }
}
