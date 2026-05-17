package org.atcraftmc.starlight.api;

import org.atcraftmc.starlight.api.event.BukkitEvent;
import org.atcraftmc.starlight.api.event.CustomEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

@BukkitEvent
public final class PlayerFirstJoinEvent extends CustomEvent {
    private final Player player;

    public PlayerFirstJoinEvent(Player player) {
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return getHandlerList(PlayerFirstJoinEvent.class);
    }

    public Player getPlayer() {
        return player;
    }
}
