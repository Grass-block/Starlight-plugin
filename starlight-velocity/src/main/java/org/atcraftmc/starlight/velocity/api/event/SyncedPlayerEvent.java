package org.atcraftmc.starlight.velocity.api.event;

import org.atcraftmc.starlight.velocity.api.WrappedPlayer;

public abstract class SyncedPlayerEvent {
    private final WrappedPlayer player;

    public SyncedPlayerEvent(WrappedPlayer player) {
        this.player = player;
    }

    public final WrappedPlayer getPlayer() {
        return player;
    }
}
