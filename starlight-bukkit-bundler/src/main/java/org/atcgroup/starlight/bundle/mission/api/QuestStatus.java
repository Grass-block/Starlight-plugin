package org.atcgroup.starlight.bundle.mission.api;

public enum QuestStatus {
    CREATING(0),
    OPEN(1),
    COMPLETED(2),
    CLOSED(3);

    final int value;
    QuestStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static QuestStatus fromValue(int value) {
        for (var status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown quest status value: " + value);
    }
}
