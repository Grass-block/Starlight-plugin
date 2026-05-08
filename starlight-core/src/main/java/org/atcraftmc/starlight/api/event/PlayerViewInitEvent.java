package org.atcraftmc.starlight.api.event;

import org.bukkit.entity.Player;
import org.atcraftmc.starlight.core.view.PlayerUISetting;
import org.atcraftmc.starlight.core.view.PlayerView;
import org.bukkit.event.HandlerList;

@BukkitEvent
public final class PlayerViewInitEvent extends CustomEvent {
    private final Player player;
    private final PlayerView view;
    private PlayerUISetting setting = null;

    public PlayerViewInitEvent(Player player, PlayerView view) {
        this.player = player;
        this.view = view;
    }

    public static HandlerList getHandlerList() {
        return getHandlerList(PlayerViewInitEvent.class);
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerView getView() {
        return view;
    }

    public PlayerUISetting getSetting() {
        return setting;
    }

    public void setSetting(PlayerUISetting setting) {
        this.setting = setting;
    }
}
