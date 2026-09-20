package org.atcgroup.starlight.bundle.mission.reward;

import java.util.*;

public final class RewardProviderRegistry {
    private final Map<Class<?>, Set<RewardProvider<?>>> pipelines = new HashMap<>();

    public <E> void register(Class<E> type, RewardProvider<E> provider) {
        this.pipelines.computeIfAbsent(type, s -> new HashSet<>()).add(provider);
    }

    public <E> void unregister(Class<E> type, RewardProvider<E> provider) {
        var set = this.pipelines.computeIfAbsent(type, s -> new HashSet<>());

        set.remove(provider);

        if (set.isEmpty()) {
            this.pipelines.remove(type);
        }
    }

    public <E> HashSet<RewardInstance> triggerEvent(UUID receiver, E event) {
        var type = event.getClass();
        if (!this.pipelines.containsKey(type)) {
            return null;
        }

        var providers = this.pipelines.get(type);
        var result = new HashSet<RewardInstance>();
        for (var provider : providers) {
            provider.addReward(event, receiver, result);
        }

        return result;
    }
}
