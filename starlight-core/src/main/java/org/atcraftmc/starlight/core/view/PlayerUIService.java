package org.atcraftmc.starlight.core.view;

import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.Service;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.starlight.foundation.platform.BukkitUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationService(id = "player-ui")
public interface PlayerUIService extends Service {
    Map<UUID, PlayerView> INSTANCES = new HashMap<>();
    BukkitListener LISTENER = new BukkitListener();

    @ServiceInject
    static void start(){
        BukkitUtil.registerEventListener(LISTENER);
    }

    @ServiceInject
    static void stop(){
        BukkitUtil.unregisterEventListener(LISTENER);
    }

    static PlayerView getInstance(final Player player) {
        return INSTANCES.computeIfAbsent(player.getUniqueId(), (k) -> new PlayerView(player));
    }

    final class BukkitListener implements Listener {
        @EventHandler
        public void onPlayerQuit(final PlayerQuitEvent event) {
            var i = INSTANCES.remove(event.getPlayer().getUniqueId());
            if (i != null) {
                i.destroy();
            }
        }

        @EventHandler
        public void onPlayerLogin(final PlayerJoinEvent event) {
            final Player player = event.getPlayer();

            getInstance(player);
        }
    }
}
