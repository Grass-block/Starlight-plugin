package org.atcgroup.starlight.bundle.oddities.model;

/** A Minecraft resource-pack cube produced from one Blockbench element. */
public sealed interface MinecraftCube permits DirectCube, FaceBasedCube {
    ModelVector from();

    ModelVector to();

    ModelVector pivot();

    ModelVector rotation();
}
