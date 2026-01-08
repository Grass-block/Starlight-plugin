package org.atcraftmc.starlight.velocity.api;

public class RemoteServerConnectEvent {
    private final String player;
    private final String server;
    private final String previous;

    public RemoteServerConnectEvent(String player, String server, String previous) {
        this.player = player;
        this.server = server;
        this.previous = previous;
    }

    public String getPlayer() {
        return player;
    }

    public String getServer() {
        return server;
    }

    public String getPrevious() {
        return previous;
    }

    public boolean isInitial() {
        return previous == null;
    }
}
