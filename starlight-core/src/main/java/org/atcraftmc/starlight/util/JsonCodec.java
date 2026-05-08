package org.atcraftmc.starlight.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import org.atcraftmc.starlight.core.ComponentSerializer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public interface JsonCodec {
    static JsonObject getItemLore(ItemMeta meta) {
        var obj = new JsonObject();

        if (!meta.hasLore()) {
            return obj;
        }

        try {
            var component = new JsonArray();
            for (var c : Objects.requireNonNull(meta.lore())) {
                component.add(new JsonParser().parse(ComponentSerializer.json(c)));
            }
            obj.add("component", component);
        } catch (Exception ignored) {
        }

        var raw = new JsonArray();
        for (var s : meta.getLore()) {
            raw.add(s);
        }
        obj.add("raw", raw);

        return obj;
    }

    static void setItemLore(ItemMeta meta, JsonObject dom) {
        var strings = new ArrayList<String>();
        for (var s : dom.get("raw").getAsJsonArray()) {
            strings.add(s.getAsJsonPrimitive().getAsString());
        }

        if (!dom.has("component")) {
            meta.setLore(strings);
            return;
        }

        try {
            var components = new ArrayList<Component>();
            for (var obj : dom.get("component").getAsJsonArray()) {
                components.add(ComponentSerializer.json(obj.getAsJsonObject().toString()));
            }

            meta.lore(components);
        } catch (Exception ignored) {
            meta.setLore(strings);
        }
    }

    static JsonObject getItemDisplayName(ItemMeta meta) {
        var obj = new JsonObject();

        try {
            if (meta.displayName() == null) {
                return obj;
            }

            obj.add("component", new JsonParser().parse(ComponentSerializer.json(Objects.requireNonNull(meta.displayName()))));
        } catch (Exception ignored) {
        }

        if (meta.getDisplayName().equals("")) {
            return obj;
        }

        obj.addProperty("raw", meta.getDisplayName());

        return obj;
    }

    static void setItemDisplayName(ItemMeta meta, JsonObject dom) {
        if (!dom.has("component")) {

            return;
        }

        try {
            meta.displayName(ComponentSerializer.json(dom.get("component").getAsJsonObject().toString()));
        } catch (Exception e) {
            meta.setDisplayName(dom.getAsJsonPrimitive("raw").getAsString());
        }
    }

    static JsonObject serializeItem(ItemStack item) {
        var json = new JsonObject();
        var gson = new Gson();

        json.addProperty("type", item.getType().getKey().asString());
        json.addProperty("amount", item.getAmount());

        var meta = item.getItemMeta();
        if (meta == null) {
            return json;
        }

        json.add("display_name", getItemDisplayName(meta));
        json.add("lore", getItemLore(meta));

        if (meta.hasEnchants()) {
            JsonObject enchants = new JsonObject();
            for (Map.Entry<Enchantment, Integer> e : meta.getEnchants().entrySet()) {
                enchants.addProperty(e.getKey().getKey().getKey(), e.getValue());
            }
            json.add("enchants", enchants);
        }

        if (meta.hasCustomModelData()) {
            json.addProperty("custom_model_data", meta.getCustomModelData());
        }

        json.add("flags", gson.toJsonTree(meta.getItemFlags().stream().map(Enum::name).toList()));

        return json;
    }
}
