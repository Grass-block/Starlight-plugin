package org.atcraftmc.starlight.core.data.region;

import org.atcraftmc.starlight.core.objects.Region;

import java.util.Set;

@FunctionalInterface
public interface RegionDataProvider<V extends Region> {
    Set<V> load(String worldId, int wx0, int wz0, int wx1, int wz1);
}
