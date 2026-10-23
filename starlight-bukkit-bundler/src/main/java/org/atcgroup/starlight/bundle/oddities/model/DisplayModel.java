package org.atcgroup.starlight.bundle.oddities.model;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/** Decoded cuboids and model-level BasePos/BaseRot overlays. */
public record DisplayModel(ModelVector basePos, ModelVector baseRot, List<DisplayBlockModel> blocks) {
    public DisplayModel {
        blocks = List.copyOf(blocks);
    }

    public List<BlockDisplay> spawn(Location anchor) {
        var world = anchor.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("Anchor location must have a world");
        }

        var spawned = new ArrayList<BlockDisplay>(this.blocks.size());
        var baseRotation = new Quaternionf().rotationXYZ(
                (float) Math.toRadians(this.baseRot.x()),
                (float) Math.toRadians(this.baseRot.y()),
                (float) Math.toRadians(this.baseRot.z())
        );
        var baseOffset = new Vector3f(this.basePos.x(), this.basePos.y(), this.basePos.z());

        for (var block : this.blocks) {
            var from = vector(block.from());
            var to = vector(block.to());
            var pivot = vector(block.pivot());
            var size = new Vector3f(to).sub(from);
            var rotation = vector(block.rotation());
            var elementRotation = new Quaternionf().rotationXYZ(
                    (float) Math.toRadians(rotation.x()),
                    (float) Math.toRadians(rotation.y()),
                    (float) Math.toRadians(rotation.z())
            );

            var position = new Vector3f(from).sub(pivot);
            elementRotation.transform(position).add(pivot);
            baseRotation.transform(position).add(baseOffset);

            var display = world.spawn(anchor, BlockDisplay.class);
            display.setBlock(org.bukkit.Bukkit.createBlockData(block.blockId()));
            display.setShadowRadius(0);
            display.setShadowStrength(0);
            display.setTransformation(new Transformation(
                    position,
                    new Quaternionf(baseRotation).mul(elementRotation),
                    size,
                    new Quaternionf()
            ));
            spawned.add(display);
        }
        return List.copyOf(spawned);
    }

    private static Vector3f vector(ModelVector vector) {
        return new Vector3f(vector.x(), vector.y(), vector.z());
    }
}
