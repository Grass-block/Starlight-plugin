package org.atcgroup.starlight.bundle.task.reward;

import com.google.gson.JsonElement;
import org.atcgroup.starlight.bundle.task.data.Commission;
import org.atcraftmc.starlight.core.platform.Players;
import org.atcraftmc.starlight.util.PersistentItemStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;

public final class ItemReward implements Reward {
    private final PersistentItemStorage storage;

    public ItemReward(JsonElement json) {
        this.storage = new PersistentItemStorage(json.getAsJsonArray());
    }

    @Override
    public void add(Player player) {
        for (var item : this.storage) {
            Players.give(player, item);
        }
    }
}

