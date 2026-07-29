package org.atcraftmc.starlight.proxy;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.gb2022.apm.remote.RemoteMessenger;
import me.gb2022.apm.remote.event.APMRemoteEvent;
import me.gb2022.apm.remote.event.message.RemoteMessageEvent;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import me.gb2022.gluon.module.component.ComponentProvider;
import me.gb2022.simpnet.util.BufferUtil;
import net.kyori.adventure.text.Component;
import org.atcraftmc.qlib.bukkit.ComponentSerializer;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.language.Language;
import org.atcraftmc.starlight.APMChannels;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.framework.module.SLModuleComponent;
import org.atcraftmc.starlight.shared.RemoteMessageService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationModule(id = "out-proxy-info-sync", defaultEnable = false)
@ComponentProvider({OutProxyInfoSync.PaperListener.class, OutProxyInfoSync.BukkitListener.class})
@AutoRegister({Registrations.PLUGIN_VPN_EVENT,Registrations.SERVER_EVENT})
public final class OutProxyInfoSync extends BukkitAbstractModule {
    private final Map<String, String> remotePlayers = new ConcurrentHashMap<>();
    private final Map<String, String> remotePlayerServers = new ConcurrentHashMap<>();

    @APMRemoteEvent(APMChannels.CHAT_SYNC)
    public void onOutProxyChatMessage(RemoteMessenger ctx, RemoteMessageEvent event) {
        var audience = QLib.context().audiences();

        var args = event.decode(String.class).split("::");

        var player = args[0];
        var server = args[1];
        var message = ComponentSerializer.json(args[2]);

        var template = Language.format(config().value("chat-sync-template").string(),server, player);
        var line = QLib.textBuilder().buildComponent(template).append(message);

        audience.players().sendMessage(line);
        audience.console().sendMessage(Component.text("[proxy]"+player+": ").append(message));
    }

    @APMRemoteEvent(APMChannels.PROXY_JOIN)
    public void onOutProxyJoinSync(RemoteMessenger ctx, RemoteMessageEvent event) {
        var buffer = event.message();
        var uuid = BufferUtil.readString(buffer);
        var name = BufferUtil.readString(buffer);
        var server = BufferUtil.readString(buffer);

        this.remotePlayers.put(uuid, name);
        this.remotePlayerServers.put(uuid, server);
    }

    @APMRemoteEvent(APMChannels.PROXY_CONNECT)
    public void onOutProxyConnectSync(RemoteMessenger ctx, RemoteMessageEvent event) {
        var buffer = event.message();
        var uuid = BufferUtil.readString(buffer);
        var server = BufferUtil.readString(buffer);
        BufferUtil.readString(buffer);

        this.remotePlayerServers.put(uuid, server);
    }

    @APMRemoteEvent(APMChannels.PROXY_LEAVE)
    public void onOutProxyLeftSync(RemoteMessenger ctx, RemoteMessageEvent event) {
        var uuid = event.decode(String.class);

        this.remotePlayers.remove(uuid);
        this.remotePlayerServers.remove(uuid);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var server = RemoteMessageService.instance().getConnector().getIdentifier();

        RemoteMessageService.instance().broadcast(APMChannels.PROXY_JOIN, (b) -> {
            BufferUtil.writeString(b, event.getPlayer().getUniqueId().toString());
            BufferUtil.writeString(b, event.getPlayer().getName());
            BufferUtil.writeString(b, server);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        RemoteMessageService.instance().broadcast(APMChannels.PROXY_LEAVE, event.getPlayer().getUniqueId().toString());
    }

    public void send(Player player, Component message) {
        var server = RemoteMessageService.instance().getConnector().getIdentifier();
        var pl = player.getName();
        var msg = ComponentSerializer.json(message);

        RemoteMessageService.instance().broadcast(APMChannels.CHAT_SYNC, String.join("::", pl, server, msg));
    }

    @AutoRegister({Registrations.SERVER_EVENT})
    public static final class BukkitListener extends SLModuleComponent<OutProxyInfoSync> {
        @Override
        public void checkCompatibility() throws APIIncompatibleException {
            try {
                Class.forName("io.papermc.paper.event.player.AsyncChatEvent");
                throw new APIIncompatibleException("assertion failed");
            } catch (ClassNotFoundException ignored) {
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onChat(AsyncPlayerChatEvent event) {
            this.parent.send(event.getPlayer(), Component.text(event.getMessage()));
        }
    }

    @AutoRegister({Registrations.SERVER_EVENT})
    public static final class PaperListener extends SLModuleComponent<OutProxyInfoSync> {
        @Override
        public void checkCompatibility()   throws APIIncompatibleException {
            Compatibility.requireClass(() -> Class.forName("io.papermc.paper.event.player.AsyncChatEvent"));
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onChat(AsyncChatEvent event) {
            this.parent.send(event.getPlayer(), event.message());
        }
    }
}
