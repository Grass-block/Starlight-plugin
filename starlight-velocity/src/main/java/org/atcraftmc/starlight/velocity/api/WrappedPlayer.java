package org.atcraftmc.starlight.velocity.api;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.util.GameProfile;
import me.gb2022.simpnet.util.BufferUtil;
import org.atcraftmc.starlight.APMChannels;
import org.atcraftmc.starlight.shared.RemoteMessageService;
import org.atcraftmc.starlight.velocity.StarlightVelocity;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface WrappedPlayer {
    void connect(RegisteredServer server);

    GameProfile getGameProfile();

    Optional<RegisteredServer> getConnectedServer();

    String getUsername();

    UUID getUniqueId();

    Player getHandle();

    final class LocalPlayer implements WrappedPlayer {
        private final Player player;

        public LocalPlayer(Player player) {
            this.player = player;
        }

        @Override
        public Optional<RegisteredServer> getConnectedServer() {
            return player.getCurrentServer().map(ServerConnection::getServer);
        }

        @Override
        public void connect(RegisteredServer server) {
            this.player.createConnectionRequest(server).connect();
        }

        @Override
        public String getUsername() {
            return player.getUsername();
        }

        @Override
        public UUID getUniqueId() {
            return player.getUniqueId();
        }

        @Override
        public GameProfile getGameProfile() {
            return player.getGameProfile();
        }

        @Override
        public Player getHandle() {
            return player;
        }
    }

    final class RemotePlayer implements WrappedPlayer {
        private final UUID uuid;
        private final String proxy;
        private final String name;
        private final GameProfile gameProfile;
        private String server;


        public RemotePlayer(UUID uuid, String proxy, String name, String server) {
            this.uuid = uuid;
            this.proxy = proxy;
            this.name = name;
            this.server = server;
            this.gameProfile = new GameProfile(uuid, name, new ArrayList<>());
        }

        @Override
        public void connect(RegisteredServer server) {
            RemoteMessageService.instance().message(this.proxy, APMChannels.PROXY_ACT_CONNECT, (b) -> {
                BufferUtil.writeString(b, this.getUniqueId().toString());
                BufferUtil.writeString(b, server.getServerInfo().getName());
            });
        }

        @Override
        public GameProfile getGameProfile() {
            return this.gameProfile;
        }

        @Override
        public Optional<RegisteredServer> getConnectedServer() {
            return StarlightVelocity.instance().getServer().getServer(this.server);
        }

        @Override
        public String getUsername() {
            return this.name;
        }

        public String getServer() {
            return this.server;
        }

        @Override
        public UUID getUniqueId() {
            return this.uuid;
        }

        public void setServer(String server) {
            this.server = server;
        }

        @Override
        public Player getHandle() {
            return null;
        }
    }
}
