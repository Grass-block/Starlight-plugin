package org.atcgroup.starlight.bundle.mission.reward;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface Reward {
    void add(Player player, RewardInstance reward);
}
