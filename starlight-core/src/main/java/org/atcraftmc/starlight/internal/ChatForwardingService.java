package org.atcraftmc.starlight.internal;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.api.event.ChatForwardingEvent;
import org.atcraftmc.starlight.foundation.ComponentSerializer;
import org.atcraftmc.starlight.foundation.TextSender;
import org.atcraftmc.starlight.foundation.platform.BukkitUtil;
import org.atcraftmc.starlight.framework.BukkitService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@ApplicationService(id = "chat-forwarding")
public interface ChatForwardingService extends BukkitService {
    Listener EVENT_HANDLER = createImplementation();

    @ServiceInject
    static void start() {
        BukkitUtil.registerEventListener(EVENT_HANDLER);
    }

    @ServiceInject
    static void stop() {
        BukkitUtil.unregisterEventListener(EVENT_HANDLER);
    }

    static Listener createImplementation() {
        try {
            Class.forName("io.papermc.paper.event.player.AsyncChatEvent");
            return new PaperEventHandler();
        } catch (Throwable ignored) {
            return new BukkitEventHandler();
        }
    }

    class PaperEventHandler implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onChatting(AsyncChatEvent event) {
            if (event.isCancelled()) {
                return;
            }

            event.setCancelled(true);

            for (var player : Bukkit.getOnlinePlayers()) {
                var msg = event.renderer().render(event.getPlayer(), event.getPlayer().displayName(), event.message(), player);

                var e = new ChatForwardingEvent(player, event.getPlayer(), msg);
                BukkitUtil.callEvent(e);

                if (e.isCancelled()) {
                    continue;
                }

                QLib.audience(player).sendMessage(msg);
            }

            var e = new ChatForwardingEvent(Bukkit.getConsoleSender(), event.getPlayer(), event.message());
            BukkitUtil.callEvent(e);

            if (e.isCancelled()) {
                return;
            }

            System.out.printf("<%s> %s%n", event.getPlayer().getName(), ComponentSerializer.plain(e.getExamined()));
        }
    }

    class BukkitEventHandler implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onChatting(AsyncPlayerChatEvent event) {
            if (event.isCancelled()) {
                return;
            }

            var component = QLib.textBuilder().buildComponent(event.getFormat().formatted(event.getPlayer(), event.getMessage()));

            event.setCancelled(true);
            for (var player : Bukkit.getOnlinePlayers()) {
                var e = new ChatForwardingEvent(player, event.getPlayer(), component);
                BukkitUtil.callEvent(e);

                if (e.isCancelled()) {
                    continue;
                }

                QLib.audience(player).sendMessage(component);
            }

            var e = new ChatForwardingEvent(Bukkit.getConsoleSender(), event.getPlayer(), component);
            BukkitUtil.callEvent(e);

            if (e.isCancelled()) {
                return;
            }

            System.out.printf("<%s> %s%n", event.getPlayer().getName(), ComponentSerializer.plain(e.getExamined()));
        }
    }
}
