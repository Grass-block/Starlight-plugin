package org.atcraftmc.starlight.velocity.api.event;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.atcraftmc.starlight.velocity.api.WrappedPlayer;

public final class RemotePlayerLeftEvent extends SyncedPlayerEvent{
    private final RegisteredServer server;

    public RemotePlayerLeftEvent(WrappedPlayer player,RegisteredServer server) {
        super(player);
        this.server = server;
    }

    public RegisteredServer getServer() {
        return server;
    }
}
