package org.atcraftmc.starlight.shared.jdbc.document;

import com.google.gson.JsonElement;

public interface DocumentFieldCodec<I> {
    JsonElement encodeJson(I value);

    I decodeJson(JsonElement value);
}
