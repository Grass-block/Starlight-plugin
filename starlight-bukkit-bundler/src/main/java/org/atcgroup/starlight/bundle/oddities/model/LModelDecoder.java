package org.atcgroup.starlight.bundle.oddities.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;

/** Decodes Blockbench Bedrock geometry JSON into BlockDisplay-ready cuboids. */
public final class LModelDecoder {
    private static final float PIXELS_PER_BLOCK = 16.0f;

    private LModelDecoder() {
    }

    public static DisplayModel decode(Path file) throws IOException {
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return decode(JsonParser.parseReader(reader).getAsJsonObject());
        }
    }

    public static DisplayModel decode(String json) {
        return decode(JsonParser.parseString(json).getAsJsonObject());
    }

    public static DisplayModel decode(JsonObject root) {
        var textures = readTextures(root.getAsJsonArray("textures"));
        var blocks = new ArrayList<DisplayBlockModel>();
        var geometries = root.getAsJsonArray("minecraft:geometry");
        var elements = root.getAsJsonArray("elements");
        if (elements != null) {
            decodeElements(elements, textures, blocks);
        }
        if (geometries == null || geometries.size() == 0) {
            if (elements == null || elements.size() == 0) {
                throw new IllegalArgumentException("Blockbench model is missing elements or minecraft:geometry");
            }
            return new DisplayModel(
                    divide(vector(root.get("BasePos"), ModelVector.ZERO), PIXELS_PER_BLOCK),
                    vector(root.get("BaseRot"), ModelVector.ZERO),
                    blocks
            );
        }

        for (var geometryElement : geometries) {
            var geometry = geometryElement.getAsJsonObject();
            var bones = geometry.getAsJsonArray("bones");
            if (bones == null) {
                continue;
            }
            for (var boneElement : bones) {
                var bone = boneElement.getAsJsonObject();
                var cubes = bone.getAsJsonArray("cubes");
                if (cubes == null) {
                    continue;
                }
                var bonePivot = vector(bone.get("pivot"), ModelVector.ZERO);
                for (var cubeElement : cubes) {
                    var cube = cubeElement.getAsJsonObject();
                    var from = requiredVector(cube, "origin");
                    var size = requiredVector(cube, "size");
                    var to = add(from, size);
                    var pivot = vector(cube.get("pivot"), bonePivot);
                    var rotation = vector(cube.get("rotation"), ModelVector.ZERO);

                    blocks.add(new DisplayBlockModel(
                            resolveBlockId(cube, textures),
                            divide(from, PIXELS_PER_BLOCK),
                            divide(to, PIXELS_PER_BLOCK),
                            divide(pivot, PIXELS_PER_BLOCK),
                            rotation
                    ));
                }
            }
        }

        return new DisplayModel(
                divide(vector(root.get("BasePos"), ModelVector.ZERO), PIXELS_PER_BLOCK),
                vector(root.get("BaseRot"), ModelVector.ZERO),
                blocks
        );
    }

    /** Decodes Blockbench Java-model elements to Minecraft resource-pack cubes. */
    public static java.util.List<MinecraftCube> decodeMinecraftCubes(Path file) throws IOException {
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return decodeMinecraftCubes(JsonParser.parseReader(reader).getAsJsonObject());
        }
    }

    public static java.util.List<MinecraftCube> decodeMinecraftCubes(JsonObject root) {
        var elements = root.getAsJsonArray("elements");
        if (elements == null) {
            throw new IllegalArgumentException("Minecraft model is missing elements");
        }
        var textures = readMinecraftTextures(root.getAsJsonObject("textures"));
        var result = new ArrayList<MinecraftCube>(elements.size());
        for (var element : elements) {
            result.add(decodeMinecraftCube(element.getAsJsonObject(), textures));
        }
        return java.util.List.copyOf(result);
    }

    private static MinecraftCube decodeMinecraftCube(JsonObject element, Map<String, String> textures) {
        var from = divide(requiredVector(element, "from"), 16);
        var to = divide(requiredVector(element, "to"), 16);
        var rotation = decodeRotation(element.get("rotation"));
        var rotationObject = element.getAsJsonObject("rotation");
        var pivot = rotationObject == null ? ModelVector.ZERO
                : divide(vector(rotationObject.get("origin"), ModelVector.ZERO), 16);
        var facesObject = element.getAsJsonObject("faces");
        if (facesObject == null) {
            throw new IllegalArgumentException("Minecraft cube is missing faces");
        }

        var faces = new LinkedHashMap<String, CubeFace>();
        String common = null;
        var same = true;
        for (var side : new String[]{"north", "east", "south", "west", "up", "down"}) {
            var face = facesObject.getAsJsonObject(side);
            if (face == null) {
                continue;
            }
            var id = resolveMinecraftTexture(face.get("texture"), textures);
            var uv = readUv(face.getAsJsonArray("uv"));
            faces.put(side, new CubeFace(id, uv));
            if (common == null) {
                common = id;
            } else if (!common.equals(id)) {
                same = false;
            }
        }
        if (common == null) {
            throw new IllegalArgumentException("Minecraft cube contains no textured faces");
        }
        if (same) {
            return new DirectCube(common, from, to, pivot, rotation);
        }
        return new FaceBasedCube(from, to, pivot, rotation, faces);
    }

    private static Map<String, String> readMinecraftTextures(JsonObject textures) {
        var result = new HashMap<String, String>();
        if (textures == null) {
            return result;
        }
        for (var entry : textures.entrySet()) {
            if (entry.getValue().isJsonPrimitive()) {
                result.put(entry.getKey(), normalizeBlockId(entry.getValue().getAsString()));
            }
        }
        return result;
    }

    private static String resolveMinecraftTexture(JsonElement texture, Map<String, String> textures) {
        if (texture == null || !texture.isJsonPrimitive()) {
            throw new IllegalArgumentException("Minecraft face is missing texture");
        }
        var key = texture.getAsString();
        if (key.startsWith("#")) {
            key = key.substring(1);
        }
        var result = textures.get(key);
        if (result == null) {
            throw new IllegalArgumentException("Unknown Minecraft texture reference: " + texture);
        }
        return result;
    }

    private static float[] readUv(JsonArray uv) {
        if (uv == null || uv.size() != 4) {
            return new float[0];
        }
        return new float[]{uv.get(0).getAsFloat(), uv.get(1).getAsFloat(), uv.get(2).getAsFloat(), uv.get(3).getAsFloat()};
    }

    private static void decodeElements(JsonArray elements, Map<String, String> textures,
                                       ArrayList<DisplayBlockModel> blocks) {
        for (var element : elements) {
            var cube = element.getAsJsonObject();
            var from = requiredVector(cube, "from");
            var to = requiredVector(cube, "to");
            var pivot = vector(cube.get("origin"), ModelVector.ZERO);
            var rotation = decodeRotation(cube.get("rotation"));
            var rotationData = cube.getAsJsonObject("rotation");
            if (rotationData != null) {
                pivot = vector(rotationData.get("origin"), pivot);
            }
            blocks.add(new DisplayBlockModel(
                    resolveBlockId(cube, textures),
                    divide(from, PIXELS_PER_BLOCK),
                    divide(to, PIXELS_PER_BLOCK),
                    divide(pivot, PIXELS_PER_BLOCK),
                    rotation
            ));

        }
    }

    private static Map<String, String> readTextures(JsonArray textures) {
        var result = new HashMap<String, String>();
        if (textures == null) {
            return result;
        }
        for (var i = 0; i < textures.size(); i++) {
            var texture = textures.get(i).getAsJsonObject();
            var blockId = firstString(texture, "blockId", "block_id", "name", "id", "path");
            if (blockId == null || blockId.isBlank()) {
                continue;
            }
            blockId = normalizeBlockId(blockId);
            result.put(Integer.toString(i), blockId);
            putIfPresent(result, texture.get("uuid"), blockId);
            putIfPresent(result, texture.get("id"), blockId);
            putIfPresent(result, texture.get("name"), blockId);
            putIfPresent(result, texture.get("path"), blockId);
        }
        return result;
    }

    private static String resolveBlockId(JsonObject cube, Map<String, String> textures) {
        var explicit = firstString(cube, "blockId", "block_id", "material");
        if (explicit != null) {
            return normalizeBlockId(explicit);
        }
        var faces = cube.getAsJsonObject("faces");
        if (faces != null) {
            for (var face : faces.entrySet()) {
                var faceData = face.getValue().getAsJsonObject();
                var texture = faceData.get("texture");
                if (texture == null || texture.isJsonNull()) {
                    continue;
                }
                var blockId = textures.get(texture.getAsString());
                if (blockId == null && texture.isJsonPrimitive() && texture.getAsJsonPrimitive().isNumber()) {
                    blockId = textures.get(Integer.toString(texture.getAsInt()));
                }
                if (blockId != null) {
                    return blockId;
                }
            }
        }
        throw new IllegalArgumentException("Cannot resolve blockId from cube texture metadata");
    }

    private static ModelVector decodeRotation(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return ModelVector.ZERO;
        }
        if (element.isJsonArray()) {
            return vector(element, ModelVector.ZERO);
        }
        var object = element.getAsJsonObject();
        if (object.has("angle") && object.has("axis")) {
            var angle = object.get("angle").getAsFloat();
            return switch (object.get("axis").getAsString().toLowerCase()) {
                case "x" -> new ModelVector(angle, 0, 0);
                case "y" -> new ModelVector(0, angle, 0);
                case "z" -> new ModelVector(0, 0, angle);
                default -> throw new IllegalArgumentException("Unknown cube rotation axis: " + object.get("axis"));
            };
        }
        return vector(element, ModelVector.ZERO);
    }

    private static String normalizeBlockId(String value) {
        var id = value.replace('\\', '/').trim();
        var namespace = id.indexOf(':');
        var namespacePrefix = namespace >= 0
                ? id.substring(id.lastIndexOf('/', Math.max(0, namespace - 1)) + 1, namespace + 1)
                : "minecraft:";
        var path = namespace >= 0 ? id.substring(namespace + 1) : id;
        if (namespace < 0 && path.startsWith("block/")) {
            path = path.substring("block/".length());
        }
        var start = path.lastIndexOf('/') + 1;
        path = path.substring(start);
        var dot = path.lastIndexOf('.');
        if (dot > 0) {
            path = path.substring(0, dot);
        }
        return namespacePrefix + path;
    }

    private static String firstString(JsonObject object, String... keys) {
        for (var key : keys) {
            var value = object.get(key);
            if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
                return value.getAsString();
            }
        }
        return null;
    }

    private static void putIfPresent(Map<String, String> target, JsonElement key, String value) {
        if (key != null && !key.isJsonNull()) {
            target.put(key.getAsString(), value);
        }
    }

    private static ModelVector requiredVector(JsonObject object, String key) {
        var value = object.get(key);
        if (value == null || value.isJsonNull()) {
            throw new IllegalArgumentException("Bedrock cube is missing " + key);
        }
        return vector(value, ModelVector.ZERO);
    }

    private static ModelVector vector(JsonElement element, ModelVector fallback) {
        if (element == null || element.isJsonNull()) {
            return fallback;
        }
        if (element.isJsonArray()) {
            var array = element.getAsJsonArray();
            if (array.size() != 3) {
                throw new IllegalArgumentException("Expected a 3-component vector");
            }
            return new ModelVector(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
        }
        if (element.isJsonObject()) {
            var object = element.getAsJsonObject();
            return new ModelVector(
                    number(object, "x", fallback.x()),
                    number(object, "y", fallback.y()),
                    number(object, "z", fallback.z())
            );
        }
        throw new IllegalArgumentException("Expected vector as array or object");
    }

    private static float number(JsonObject object, String key, float fallback) {
        var value = object.get(key);
        return value == null || value.isJsonNull() ? fallback : value.getAsFloat();
    }

    private static ModelVector add(ModelVector left, ModelVector right) {
        return new ModelVector(left.x() + right.x(), left.y() + right.y(), left.z() + right.z());
    }

    private static ModelVector divide(ModelVector vector, float divisor) {
        return new ModelVector(vector.x() / divisor, vector.y() / divisor, vector.z() / divisor);
    }
}
