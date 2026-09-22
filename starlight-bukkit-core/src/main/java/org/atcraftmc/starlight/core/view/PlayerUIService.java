package org.atcraftmc.starlight.core.view;

import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import me.gb2022.gluon.service.ServiceLayer;
import org.atcraftmc.starlight.api.event.PlayerViewInitEvent;
import org.atcraftmc.starlight.api.event.ui.PlayerUIDismountEvent;
import org.atcraftmc.starlight.api.event.ui.PlayerUIMountEvent;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.atcraftmc.starlight.framework.BukkitService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@ApplicationService(layer = ServiceLayer.FOUNDATION, id = "player-ui", impl = PlayerUIService.PlayerUIServiceImpl.class)
public interface PlayerUIService extends BukkitService {
    String TRACKING = "starlight:ui-tracking";

    @ServiceInject
    ServiceHolder<PlayerUIService> INSTANCE = new ServiceHolder<>();

    static PlayerUIService instance() {
        return INSTANCE.get();
    }

    static PlayerView getInstance(final Player player) {
        return instance().getView(player);
    }

    PlayerView getView(Player player);

    void registerRendererChannel(String id, Function<PlayerView, PlayerViewChannelRenderer> provider);

    void unregisterRendererChannel(String id);

    void attachCallback(UITrackingStateCallback callback);

    void detachCallback(UITrackingStateCallback callback);

    final class PlayerUIServiceImpl implements PlayerUIService {
        private final Map<String, Function<PlayerView, PlayerViewChannelRenderer>> registry = new ConcurrentHashMap<>();
        private final Map<UUID, PlayerView> views = new ConcurrentHashMap<>();
        private final Set<UITrackingStateCallback> callbacks = new HashSet<>();

        @Override
        public void enable() throws Exception {
            registerRendererChannel("starlight:action-bar", (v) -> new PlayerViewChannelRenderer("starlight:action-bar", v));

            BukkitUtil.registerEventListener(this);
        }

        @Override
        public void disable() throws Exception {
            BukkitUtil.unregisterEventListener(this);
        }


        @Override
        public PlayerView getView(Player player) {
            return this.views.computeIfAbsent(player.getUniqueId(), (id) -> createView(player));
        }

        @Override
        public void registerRendererChannel(String id, Function<PlayerView, PlayerViewChannelRenderer> provider) {
            this.registry.put(id, provider);
            for (var view : this.views.values()) {
                var channels = view.getRenderChannels();

                if (!channels.containsKey(id)) {
                    channels.put(id, provider.apply(view));
                }
            }
        }

        @Override
        public void unregisterRendererChannel(String id) {
            this.registry.remove(id);

            for (var view : this.views.values()) {
                view.getRenderChannels().remove(id);
            }
        }

        @Override
        public void attachCallback(final UITrackingStateCallback callback) {
            for (var p : Bukkit.getOnlinePlayers()) {
                callback.startRender(p, getInstance(p));
            }

            this.callbacks.add(callback);
        }

        @Override
        public void detachCallback(final UITrackingStateCallback callback) {
            this.callbacks.remove(callback);

            for (var p : Bukkit.getOnlinePlayers()) {
                callback.stopRender(p, getInstance(p));
            }
        }


        private PlayerView createView(Player player) {
            var view = new PlayerView(player);

            BukkitUtil.callEvent(new PlayerViewInitEvent(player, view), (e) -> {
                var setting = e.getSetting();

                if (setting != null) {
                    view.sync(setting);
                }
            });

            for (var key : this.registry.keySet()) {
                view.getRenderChannels().put(key, this.registry.get(key).apply(view));
            }

            return view;
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

            var i = this.views.remove(player.getUniqueId());
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
