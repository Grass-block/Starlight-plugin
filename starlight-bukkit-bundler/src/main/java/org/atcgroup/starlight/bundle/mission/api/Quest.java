package org.atcgroup.starlight.bundle.mission.api;

import com.google.gson.JsonObject;
import org.atcraftmc.qlib.bukkit.QLibBukkitContext;
import org.atcraftmc.qlib.language.LocalizedMessageSupplier;
import org.bukkit.entity.Player;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Quest {
    protected final UUID uuid;
    protected final UUID creator;
    protected final JsonObject data;
    protected final Date expiration;
    protected final Set<UUID> participants;
    protected QuestStatus status;
    protected LocalizedMessageSupplier displayName;
    protected LocalizedMessageSupplier description;

    public Quest(UUID uuid, UUID creator, Date expiration, LocalizedMessageSupplier displayName, LocalizedMessageSupplier description) {
        this.uuid = uuid;
        this.creator = creator;
        this.expiration = expiration;
        this.status = QuestStatus.OPEN;
        this.displayName = displayName;
        this.description = description;
        this.data = new JsonObject();
        this.participants = new HashSet<>();
    }

    public Quest(UUID uuid, UUID creator, QuestStatus status, Date expiration, Set<UUID> participants, JsonObject metadata) {
        this.uuid = uuid;
        this.creator = creator;
        this.data = metadata;
        this.status = status;
        this.displayName = LocalizedMessageSupplier.deserialize(QLibBukkitContext.global(), metadata.getAsJsonObject("__name"));
        this.description = LocalizedMessageSupplier.deserialize(QLibBukkitContext.global(), metadata.getAsJsonObject("__desc"));
        this.expiration = expiration;
        this.participants = participants;

        this.deserializeMeta(metadata);
    }

    public void serialize(JsonObject dom) {
        dom.add("__name", LocalizedMessageSupplier.serialize(this.displayName));
        dom.add("__desc", LocalizedMessageSupplier.serialize(this.description));

        this.serializeMeta(dom);
    }

    public abstract void serializeMeta(JsonObject meta);

    public abstract void deserializeMeta(JsonObject meta);


    public final JsonObject getData() {
        this.serializeMeta(this.data);
        return data;
    }

    public final UUID getUuid() {
        return uuid;
    }

    public final UUID getCreator() {
        return creator;
    }

    public final Date getExpiration() {
        return expiration;
    }

    public final QuestStatus getStatus() {
        return status;
    }

    public final void setStatus(QuestStatus status) {
        this.status = status;
    }

    public final LocalizedMessageSupplier getDisplayName() {
        return displayName;
    }

    public final void setDisplayName(LocalizedMessageSupplier displayName) {
        this.displayName = displayName;
    }

    public final LocalizedMessageSupplier getDescription() {
        return description;
    }

    public final void setDescription(LocalizedMessageSupplier description) {
        this.description = description;
    }

    public final Set<UUID> getParticipants() {
        return participants;
    }


    public void onPlayerJoined(Player player) {
    }

    public void onPlayerLeft(Player player) {
    }

    public String getFormattedMetadata(JsonObject metadata) {
        return "['NO DATA']";
    }
}
