package org.atcraftmc.starlight.core.view;

import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.Service;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.starlight.api.event.ui.PlayerUIDismountEvent;
import org.atcraftmc.starlight.api.event.ui.PlayerUIMountEvent;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.*;

@ApplicationService(id = "player-ui")
public interface PlayerUIService extends Service {
    Map<UUID, PlayerView> INSTANCES = new HashMap<>();
    BukkitListener TRACKER = new BukkitListener();
    String TRACKING = "starlight:ui-tracking";

    @ServiceInject
    static void start() {
        BukkitUtil.registerEventListener(TRACKER);
    }

    @ServiceInject
    static void stop() {
        BukkitUtil.unregisterEventListener(TRACKER);
    }

    static PlayerView getInstance(final Player player) {
        return INSTANCES.computeIfAbsent(player.getUniqueId(), (k) -> new PlayerView(player));
    }

    final class BukkitListener implements Listener {
        private final Set<UITrackingStateCallback> callbacks = new HashSet<>();

        public void attachCallback(final UITrackingStateCallback callback) {
            for (var p : Bukkit.getOnlinePlayers()) {
                callback.startRender(p, getInstance(p));
            }

            this.callbacks.add(callback);
        }

        public void detachCallback(final UITrackingStateCallback callback) {
            this.callbacks.remove(callback);

            for (var p : Bukkit.getOnlinePlayers()) {
                callback.stopRender(p, getInstance(p));
            }
        }

        private void mount(Player player) {
            BukkitUtil.callEvent(new PlayerUIMountEvent(player, getInstance(player)));

            for (var c : this.callbacks) {
                c.startRender(player, getInstance(player));
            }
        }

        private void unmount(Player player) {
            BukkitUtil.callEvent(new PlayerUIDismountEvent(player, getInstance(player)));

            for (var c : this.callbacks) {
                c.stopRender(player, getInstance(player));
            }

            var i = INSTANCES.remove(player.getUniqueId());
            if (i != null) {
                i.destroy();
            }
        }

        @EventHandler
        public void onPlayerJoin(final PlayerJoinEvent event) {
            mount(event.getPlayer());
        }

        @EventHandler
        public void onPlayerRespawn(final PlayerRespawnEvent event) {
            mount(event.getPlayer());
        }

        @EventHandler
        public void onPlayerQuit(final PlayerQuitEvent event) {
            unmount(event.getPlayer());
        }

        @EventHandler
        public void onPlayerDeath(final PlayerDeathEvent event) {
            unmount(event.getEntity());
        }
    }
}
