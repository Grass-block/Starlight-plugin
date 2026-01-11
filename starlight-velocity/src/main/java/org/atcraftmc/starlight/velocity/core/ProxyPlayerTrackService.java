package org.atcraftmc.starlight.velocity.core;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import me.gb2022.apm.remote.event.APMRemoteEvent;
import me.gb2022.apm.remote.event.EndpointLeftEvent;
import me.gb2022.apm.remote.event.message.RemoteMessageEvent;
import me.gb2022.modular.service.ApplicationService;
import me.gb2022.modular.service.Service;
import me.gb2022.modular.service.ServiceHolder;
import me.gb2022.modular.service.ServiceInject;
import org.atcraftmc.starlight.shared.service.IRemoteMessageService;
import org.atcraftmc.starlight.shared.service.RemoteMessageService;
import org.atcraftmc.starlight.velocity.StarlightVelocity;
import org.atcraftmc.starlight.velocity.api.ProxyJoinedEvent;
import org.atcraftmc.starlight.velocity.api.RemotePlayerLeftEvent;
import org.atcraftmc.starlight.velocity.api.RemoteServerConnectEvent;
import org.atcraftmc.starlight.velocity.util.VelocityUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@ApplicationService(id = "proxy-player-track")
public interface ProxyPlayerTrackService extends Service {
    String MSG_PROXY_JOIN = "/proxy/player/join";//player,server
    String MSG_PROXY_LEAVE = "/proxy/player/leave";//player
    String MSG_PROXY_CONNECT = "/proxy/player/connect";//player,current,previous

    @ServiceInject
    ServiceHolder<ProxyPlayerTrackService> INSTANCE = new ServiceHolder<>();

    static Map<String, String> getAllPlayersInProxy() {
        return INSTANCE.get().getPlayerServerMap();
    }

    static Optional<String> getPlayerServer(String player) {
        return Optional.ofNullable(INSTANCE.get().getPlayerServerMap().get(player));
    }

    Map<String, String> getPlayerProxyMap();

    Map<String, String> getPlayerServerMap();


    final class ServiceImpl implements ProxyPlayerTrackService {
        private final Map<String, String> player2ServerTable = new HashMap<>();
        private final Map<String, String> player2ProxyTable = new HashMap<>();

        private final IRemoteMessageService service = RemoteMessageService.instance();

        @Override
        public Map<String, String> getPlayerProxyMap() {
            return player2ProxyTable;
        }

        @Override
        public Map<String, String> getPlayerServerMap() {
            return player2ServerTable;
        }

        @Override
        public void enable() throws Exception {
            this.service.registerEventHandler(this);
            VelocityUtil.registerListener(this);
        }

        @Override
        public void disable() {
            this.service.registerEventHandler(this);
            VelocityUtil.unregisterListener(this);
        }

        @APMRemoteEvent(MSG_PROXY_JOIN)
        public void onPlayerJointed_R(RemoteMessageEvent event) {
            var data = event.decode(String.class).split(";");
            this.player2ProxyTable.put(data[0], event.sender());
            this.player2ServerTable.put(data[0], data[1]);

            fireEvent(new RemoteServerConnectEvent(data[0], data[1], null));
        }

        @APMRemoteEvent(MSG_PROXY_LEAVE)
        public void onPlayerLeft_R(RemoteMessageEvent event) {
            var player = event.decode(String.class);
            var sv = this.player2ServerTable.get(player);

            this.player2ProxyTable.remove(player);
            this.player2ServerTable.remove(player);

            fireEvent(new RemotePlayerLeftEvent(player, sv));
        }

        @APMRemoteEvent(MSG_PROXY_CONNECT)
        public void onPlayerConnected_R(RemoteMessageEvent event) {
            var data = event.decode(String.class).split(";");
            this.player2ServerTable.put(data[0], data[1]);
            fireEvent(new RemoteServerConnectEvent(data[0], data[1], data[2]));
        }

        @APMRemoteEvent
        public void onServerOffline(EndpointLeftEvent event) {
            var server = event.getServer();

            for (var player : new HashSet<>(this.player2ProxyTable.keySet())) {
                if (!this.player2ProxyTable.get(player).equals(server)) {
                    continue;
                }

                var sv = this.player2ServerTable.get(player);

                this.player2ServerTable.remove(player);
                this.player2ProxyTable.remove(player);

                fireEvent(new RemotePlayerLeftEvent(player, sv));
            }
        }


        private void fireEvent(Object event) {
            StarlightVelocity.instance().getServer().getEventManager().fire(event);
        }


        @Subscribe
        public void onPlayerDisconnect(DisconnectEvent event) {
            this.service.broadcast(MSG_PROXY_LEAVE, event.getPlayer().getUsername());
        }

        @Subscribe
        public void onServerConnect(ServerConnectedEvent event) {
            var server = event.getServer().getServerInfo().getName();
            var playerId = event.getPlayer().getUsername();

            if (ProxyJoinedEvent.isInitialConnect(event)) {
                this.service.broadcast(MSG_PROXY_JOIN, String.join(";", playerId, server));
            } else {
                var prev = event.getPreviousServer().orElseThrow().getServerInfo().getName();
                this.service.broadcast(MSG_PROXY_CONNECT, String.join(";", playerId, server, prev));
            }
        }
    }
}
