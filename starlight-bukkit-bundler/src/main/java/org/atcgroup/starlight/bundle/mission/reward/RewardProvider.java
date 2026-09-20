package org.atcgroup.starlight.bundle.mission.reward;

import org.atcgroup.starlight.bundle.mission.commission.Commission;

import java.util.Set;
import java.util.UUID;

public abstract class RewardProvider<E> {
    private final Class<E> type;

    protected RewardProvider(Class<E> type) {
        this.type = type;
    }

    public void addReward(Object event, UUID receiver, Set<RewardInstance> rewards) {
        if (!this.type.isInstance(event)) {
            return;
        }

        this.handleReward(this.type.cast(event), receiver, rewards);
    }

    public abstract void handleReward(E event, UUID receiver, Set<RewardInstance> rewards);

    public static <I> RewardProvider<I> create(Class<I> type, Handler<I> handler) {
        return new RewardProvider<>(type) {
            @Override
            public void handleReward(I event, UUID receiver, Set<RewardInstance> rewards) {
                handler.handleReward(event, receiver, rewards);
            }
        };
    }


    public interface Handler<E> {
        void handleReward(E event, UUID receiver, Set<RewardInstance> rewards);
    }

    private static final class CommissionRewardProvider extends RewardProvider<Commission> {
        private CommissionRewardProvider(Class<Commission> type) {
            super(type);
        }

        @Override
        public void handleReward(Commission commission, UUID receiver, Set<RewardInstance> rewards) {
            var meta = commission.getMetaData();

            if (!meta.has("rewards")) {
                return;
            }

            var dom = meta.get("rewards").getAsJsonArray();

            for (var e : dom) {
                var o = e.getAsJsonObject();
                var type = o.get("type").getAsString();
                var data = o.get("data");

                rewards.add(new RewardInstance(type, receiver, data));
            }
        }
    }
}
