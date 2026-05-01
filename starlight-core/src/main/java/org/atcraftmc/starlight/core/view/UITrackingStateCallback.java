package org.atcraftmc.starlight.core.view;

import org.bukkit.entity.Player;

public interface UITrackingStateCallback {
    void startRender(Player player, PlayerView ui);

    void stopRender(Player player, PlayerView ui);
}
