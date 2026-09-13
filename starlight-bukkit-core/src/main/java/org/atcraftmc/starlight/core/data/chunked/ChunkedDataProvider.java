package org.atcraftmc.starlight.core.data.chunked;

import org.atcraftmc.starlight.util.UUIDMapped;

import java.util.Set;

@FunctionalInterface
public interface ChunkedDataProvider<V extends UUIDMapped> {
    Set<V> load(String worldId, int wx0, int wz0, int wx1, int wz1);
}
