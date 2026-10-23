package org.atcgroup.starlight.bundle.quest.api;

import com.google.gson.JsonObject;
import org.atcraftmc.qlib.bukkit.QLibBukkitContext;
import org.atcraftmc.qlib.language.LocalizedMessageSupplier;

import java.util.UUID;

public abstract class Quest {
    protected final UUID uuid;
    protected final UUID creator;
    protected final JsonObject data;
    protected LocalizedMessageSupplier displayName;
    protected LocalizedMessageSupplier description;

    public Quest(UUID uuid, UUID creator, LocalizedMessageSupplier displayName, LocalizedMessageSupplier description) {
        this.uuid = uuid;
        this.creator = creator;
        this.displayName = displayName;
        this.description = description;
        this.data = new JsonObject();
    }

    public Quest(UUID uuid, UUID creator, JsonObject metadata) {
        this.uuid = uuid;
        this.creator = creator;
        this.data = metadata;
        this.displayName = LocalizedMessageSupplier.deserialize(QLibBukkitContext.global(), metadata.getAsJsonObject("__name"));
        this.description = LocalizedMessageSupplier.deserialize(QLibBukkitContext.global(), metadata.getAsJsonObject("__desc"));

        this.deserializeMeta(metadata);
    }

    public void serialize(JsonObject dom) {
        dom.add("__name", LocalizedMessageSupplier.serialize(this.displayName));
        dom.add("__desc", LocalizedMessageSupplier.serialize(this.description));

        this.serializeMeta(dom);
    }

    public abstract void serializeMeta(JsonObject meta);

    public abstract void deserializeMeta(JsonObject meta);


    public JsonObject getData() {
        return data;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getCreator() {
        return creator;
    }

    public LocalizedMessageSupplier getDisplayName() {
        return displayName;
    }

    public void setDisplayName(LocalizedMessageSupplier displayName) {
        this.displayName = displayName;
    }

    public LocalizedMessageSupplier getDescription() {
        return description;
    }

    public void setDescription(LocalizedMessageSupplier description) {
        this.description = description;
    }
}
