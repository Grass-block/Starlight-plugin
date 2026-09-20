package org.atcgroup.starlight.bundle.mission.reward;

import org.atcraftmc.starlight.core.platform.Players;
import org.atcraftmc.starlight.util.PersistentItemStorage;
import org.bukkit.entity.Player;

public final class ItemReward implements Reward {

    @Override
    public void add(Player player, RewardInstance instance) {
        var storage = new PersistentItemStorage(instance.getMetadata().getAsJsonArray());

        for (var item : storage) {
            Players.give(player, item);
        }
    }
}

