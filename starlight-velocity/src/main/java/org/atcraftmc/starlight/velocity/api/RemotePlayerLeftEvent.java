package org.atcraftmc.starlight.velocity.api;

public final class RemotePlayerLeftEvent {
    private final String player;
    private final String server;

    public RemotePlayerLeftEvent(String player, String server) {
        this.player = player;
        this.server = server;
    }

    public String getPlayer() {
        return player;
    }

    public String getServer() {
        return server;
    }
}
