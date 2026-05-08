package org.atcraftmc.starlight.core.data.region;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.Set;

public final class RegionContainer<V extends Region> {
    private final LongSet locks = new LongOpenHashSet(1024);
    private V region;

    public RegionContainer(V region) {
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

    public V getRegion() {
        return region;
    }

    public RegionContainer<V> setRegion(V r) {
        this.region = r;
        return this;
    }
}
