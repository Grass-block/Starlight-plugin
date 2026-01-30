package org.atcraftmc.starlight.velocity.api.event;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.event.annotation.AwaitingEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

@AwaitingEvent
public final class ProxyJoinedEvent {
    private final Player player;
    private final RegisteredServer server;

    public ProxyJoinedEvent(Player player, RegisteredServer server) {
        this.player = Preconditions.checkNotNull(player, "player");
        this.server = Preconditions.checkNotNull(server, "server");
    }

    public static boolean isInitialConnect(ServerConnectedEvent event) {
        return event.getPreviousServer().isEmpty();
    }

    public Player getPlayer() {
        return this.player;
    }

    public RegisteredServer getServer() {
        return this.server;
    }

    public String toString() {
        return "ProxyJoinedEvent{player=" + this.player + ", server=" + this.server + "}";
    }
}