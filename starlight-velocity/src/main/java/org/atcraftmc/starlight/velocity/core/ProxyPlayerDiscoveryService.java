package org.atcraftmc.starlight.velocity.core;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import me.gb2022.apm.remote.RemoteMessenger;
import me.gb2022.apm.remote.event.APMRemoteEvent;
import me.gb2022.apm.remote.event.EndpointLeftEvent;
import me.gb2022.apm.remote.event.connector.ConnectorReadyEvent;
import me.gb2022.apm.remote.event.message.RemoteMessageEvent;
import me.gb2022.gluon.service.*;
import me.gb2022.simpnet.util.BufferUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.shared.service.IRemoteMessageService;
import org.atcraftmc.starlight.shared.service.RemoteMessageService;
import org.atcraftmc.starlight.velocity.StarlightVelocity;
import org.atcraftmc.starlight.velocity.api.WrappedPlayer;
import org.atcraftmc.starlight.velocity.api.event.ProxyJoinedEvent;
import org.atcraftmc.starlight.velocity.api.event.RemotePlayerLeftEvent;
import org.atcraftmc.starlight.velocity.api.event.RemoteServerConnectEvent;
import org.atcraftmc.starlight.velocity.util.VelocityUtil;

import java.util.*;

@ApplicationService(id = "proxy-player-track", impl = ProxyPlayerDiscoveryService.ServiceImpl.class, layer = ServiceLayer.USER)
public interface ProxyPlayerDiscoveryService extends Service {
    Logger LOGGER = SLPluginEnvironment.createLogger("ProxyDiscoveryService");

    String MSG_PROXY_JOIN = "sync:join";
    String MSG_PROXY_LEAVE = "sync:leave";
    String MSG_PROXY_CONNECT = "sync:connect";
    String MSG_PROXY_ACT_CONNECT = "sync:act-connect";
    String MSG_DISCOVER_PLAYER = "sync:discover";

    @ServiceInject
    ServiceHolder<ProxyPlayerDiscoveryService> INSTANCE = new ServiceHolder<>();

    static ProxyPlayerDiscoveryService instance() {
        return INSTANCE.get();
    }

    WrappedPlayer getPlayer(UUID player);

    WrappedPlayer getPlayer(String player);

    Map<UUID, WrappedPlayer> getAllPlayers();

    Set<String> getAllPlayerNames();


    final class ServiceImpl implements ProxyPlayerDiscoveryService {
        private final Map<UUID, WrappedPlayer> playerUuidMap = new HashMap<>();
        private final Map<String, WrappedPlayer> playerIdMap = new HashMap<>();
        private final ProxyServer server = StarlightVelocity.instance().getServer();
        private IRemoteMessageService service;

        @Override
        public void enable() throws Exception {
            this.service = RemoteMessageService.instance();
            this.service.registerEventHandler(this);
            VelocityUtil.registerListener(this);
        }

        @Override
        public void disable() {
            this.service.registerEventHandler(this);
            VelocityUtil.unregisterListener(this);
        }


        @Subscribe(order = PostOrder.LAST)
        public void onPlayerLeft(DisconnectEvent event) {
            this.service.broadcast(MSG_PROXY_LEAVE, event.getPlayer().getUniqueId().toString());

            var plugin = StarlightVelocity.instance();

            plugin.getServer().getScheduler()
                    .buildTask(plugin, () -> {
                        this.playerUuidMap.remove(event.getPlayer().getUniqueId());
                        this.playerIdMap.remove(event.getPlayer().getUsername());
                    })
                    .schedule();
        }

        @Subscribe
        public void onPlayerJoin(ServerConnectedEvent event) {
            var player = new WrappedPlayer.LocalPlayer(event.getPlayer());

            this.playerUuidMap.put(event.getPlayer().getUniqueId(), player);
            this.playerIdMap.put(event.getPlayer().getUsername(), player);

            if (ProxyJoinedEvent.isInitialConnect(event)) {
                this.service.broadcast(MSG_PROXY_JOIN, (b) -> {
                    BufferUtil.writeString(b, event.getPlayer().getUniqueId().toString());
                    BufferUtil.writeString(b, event.getPlayer().getUsername());
                    BufferUtil.writeString(b, event.getServer().getServerInfo().getName());
                });
            } else {
                var prev = event.getPreviousServer().orElseThrow().getServerInfo().getName();
                this.service.broadcast(MSG_PROXY_CONNECT, (b) -> {
                    BufferUtil.writeString(b, event.getPlayer().getUniqueId().toString());
                    BufferUtil.writeString(b, event.getServer().getServerInfo().getName());
                    BufferUtil.writeString(b, prev);
                });
            }
        }

        @APMRemoteEvent
        public void onConnectorReady(RemoteMessenger ctx, ConnectorReadyEvent event) {
            LOGGER.info("joined network, discovering players...");
            ctx.broadcast(MSG_DISCOVER_PLAYER, "");
        }

        @APMRemoteEvent(MSG_DISCOVER_PLAYER)
        public void onDiscoverPlayer(RemoteMessenger ctx, RemoteMessageEvent event) {
            LOGGER.info("Received discover request from {}, collecting...", event.sender());

            var counter = 0;

            for (var player : StarlightVelocity.instance().getServer().getAllPlayers()) {
                var sv = player.getCurrentServer();

                if (sv.isEmpty()) {
                    continue;
                }

                counter++;

                ctx.message(event.sender(), MSG_PROXY_JOIN, (b) -> {
                    BufferUtil.writeString(b, player.getUniqueId().toString());
                    BufferUtil.writeString(b, player.getUsername());
                    BufferUtil.writeString(b, sv.orElseThrow().getServerInfo().getName());
                });
            }

            LOGGER.info("Sent {} players to {}.", counter, event.sender());
        }

        @APMRemoteEvent(MSG_PROXY_JOIN)
        public void onPlayerJointed_R(RemoteMessenger ctx, RemoteMessageEvent event) {
            var proxy = event.sender();
            var msg = event.message();

            var uuid = UUID.fromString(BufferUtil.readString(msg));
            var name = BufferUtil.readString(msg);
            var server = BufferUtil.readString(msg);

            LOGGER.info("Discovered player join: {}({}) -> {}", name, uuid, server);

            var player = new WrappedPlayer.RemotePlayer(uuid, proxy, name, server);

            this.playerUuidMap.put(uuid, player);
            this.playerIdMap.put(name, player);

            fireEvent(new RemoteServerConnectEvent(player, player.getConnectedServer().orElseThrow(), null));
        }

        @APMRemoteEvent(MSG_PROXY_LEAVE)
        public void onPlayerLeft_R(RemoteMessenger ctx, RemoteMessageEvent event) {
            var uuid = UUID.fromString(event.decode(String.class));
            var player = getPlayer(uuid);

            LOGGER.info("Discovered player left: {}({})", player.getUsername(), uuid);

            fireEvent(new RemotePlayerLeftEvent(player, player.getConnectedServer().orElse(null)));

            this.playerUuidMap.remove(uuid);
            this.playerIdMap.remove(player.getUsername());
        }

        @APMRemoteEvent(MSG_PROXY_CONNECT)
        public void onPlayerConnected_R(RemoteMessenger ctx, RemoteMessageEvent event) {
            var b = event.message();

            var uuid = UUID.fromString(BufferUtil.readString(b));
            var server = BufferUtil.readString(b);
            var prev = BufferUtil.readString(b);
            var player = ((WrappedPlayer.RemotePlayer) getPlayer(uuid));

            LOGGER.info("Discovered player connect: {}({}) {} -> {}", player.getUsername(), uuid, prev, server);

            player.setServer(server);

            var ps = this.server.getServer(server).orElse(null);
            var pv = this.server.getServer(prev).orElse(null);
            fireEvent(new RemoteServerConnectEvent(player, ps, pv));
        }

        @APMRemoteEvent(MSG_PROXY_ACT_CONNECT)
        public void onRemoteConnectRequest(RemoteMessenger ctx, RemoteMessageEvent event) {
            var b = event.message();

            var uuid = UUID.fromString(BufferUtil.readString(b));
            var server = BufferUtil.readString(b);

            this.server.getPlayer(uuid).ifPresent((p) -> {
                var sv = this.server.getServer(server).orElseThrow();
                p.createConnectionRequest(sv).connect();

                LOGGER.info("Handled player stp request: {}({}) {}: {}", p.getUsername(), uuid, event.sender(), server);
            });
        }

        @APMRemoteEvent
        public void onServerOffline(RemoteMessenger ctx, EndpointLeftEvent event) {
            var server = event.getServer();

            for (var e : new HashSet<>(this.playerUuidMap.entrySet())) {
                var uuid = e.getKey();
                var player = e.getValue();

                if (!player.getConnectedServer().orElseThrow().getServerInfo().getName().equals(server)) {
                    continue;
                }

                this.playerUuidMap.remove(uuid);
                this.playerIdMap.remove(player.getUsername());

                fireEvent(new RemotePlayerLeftEvent(player, this.server.getServer(server).orElse(null)));
            }
        }


        @Override
        public WrappedPlayer getPlayer(UUID player) {
            return this.playerUuidMap.get(player);
        }

        @Override
        public WrappedPlayer getPlayer(String player) {
            return this.playerIdMap.get(player);
        }

        @Override
        public Map<UUID, WrappedPlayer> getAllPlayers() {
            return this.playerUuidMap;
        }

        @Override
        public Set<String> getAllPlayerNames() {
            return this.playerIdMap.keySet();
        }


        private void fireEvent(Object event) {
            StarlightVelocity.instance().getServer().getEventManager().fire(event);
        }
    }
}
