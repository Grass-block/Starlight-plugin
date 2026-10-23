package org.atcgroup.starlight.bundle.oddities.model;

public record ModelVector(float x, float y, float z) {
    public static final ModelVector ZERO = new ModelVector(0, 0, 0);
}
