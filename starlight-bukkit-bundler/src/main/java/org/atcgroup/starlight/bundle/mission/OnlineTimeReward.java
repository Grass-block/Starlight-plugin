package org.atcgroup.starlight.bundle.mission;

import me.gb2022.gluon.module.ApplicationModule;
import org.atcgroup.starlight.bundle.mission.reward.RewardInstance;
import org.atcgroup.starlight.bundle.mission.reward.RewardProvider;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.api.event.BukkitEvent;
import org.atcraftmc.starlight.api.event.CustomEvent;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.UUID;

//todo
@ApplicationModule(id = "online-time-reward")
public class OnlineTimeReward extends BukkitAbstractModule {
    public static final RewardProvider<PlayerOnlineTimeRewardEvent> PROVIDER = new OnlineTimeRewardProvider();

    @Override
    public void enable() throws Exception {
        RewardService.instance().getProviderRegistry().register(PlayerOnlineTimeRewardEvent.class, PROVIDER);
    }

    @Override
    public void disable() throws Exception {
        RewardService.instance().getProviderRegistry().unregister(PlayerOnlineTimeRewardEvent.class, PROVIDER);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        var period = config().value("tick-period").intValue();
        var uuid = event.getPlayer().getUniqueId();

        QLib.task().async().timer("online-reward-timer#" + uuid, 0, period, () -> {
            var e = new PlayerOnlineTimeRewardEvent(event.getPlayer());
            BukkitUtil.callEvent(e);
            RewardService.instance().triggerEvent(uuid, e);
        });
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        QLib.task().async().cancel("online-reward-timer#" + event.getPlayer().getUniqueId());
    }

    @BukkitEvent
    public static final class PlayerOnlineTimeRewardEvent extends CustomEvent {
        private final Player player;

        public PlayerOnlineTimeRewardEvent(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }
    }

    private static final class OnlineTimeRewardProvider extends RewardProvider<PlayerOnlineTimeRewardEvent> {
        private OnlineTimeRewardProvider() {
            super(PlayerOnlineTimeRewardEvent.class);
        }

        @Override
        public void handleReward(PlayerOnlineTimeRewardEvent event, UUID receiver, Set<RewardInstance> rewards) {

        }
    }
}
