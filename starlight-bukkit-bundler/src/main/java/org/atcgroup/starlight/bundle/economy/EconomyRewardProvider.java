package org.atcgroup.starlight.bundle.economy;

import me.gb2022.gluon.module.ApplicationModule;
import org.atcgroup.starlight.bundle.mission.RewardService;
import org.atcgroup.starlight.bundle.mission.reward.Reward;
import org.atcgroup.starlight.bundle.mission.reward.RewardInstance;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.entity.Player;

@ApplicationModule(id = "economy-reward-provider")
public final class EconomyRewardProvider extends BukkitAbstractModule {
    @Override
    public void enable() throws Exception {
        var sm = StarlightBukkitCore.instance().getGluonContext().getServiceManager();
        sm.hookRegister(RewardService.class, (i) -> i.getRegistry().put("starlight:economy", new EconomyReward()));
        sm.hookUnregister(RewardService.class, (i) -> i.getRegistry().remove("starlight:economy"));
    }

    public static final class EconomyReward implements Reward {
        @Override
        public void add(Player player, RewardInstance reward) {
            EconomyService.instance().depositPlayer(player, reward.getMetadata().getAsDouble());
        }
    }
}
