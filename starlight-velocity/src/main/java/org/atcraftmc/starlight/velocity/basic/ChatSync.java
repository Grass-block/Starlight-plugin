package org.atcraftmc.starlight.velocity.basic;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import me.gb2022.apm.remote.RemoteMessenger;
import me.gb2022.apm.remote.event.APMRemoteEvent;
import me.gb2022.apm.remote.event.message.RemoteMessageEvent;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.atcraftmc.starlight.shared.RemoteMessageService;
import org.atcraftmc.starlight.velocity.framework.VelocityAbstractModule;
import org.atcraftmc.starlight.velocity.util.ServerDisplayName;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@AutoRegister({Registrations.SERVER_EVENT, Registrations.PLUGIN_VPN_EVENT})
@ApplicationModule(id = "chat-sync")
public final class ChatSync extends VelocityAbstractModule {
    public static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.from("quark_plugin:msg");
    public static final MinecraftChannelIdentifier CHANNEL_LEGACY = MinecraftChannelIdentifier.from("quark_plugin:msg");
    public static final MinecraftChannelIdentifier CHANNEL_NEW = MinecraftChannelIdentifier.from("starlight:msg");

    @Override
    public void enable() {
        getProxy().getServer().getChannelRegistrar().register(CHANNEL);
    }

    @APMRemoteEvent("starlight:chat-sync")
    public void onBroadcast(RemoteMessenger ctx, RemoteMessageEvent event) {
        var args = event.decode(String.class).split("::");
        var so = getProxyServer().getServer(event.sender());

        if (so.isPresent()) {
            if (!so.get().getPlayersConnected().isEmpty()) {
                return;
            }
        }

        broadcast(args[0], args[1], args[2]);
    }

    public void broadcast(String player, String server, String json) {
        var msg = GsonComponentSerializer.gson().deserialize(json);

        var targetName = ServerDisplayName.getDisplayName(server);

        var template = config().value("template").string().formatted(targetName, player);
        var line = QLib.textBuilder().buildComponent(template).append(msg);

        getProxy().getServer()
                .getAllPlayers()
                .stream()
                .filter((p) -> !Objects.equals(p.getUsername(), player))
                .filter((p) -> p.getCurrentServer()
                        .map((s) -> !Objects.equals(server, s.getServerInfo().getName()))
                        .orElse(false))
                .forEach((p) -> p.sendMessage(line));
    }


    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event) {
        if (!(CHANNEL.equals(event.getIdentifier()) || (CHANNEL_LEGACY.equals(event.getIdentifier())) || (CHANNEL_NEW.equals(event.getIdentifier())))) {
            return;
        }

        if (!(event.getSource() instanceof ServerConnection connection)) {
            return;
        }

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        var server = connection.getServer().getServerInfo().getName();
        var player = connection.getPlayer().getUsername();
        var message = new String(event.getData(), StandardCharsets.UTF_8);

        RemoteMessageService.instance().broadcast("starlight:chat-sync", String.join("::", player, server, message));
        broadcast(player, server, message);
    }
}
