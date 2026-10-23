package org.atcgroup.starlight.bundle.oddities.model;

/** Cube whose six faces use one block texture/model ID. */
public record DirectCube(
        String blockId,
        ModelVector from,
        ModelVector to,
        ModelVector pivot,
        ModelVector rotation
) implements MinecraftCube {
}
