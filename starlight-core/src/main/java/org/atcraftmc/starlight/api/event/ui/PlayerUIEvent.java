package org.atcraftmc.starlight.api.event.ui;

import org.atcraftmc.starlight.core.event.CustomEvent;
import org.atcraftmc.starlight.core.view.PlayerView;
import org.bukkit.entity.Player;

public abstract class PlayerUIEvent extends CustomEvent {
    private final Player player;
    private final PlayerView view;

    public PlayerUIEvent(Player player, PlayerView view) {
        this.player = player;
        this.view = view;
    }

    public PlayerView getView() {
        return view;
    }

    public Player getPlayer() {
        return player;
    }
}
