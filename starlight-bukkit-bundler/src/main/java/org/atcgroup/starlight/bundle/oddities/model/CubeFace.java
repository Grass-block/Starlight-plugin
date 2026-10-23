package org.atcgroup.starlight.bundle.oddities.model;

public record CubeFace(String blockId, float[] uv) {
    public CubeFace {
        uv = uv == null ? new float[0] : uv.clone();
    }

    @Override
    public float[] uv() {
        return uv.clone();
    }
}
