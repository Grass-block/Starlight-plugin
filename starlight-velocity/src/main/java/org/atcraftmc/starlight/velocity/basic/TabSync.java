package org.atcraftmc.starlight.velocity.basic;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.player.TabList;
import com.velocitypowered.api.proxy.player.TabListEntry;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.starlight.velocity.api.WrappedPlayer;
import org.atcraftmc.starlight.velocity.api.event.RemotePlayerLeftEvent;
import org.atcraftmc.starlight.velocity.api.event.RemoteServerConnectEvent;
import org.atcraftmc.starlight.velocity.core.ProxyPlayerDiscoveryService;
import org.atcraftmc.starlight.velocity.framework.VelocityAbstractModule;
import org.atcraftmc.starlight.velocity.util.ServerDisplayName;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@AutoRegister(Registrations.SERVER_EVENT)
@ApplicationModule(id = "tab-sync")
public final class TabSync extends VelocityAbstractModule {

    @Inject
    private ProxyServer server;

    @Override
    public void enable() {
        getProxyServer().getScheduler().buildTask(getProxy(), () -> {
            for (var player : this.server.getAllPlayers()) {
                discover(player);
            }
        }).repeat(1, TimeUnit.SECONDS).schedule();
    }

    @Subscribe
    public void onServerConnect(ServerConnectedEvent event) {
        this.setPlayer(ProxyPlayerDiscoveryService.instance().getPlayer(event.getPlayer().getUniqueId()));
        discover(event.getPlayer());
    }

    @Subscribe
    public void onPlayerLogout(DisconnectEvent event) {
        this.removePlayer(ProxyPlayerDiscoveryService.instance().getPlayer(event.getPlayer().getUniqueId()));
    }

    @Subscribe
    public void onDiscoveredPlayerConnect(RemoteServerConnectEvent event) {
        this.setPlayer(event.getPlayer());
    }

    @Subscribe
    public void onDiscoveredPlayerQuit(RemotePlayerLeftEvent event) {
        this.removePlayer(event.getPlayer());
    }

    private void setPlayerInfo(TabList list, WrappedPlayer player) {
        var template = config().value("format").string();
        var server = player.getConnectedServer().map(RegisteredServer::getServerInfo);

        if (server.isEmpty()) {
            return;
        }

        var serverInfo = server.get();
        var entryId = player.getUniqueId();

        var displayName = ServerDisplayName.getDisplayName(serverInfo.getName());
        var tabEntryName = QLib.textBuilder().buildComponent(template.formatted(displayName, player.getUsername()));

        var entry = list.getEntry(entryId);

        if (entry.isPresent() && Objects.equals(entry.get().getProfile().getId(), entryId)) {
            var e = entry.get();

            e.setDisplayName(tabEntryName);
            e.setLatency(1);
            return;
        }

        var builder = TabListEntry.builder();

        builder.profile(player.getGameProfile());
        builder.displayName(tabEntryName);
        builder.latency(1);
        builder.listed(true);
        builder.tabList(list);

        if (list.containsEntry(entryId)) {
            list.removeEntry(entryId);
        }

        list.addEntry(builder.build());
    }

    public void setPlayer(WrappedPlayer player) {
        getProxyServer().getAllPlayers().stream().filter((p) -> p != player.getHandle()).filter((p) -> {
            var s = p.getCurrentServer();
            var s1 = player.getConnectedServer();

            if (s.isEmpty() || s1.isEmpty()) {
                return false;
            }

            return !Objects.equals(s.get().getServerInfo().getName(), s1.get().getServerInfo().getName());
        }).forEach((p) -> this.setPlayerInfo(p.getTabList(), player));
    }

    public void removePlayer(WrappedPlayer player) {
        getProxyServer().getAllPlayers().stream().filter((p) -> p != player.getHandle()).forEach((p) -> {
            var uuid = player.getUniqueId();
            p.getTabList().removeEntry(uuid);
        });
    }

    public void discover(Player player) {
        for (var p : ProxyPlayerDiscoveryService.instance().getAllPlayers().values()) {
            var s1 = p.getConnectedServer();
            var s2 = player.getCurrentServer();

            if (s1.isEmpty() || s2.isEmpty()) {
                continue;
            }

            if (s1.get().getServerInfo().getName().equals(s2.get().getServerInfo().getName())) {
                continue;
            }

            this.setPlayerInfo(player.getTabList(), p);
        }
    }
}
