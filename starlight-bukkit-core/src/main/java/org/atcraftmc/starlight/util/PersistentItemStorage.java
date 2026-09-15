package org.atcraftmc.starlight.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;

public final class PersistentItemStorage extends HashSet<ItemStack> {
    public static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();
    public static final Gson GSON = new Gson();

    public static JsonObject serialize(ItemStack stack) {
        var json = new JsonObject();

        json.addProperty("native", Base64.getEncoder().encodeToString(stack.serializeAsBytes()));
        json.addProperty("type", stack.getType().getKey().toString());
        json.addProperty("amount", stack.getAmount());
        json.add("bukkit", GSON.toJsonTree(stack.serialize()));

        return json;
    }

    public static ItemStack createFromFallbackSerialize(JsonObject json) {
        var map = (Map<String, Object>) GSON.fromJson(json.get("bukkit"), MAP_TYPE);

        for (var entry : map.entrySet()) {
            var value = entry.getValue();
            var key = entry.getKey();

            if (value instanceof Number &&
                    (key.equals("amount") || key.equals("damage") || key.equals("Data") ||
                            key.equals("Damage") || key.equals("RepairCost"))) {
                map.put(key, ((Number) value).intValue());
            } else {
                map.put(key, value);
            }
        }

        return ItemStack.deserialize(map);
    }

    public static ItemStack deserialize(JsonObject json) {
        var nativeData = Base64.getDecoder().decode(json.get("native").getAsString());
        var type = json.get("type").getAsString();
        var amount = json.get("amount").getAsInt();

        try {
            var item = ItemStack.deserializeBytes(nativeData);
            if (!item.getType().getKey().toString().equals(type)) {
                return createFromFallbackSerialize(json);
            }
            if (item.getAmount() != amount) {
                return createFromFallbackSerialize(json);
            }

            return item;
        } catch (Throwable t) {
            return createFromFallbackSerialize(json);
        }
    }

    public JsonArray serialize() {
        var json = new JsonArray();
        for (var entry : this) {
            json.add(serialize(entry));
        }
        return json;
    }

    public PersistentItemStorage(JsonArray json) {
        for (var entry : json) {
            add(deserialize(entry.getAsJsonObject()));
        }
    }

    public PersistentItemStorage() {
    }
}
