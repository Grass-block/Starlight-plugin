package org.atcraftmc.starlight.core.data.region;

import java.util.HashSet;
import java.util.UUID;

public final class VirtualChunk extends HashSet<UUID> {
    private final int x;
    private final int z;

    public VirtualChunk(int x, int z) {
        this.x = x;
        this.z = z;
    }
}
