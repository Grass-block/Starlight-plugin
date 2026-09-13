package org.atcraftmc.starlight.core.data.regional;

interface RegionLoadHandler {
    void load(int cx, int cz);

    void unload(int cx, int cz);
}
