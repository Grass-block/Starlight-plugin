package org.atcgroup.starlight.bundle.oddities.model;

/**
 * One cuboid in a decoded Blockbench model. Coordinates are in Minecraft blocks.
 */
public record DisplayBlockModel(
        String blockId,
        ModelVector from,
        ModelVector to,
        ModelVector pivot,
        ModelVector rotation
) {
}
