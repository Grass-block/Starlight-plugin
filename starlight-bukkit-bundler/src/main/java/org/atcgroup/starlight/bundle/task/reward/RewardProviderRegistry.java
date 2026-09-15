package org.atcgroup.starlight.bundle.task.reward;

import org.atcgroup.starlight.bundle.task.data.Commission;
import org.atcgroup.starlight.bundle.task.data.CommissionRegistry;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class RewardProviderRegistry {
    private final CommissionRegistry registry;
    private final Map<String, Set<RewardProvider>> pipelines = new HashMap<>();

    public RewardProviderRegistry(CommissionRegistry registry) {
        this.registry = registry;
    }

    public Set<Reward> getRewardsFor(Commission commission) {
        var result = new HashSet<Reward>();
        var type = this.registry.id(commission.getClass());

        for (var p : this.pipelines.get("_global")) {
            p.addReward(commission, result);
        }

        if (this.pipelines.containsKey(type)) {
            for (var p : this.pipelines.get(type)) {
                p.addReward(commission, result);
            }
        }

        return result;
    }

    public void provideReward(Commission commission, Player player) {
        if(commission.getRewardClaimed().contains(player.getUniqueId())) {
            return;
        }

        for (var r : getRewardsFor(commission)) {
            r.add(player);
        }

        commission.getRewardClaimed().add(player.getUniqueId());
    }
}
