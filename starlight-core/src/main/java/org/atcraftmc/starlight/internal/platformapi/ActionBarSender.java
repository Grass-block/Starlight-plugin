package org.atcraftmc.starlight.internal.platformapi;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.injector.packet.PacketRegistry;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import org.atcraftmc.qlib.audience.PointedAudience;
import org.atcraftmc.qlib.text.pipe.MessageHandler;
import org.atcraftmc.starlight.core.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface ActionBarSender {
    static ProtocolManager protocol() {
        return ProtocolLibrary.getProtocolManager();
    }

    static MessageHandler getImpl() {
        try {
            PacketType.Play.Server.class.getDeclaredField("SET_ACTION_BAR_TEXT");
            PacketRegistry.getPacketClassFromType(PacketType.Play.Server.SET_ACTION_BAR_TEXT);
            return new Modern();
        } catch (Exception e) {
            return new Legacy();
        }
    }

    void send(Player player, String message);

    // =========================================================
    // 1.8.x
    // =========================================================

    final class Legacy extends MessageHandler {

        @Override
        public void handle(PointedAudience audience, Component component) {
            var sender = audience.getPointer(CommandSender.class);

            if (!(sender instanceof Player player)) {
                this.parent.handle(audience, component);
                return;
            }

            try {
                var packet = protocol().createPacket(PacketType.Play.Server.CHAT);
                packet.getChatComponents().write(0, WrappedChatComponent.fromJson(ComponentSerializer.json(component)));
                packet.getBytes().write(0, (byte) 2);
                protocol().sendServerPacket(player, packet);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // =========================================================
    // 1.9+
    // =========================================================

    final class Modern extends MessageHandler {
        @Override
        public void handle(PointedAudience audience, Component component) {
            var sender = audience.getPointer(CommandSender.class);

            if (!(sender instanceof Player player)) {
                this.parent.handle(audience, component);
                return;
            }

            // 新版本优先使用独立 ActionBar Packet
            try {
                var packet = protocol().createPacket(PacketType.Play.Server.SET_ACTION_BAR_TEXT);
                packet.getChatComponents().write(0, WrappedChatComponent.fromJson(ComponentSerializer.json(component)));
                protocol().sendServerPacket(player, packet);

                return;
            } catch (Throwable ignored) {
            }

            // fallback: CHAT + GAME_INFO
            try {
                var packet = protocol().createPacket(PacketType.Play.Server.CHAT);
                packet.getChatComponents().write(0, WrappedChatComponent.fromJson(ComponentSerializer.json(component)));

                if (!packet.getChatTypes().getFields().isEmpty()) {
                    packet.getChatTypes().write(0, EnumWrappers.ChatType.GAME_INFO);
                }

                protocol().sendServerPacket(player, packet);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}