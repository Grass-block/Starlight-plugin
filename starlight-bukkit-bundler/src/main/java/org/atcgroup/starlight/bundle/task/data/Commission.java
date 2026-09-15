package org.atcgroup.starlight.bundle.task.data;

import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Commission {
    private JsonObject metaData = new JsonObject();
    private CommissionStatus status = CommissionStatus.OPEN;
    private final Set<UUID> participants = new HashSet<>();
    private final Set<UUID> rewardClaimed = new HashSet<>();
    private final UUID uuid;
    private final UUID creator;
    private int participantLimit = 1;
    private String name;
    private String desc;

    protected Commission(UUID uuid, UUID creator) {
        this.uuid = uuid;
        this.creator = creator;
    }

    public void init(String name, String desc, int maxParticipants,JsonObject metaData) {
        this.name = name;
        this.desc = desc;
        this.participantLimit = maxParticipants;
        this.metaData = metaData;
    }

    public final Set<UUID> getParticipants() {
        return participants;
    }

    public UUID getCreator() {
        return creator;
    }

    public long getParticipantLimit() {
        return participantLimit;
    }

    public Set<UUID> getRewardClaimed() {
        return rewardClaimed;
    }

    public CommissionStatus getStatus() {
        return status;
    }

    public void setStatus(CommissionStatus status) {
        this.status = status;
    }

    public final boolean addParticipant(final UUID participant) {
        if (this.participants.size() >= this.participantLimit) {
            return false;
        }

        if (!this.participants.add(participant)) {
            return false;
        }

        this.onParticipantAdded(participant);
        return true;
    }

    public final void removeParticipant(final UUID participant) {
        this.participants.remove(participant);
        this.onParticipantRemoved(participant);
    }

    public final void setParticipantLimit(int participantLimit) {
        this.participantLimit = participantLimit;
    }

    public void onParticipantAdded(final UUID participant) {
    }

    public void onParticipantRemoved(final UUID participant) {
    }

    public JsonObject getMetaData() {
        return metaData;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }
}
