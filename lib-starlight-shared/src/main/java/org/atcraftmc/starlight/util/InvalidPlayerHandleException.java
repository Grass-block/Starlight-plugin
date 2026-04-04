package org.atcraftmc.starlight.util;

import java.util.UUID;

public class InvalidPlayerHandleException extends RuntimeException {
    private final UUID uuid;

    public InvalidPlayerHandleException(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
