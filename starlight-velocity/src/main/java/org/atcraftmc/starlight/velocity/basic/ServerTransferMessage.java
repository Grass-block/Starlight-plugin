package org.atcraftmc.starlight.velocity.basic;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.starlight.velocity.api.WrappedPlayer;
import org.atcraftmc.starlight.velocity.api.event.ProxyJoinedEvent;
import org.atcraftmc.starlight.velocity.api.event.RemotePlayerLeftEvent;
import org.atcraftmc.starlight.velocity.api.event.RemoteServerConnectEvent;
import org.atcraftmc.starlight.velocity.core.ProxyPlayerDiscoveryService;
import org.atcraftmc.starlight.velocity.framework.VelocityAbstractModule;
import org.atcraftmc.starlight.velocity.util.ServerDisplayName;

import java.util.stream.Stream;

@AutoRegister(Registrations.SERVER_EVENT)
@ApplicationModule(id = "server-transfer-message")
public final class ServerTransferMessage extends VelocityAbstractModule {
    @Subscribe
    public void onRemoteJoin(RemoteServerConnectEvent event) {
        if (event.isInitial()) {
            this.onPlayerJoin(event.getPlayer(), event.getServer().getServerInfo().getName(), false);
        } else {
            var n = event.getServer().getServerInfo().getName();
            var np = event.getPrevious().orElseThrow().getServerInfo().getName();

            this.onPlayerConnect(event.getPlayer(), np, n, false);
        }
    }

    @Subscribe
    public void onRemoteLeft(RemotePlayerLeftEvent event) {
        this.onPlayerDisconnect(event.getPlayer(), event.getServer().getServerInfo().getName());
    }

    private Stream<Player> filterPlayers(WrappedPlayer player, String server, boolean global) {
        var uid = player.getUniqueId();

        return this.getProxyServer().getAllPlayers().stream().filter((p) -> !p.getUniqueId().equals(uid)).filter((p) -> {
            var o = p.getCurrentServer().map(ServerConnection::getServerInfo);

            if (global) {
                return true;
            }

            if (o.isEmpty()) {
                return false;
            }

            return o.orElseThrow().getName().equals(server);
        });
    }

    public void onPlayerJoin(WrappedPlayer player, String server, boolean local) {
        var global = config().value("broadcast-global").bool();
        this.filterPlayers(player, server, global).forEach((a) -> language().item("join-proxy").send(a, player.getUsername()));

        if (local) {
            language().item("join-message").send(QLib.audience(player).getHandle(), player.getUsername());
        }
    }

    public void onPlayerDisconnect(WrappedPlayer player, String server) {
        var global = config().value("broadcast-global").bool();
        this.filterPlayers(player, server, global).forEach((a) -> language().item("leave-proxy").send(a, player.getUsername()));
    }

    public void onPlayerConnect(WrappedPlayer player, String prev, String server, boolean local) {
        var prevDisplay = ServerDisplayName.getDisplayName(prev);
        var serverDisplay = ServerDisplayName.getDisplayName(server);

        this.filterPlayers(player, server, false).forEach((a) -> language().item("join-server").send(a, player.getUsername(), prevDisplay));
        this.filterPlayers(player, prev, false).forEach((a) -> language().item("leave-server")
                .send(a, player.getUsername(), serverDisplay));

        if (local) {
            language().item("transfer-message").send(QLib.audience(player).getHandle(), serverDisplay);
        }
    }


    @Subscribe
    public void onServerConnect(ServerConnectedEvent event) {
        var player = ProxyPlayerDiscoveryService.instance().getPlayer(event.getPlayer().getUniqueId());
        var server = event.getServer().getServerInfo().getName();

        if (ProxyJoinedEvent.isInitialConnect(event)) {
            onPlayerJoin(player, server, true);
        } else {
            var prev = event.getPreviousServer().orElseThrow().getServerInfo().getName();
            onPlayerConnect(player, prev, server, true);
        }
    }

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        var player = ProxyPlayerDiscoveryService.instance().getPlayer(event.getPlayer().getUniqueId());
        var server = event.getPlayer().getCurrentServer().orElseThrow().getServerInfo().getName();

        onPlayerDisconnect(player, server);
    }
}
