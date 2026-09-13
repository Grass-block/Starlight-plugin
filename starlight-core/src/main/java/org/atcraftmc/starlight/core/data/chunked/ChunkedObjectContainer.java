package org.atcraftmc.starlight.core.data.chunked;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.Set;

public final class ChunkedObjectContainer<V> {
    private final LongSet locks = new LongOpenHashSet(8192);
    private V region;

    public ChunkedObjectContainer(V region) {
        this.region = region;
    }

    public synchronized void lock(long cp) {
        this.locks.add(cp);
    }

    public synchronized void unlock(long cp) {
        this.locks.remove(cp);
    }

    public Set<Long> getLocks() {
        return locks;
    }

    public V get() {
        return region;
    }

    public ChunkedObjectContainer<V> set(V r) {
        this.region = r;
        return this;
    }
}
