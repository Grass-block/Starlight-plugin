package org.atcraftmc.starlight.shared.jdbc.document;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.joml.Vector3d;

public interface DocumentFieldCodec<I> {
    DocumentFieldCodec<Vector3d> VECTOR_3D = new DocumentFieldCodec<>() {
        @Override
        public JsonElement encodeJson(Vector3d vector3d) {
            var arr = new JsonArray();
            arr.add(vector3d.x);
            arr.add(vector3d.y);
            arr.add(vector3d.z);
            return arr;
        }

        @Override
        public Vector3d decodeJson(JsonElement jsonElement) {
            var arr = jsonElement.getAsJsonArray();
            if (arr.isEmpty()) {
                return new Vector3d(Double.NaN, Double.NaN, Double.NaN);
            }

            var x = arr.get(0).getAsFloat();
            var y = arr.get(1).getAsFloat();
            var z = arr.get(2).getAsFloat();

            return new Vector3d(x, y, z);
        }
    };

    JsonElement encodeJson(I value);

    I decodeJson(JsonElement value);
}
