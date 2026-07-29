package org.atcraftmc.starlight.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

@BukkitEvent
public final class PlayerReadyEvent extends CustomEvent {
    private final Player player;

    public PlayerReadyEvent(Player player) {
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return getHandlerList(PlayerReadyEvent.class);
    }

    public Player getPlayer() {
        return player;
    }
}
