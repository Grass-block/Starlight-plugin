package org.atcgroup.starlight.bundle.task.reward;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface Reward {
    void add(Player player);
}
