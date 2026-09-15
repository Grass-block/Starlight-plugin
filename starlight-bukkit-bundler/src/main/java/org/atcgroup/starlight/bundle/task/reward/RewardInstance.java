package org.atcgroup.starlight.bundle.task.reward;

import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.UUID;

public final class RewardInstance {
    private final UUID uuid;
    private final String type;
    private final UUID receiver;
    private final JsonObject metadata;
    private boolean claimed;
    private Instant created;

    public RewardInstance(UUID uuid, String type, UUID receiver, JsonObject metadata, boolean claimed, Instant created) {
        this.uuid = uuid;
        this.type = type;
        this.receiver = receiver;
        this.metadata = metadata;
        this.claimed = claimed;
        this.created = created;
    }

    public RewardInstance(UUID uuid, String type, UUID receiver, JsonObject metadata) {
        this(uuid, type, receiver, metadata, false, Instant.now());
    }

    public RewardInstance(String type, UUID receiver, JsonObject metadata) {
        this(UUID.randomUUID(), type, receiver, metadata);
    }

    public UUID getUuid() {
        return uuid;
    }

    public JsonObject getMetadata() {
        return metadata;
    }

    public String getType() {
        return type;
    }

    public UUID getReceiver() {
        return receiver;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }
}
