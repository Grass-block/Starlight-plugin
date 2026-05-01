package org.atcraftmc.starlight.api.event.ui;

import org.atcraftmc.starlight.core.event.BukkitEvent;
import org.atcraftmc.starlight.core.view.PlayerView;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

@BukkitEvent
public final class PlayerUIMountEvent extends PlayerUIEvent {

    public PlayerUIMountEvent(Player player, PlayerView view) {
        super(player, view);
    }

    public static HandlerList getHandlerList() {
        return getHandlerList(PlayerUIMountEvent.class);
    }
}
