package org.atcgroup.starlight.bundle.task.reward;

import org.atcgroup.starlight.bundle.task.data.Commission;

import java.util.Set;

public interface RewardProvider {
    RewardProvider COMMISSION_ITEM_PROVIDER = new CommissionRewardProvider();
    RewardProvider SYSTEM_ITEM_PROVIDER = new SystemRewardProvider();

    void addReward(Commission commission, Set<Reward> rewards);

    final class SystemRewardProvider implements RewardProvider {
        @Override
        public void addReward(Commission commission, Set<Reward> rewards) {
            //todo: 添加系统奖励输出器
        }
    }

    final class CommissionRewardProvider implements RewardProvider {
        @Override
        public void addReward(Commission commission, Set<Reward> rewards) {
            var meta = commission.getMetaData();

            if (!meta.has("item_rewards")) {
                return;
            }

            rewards.add(new ItemReward(meta.get("item_rewards")));
        }
    }

}
