package org.atcgroup.starlight.bundle.oddities.model;

import java.util.Map;

/** Cube whose faces use different resource-pack texture/model IDs. */
public record FaceBasedCube(
        ModelVector from,
        ModelVector to,
        ModelVector pivot,
        ModelVector rotation,
        Map<String, CubeFace> faces
) implements MinecraftCube {
    public FaceBasedCube {
        faces = Map.copyOf(faces);
    }
}
