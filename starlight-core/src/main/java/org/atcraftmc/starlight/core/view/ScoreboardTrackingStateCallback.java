package org.atcraftmc.starlight.core.view;

import org.atcraftmc.starlight.core.VisualScoreboardService;
import org.bukkit.entity.Player;

public interface ScoreboardTrackingStateCallback {
    void mount(Player player, VisualScoreboardService.VisualScoreboard scoreboard);

    default void unmount(Player player, VisualScoreboardService.VisualScoreboard scoreboard){}
}
