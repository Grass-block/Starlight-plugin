package org.atcgroup.starlight.bundle.mission.commission;

public enum CommissionStatus {
    OPEN(1),
    COMPLETED(2),
    CLOSED(3);


    final int id;

    CommissionStatus(int id) {
        this.id = id;
    }

    public static CommissionStatus fromId(int id) {
        return switch (id) {
            case 1 -> CommissionStatus.OPEN;
            case 2 -> CommissionStatus.CLOSED;
            default -> null;
        };
    }

    public int getId() {
        return id;
    }
}
