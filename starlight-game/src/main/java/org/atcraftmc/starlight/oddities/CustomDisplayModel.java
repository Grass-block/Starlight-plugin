package org.atcraftmc.starlight.oddities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface CustomDisplayModel {
    Quaternionf NO_ROTATION = new Quaternionf();

    static List<BlockDisplay> create(Location location, File file, Vector3f rel) {
        JsonObject json;

        try (var in = new FileInputStream(file)) {
            json = JsonParser.parseString(new String(in.readAllBytes())).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return loadModel(location, json, rel);
    }

    static Transformation getTransformation(JsonObject cube, Vector3f rel) {
        var origin = cube.getAsJsonArray("origin");
        var size = cube.getAsJsonArray("size");
        var rotation = cube.getAsJsonArray("rotation");
        var pivot = cube.getAsJsonArray("pivot");

        var x = origin.get(0).getAsFloat() / 16;
        var y = origin.get(1).getAsFloat() / 16;
        var z = origin.get(2).getAsFloat() / 16;

        var sx = size.get(0).getAsFloat() / 16;
        var sy = size.get(1).getAsFloat() / 16;
        var sz = size.get(2).getAsFloat() / 16;

        var rx = 0f;
        var ry = 0f;
        var rz = 0f;

        var px = 0f;
        var py = 0f;
        var pz = 0f;

        if (pivot != null) {
            px = pivot.get(0).getAsFloat() / 16;
            py = pivot.get(1).getAsFloat() / 16;
            pz = pivot.get(2).getAsFloat() / 16;
        }

        var v_position = new Vector3f(x, y, z);
        var v_scale = new Vector3f(sx, sy, sz);
        var q = new Quaternionf();

        if (rotation != null) {
            rx = (float) Math.toRadians(rotation.get(0).getAsFloat());
            ry = (float) Math.toRadians(rotation.get(1).getAsFloat());
            rz = (float) Math.toRadians(rotation.get(2).getAsFloat());

            q.rotateXYZ(rx, ry, rz);

            var v_deltaPivot = new Vector3f(x - px, y - py, z - pz);
            var v_rotatedDeltaPivot = q.transform(new Vector3f(v_deltaPivot));
            var movement = v_rotatedDeltaPivot.sub(v_deltaPivot);

            v_position.add(movement);
        }

        return new Transformation(v_position.add(rel), q, v_scale, NO_ROTATION);
    }

    static List<BlockDisplay> loadModel(Location location, JsonObject dom, Vector3f rel) {
        var result = new ArrayList<BlockDisplay>();
        var world = location.getWorld();

        var geometries = dom.getAsJsonArray("minecraft:geometry");
        var bones = geometries.get(0).getAsJsonObject().getAsJsonArray("bones");

        for (var e : bones) {
            var bone = e.getAsJsonObject();
            var cubes = bone.getAsJsonArray("cubes");

            if (cubes == null) {
                continue;
            }

            for (var cubeElement : cubes) {
                var cube = cubeElement.getAsJsonObject();
                var display = world.spawn(location, BlockDisplay.class);
                var block = Material.matchMaterial(cube.get("material").getAsString()).createBlockData();

                display.setShadowRadius(0);
                display.setShadowStrength(0);
                display.setTransformation(getTransformation(cube, rel));

                display.setBlock(block);

                result.add(display);
            }
        }

        return result;
    }
}
