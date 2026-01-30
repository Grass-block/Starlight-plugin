package org.atcraftmc.starlight.velocity.api.event;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.atcraftmc.starlight.velocity.api.WrappedPlayer;

import java.util.Optional;

public final class RemoteServerConnectEvent extends SyncedPlayerEvent {
    private final RegisteredServer server;
    private final RegisteredServer previous;

    public RemoteServerConnectEvent(WrappedPlayer player, RegisteredServer server, RegisteredServer previous) {
        super(player);
        this.server = server;
        this.previous = previous;
    }

    public RegisteredServer getServer() {
        return server;
    }

    public Optional<RegisteredServer> getPrevious() {
        return Optional.ofNullable(previous);
    }

    public boolean isInitial() {
        return previous == null;
    }
}
