package org.atcraftmc.starlight.core.data.region;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.atcraftmc.starlight.core.objects.Region;
import org.atcraftmc.starlight.core.objects.WorldAABB;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public final class WorldRegionMonitorCache<V extends Region> {
    public static final int MAX_CAPACITY = 65536;
    public static final int V_CHUNK_SIZE = 64;
    public static final int V_CHUNK_SIZE_BIT = 6;

    private final Cache<UUID, RegionContainer<V>> regionCache;
    private final Long2ObjectMap<VirtualChunk> virtualChunkCache = new Long2ObjectOpenHashMap<>(MAX_CAPACITY);
    private final Cache<Long, Object> virtualChunkTrackCache;
    private final RegionDataProvider<V> listener;
    private final String world;

    public WorldRegionMonitorCache(String world, RegionDataProvider<V> listener) {
        this.world = world;
        this.listener = listener;
        this.regionCache = CacheBuilder.newBuilder()
                .expireAfterAccess(20, TimeUnit.SECONDS)
                .maximumSize(MAX_CAPACITY)
                .removalListener(this::handleRegionCacheRemove)
                .build();
        this.virtualChunkTrackCache = CacheBuilder.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(3))
                .maximumSize(MAX_CAPACITY)
                .removalListener(this::handleTrackCacheRemove)
                .build();
    }

    public static long getEncoded(int cx, int cz) {
        return ((long) cx << 32) | (cz & 0xFFFFFFFFL);
    }

    public static int unpackX(long v) {
        return (int) (v >> 32);
    }

    public static int unpackY(long v) {
        return (int) v;
    }

    //chunk-size = 64
    public synchronized void markLoadCP(int cx, int cz) {
        var key = getEncoded(cx, cz);

        this.virtualChunkTrackCache.put(key, new Object());

        if (this.virtualChunkCache.containsKey(key)) {
            return;
        }

        this.virtualChunkCache.put(key, loadVirtualChunk(cx, cz));
    }

    public synchronized void markLoad(int wx, int wz) {
        markLoadCP(wx >> 6, wz >> 6);
    }

    private synchronized void unload(int cx, int cz) {
        var key = getEncoded(cx, cz);

        if (!this.virtualChunkCache.containsKey(key)) {
            return;
        }

        var vchunk = this.virtualChunkCache.get(key);

        for (var id : vchunk) {
            if (!this.regionCache.asMap().containsKey(id)) {
                continue;
            }

            var region = this.regionCache.asMap().get(id);

            region.unlock(key);
        }
    }

    private VirtualChunk loadVirtualChunk(int cx, int cz) {
        var wx0 = cx * V_CHUNK_SIZE;
        var wx1 = wx0 + V_CHUNK_SIZE - 1;
        var wz0 = cz * V_CHUNK_SIZE;
        var wz1 = wz0 + V_CHUNK_SIZE - 1;

        var data = this.listener.load(this.world, wx0, wz0, wx1, wz1);
        var vc = new VirtualChunk(cx, cz);

        for (var r : data) {
            try {
                var container = this.getRegionCache().get(r.getUuid(), () -> new RegionContainer<>(r)).setRegion(r);
                container.lock(getEncoded(cx, cz));
                vc.add(r.getUuid());
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        return vc;
    }

    public void handleRegionCacheRemove(RemovalNotification<UUID, RegionContainer<V>> notification) {
        var container = notification.getValue();
        var uuid = notification.getKey();

        if (container == null || uuid == null) {
            return;
        }

        if (container.getLocks().isEmpty()) {
            return;
        }

        this.regionCache.put(notification.getKey(), container);
    }

    public void handleTrackCacheRemove(RemovalNotification<Long, Object> notification) {
        if (notification.getValue() == null || notification.getKey() == null) {
            return;
        }

        var cx = unpackX(notification.getKey());
        var cz = unpackY(notification.getKey());

        this.unload(cx, cz);
    }

    public RegionDataProvider<V> getListener() {
        return listener;
    }

    public Cache<UUID, RegionContainer<V>> getRegionCache() {
        return regionCache;
    }

    public Set<UUID> getRegionContained(int wx, int wz) {
        var cx = wx >> 6;
        var cz = wz >> 6;

        markLoadCP(cx, cz);
        return this.virtualChunkCache.get(getEncoded(cx, cz));
    }

    public Set<UUID> getRegionContained(WorldAABB region) {
        var result = new HashSet<UUID>();

        var cx0 = ((int) Math.floor(region.getMinPoint().getX())) >> 6;
        var cz0 = ((int) Math.floor(region.getMinPoint().getZ())) >> 6;
        var cx1 = ((int) Math.floor(region.getMaxPoint().getX())) >> 6;
        var cz1 = ((int) Math.floor(region.getMaxPoint().getZ())) >> 6;

        for (int xx = cx0; xx <= cx1; xx++) {
            for (int zz = cz0; zz <= cz1; zz++) {
                markLoadCP(xx, zz);
                result.addAll(this.virtualChunkCache.get(getEncoded(xx, zz)));
            }
        }

        return result;
    }

    public RegionContainer<V> getRegion(UUID uuid) {
        return this.regionCache.asMap().get(uuid);
    }


    public void invalidate() {
        this.regionCache.invalidateAll();
        this.virtualChunkCache.clear();
        this.virtualChunkTrackCache.invalidateAll();
    }
}
