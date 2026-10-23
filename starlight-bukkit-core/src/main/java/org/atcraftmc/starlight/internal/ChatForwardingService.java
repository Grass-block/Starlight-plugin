package org.atcraftmc.starlight.internal;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.gb2022.commons.reflect.method.MethodHandle;
import me.gb2022.commons.reflect.method.MethodHandleRO0;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceInject;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.api.event.ChatForwardingEvent;
import org.atcraftmc.starlight.api.event.ForwardedChatEvent;
import org.atcraftmc.starlight.core.ComponentSerializer;
import org.atcraftmc.starlight.core.TextExaminer;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.atcraftmc.starlight.framework.BukkitService;
import org.atcraftmc.starlight.internal.chat.LazyChatRenderer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

@ApplicationService(id = "chat-forwarding")
public interface ChatForwardingService extends BukkitService {
    @SuppressWarnings("Convert2MethodRef")
    MethodHandleRO0<Player, Component> GET_DISPLAY_NAME = MethodHandle.select((c) -> {
        c.attempt(() -> Player.class.getMethod("displayName"), (p) -> p.displayName());
        c.dummy((p) -> Component.text(p.getDisplayName()));
    });

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

    static void forwardCustom(ForwardedChatEvent event) {
        for (var player : Bukkit.getOnlinePlayers()) {
            var rendered = event.getRenderer().render(event.getSender(), GET_DISPLAY_NAME.invoke(event.getSender()), event.getMessage(), QLib.audience(player));
            var forwardEvent = new ChatForwardingEvent(player, event.getSender(), rendered);

            try {
                BukkitUtil.callEvent(forwardEvent).get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }

            if (forwardEvent.isCancelled()) {
                continue;
            }

            QLib.audience(player).sendMessage(forwardEvent.getExamined());
        }

        var e2 = new ChatForwardingEvent(Bukkit.getConsoleSender(), event.getSender(), event.getMessage());
        BukkitUtil.callEvent(e2);

        if (e2.isCancelled()) {
            return;
        }

        System.out.printf("<%s> %s%n", event.getSender().getName(), ComponentSerializer.plain(e2.getExamined()));
    }

    class PaperEventHandler implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        public void onChatting(AsyncChatEvent event) {
            if (event.isCancelled()) {
                return;
            }

            var previousRenderer = event.renderer();
            //noinspection OverrideOnly
            var renderer = new LazyChatRenderer(previousRenderer::render);
            var e = new ForwardedChatEvent(event.getPlayer(), renderer, event.message());

            try {
                BukkitUtil.callEvent(e).get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new RuntimeException(ex);
            }

            if (false) {//enforce platform forwarding
                forwardCustom(e);
                return;
            }

            event.setCancelled(e.isCancelled());
            event.message(e.getOutcomeMessage().asComponent());
            event.renderer(new ChatRenderer() {
                @Override
                public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
                    if (viewer instanceof ConsoleCommandSender) {
                        return previousRenderer.render(source, sourceDisplayName, message, viewer);
                    }

                    var rendered = renderer.render(source, sourceDisplayName, message, viewer);
                    var forwardEvent = new ChatForwardingEvent(source, (CommandSender) viewer, rendered);
                    var aud = QLib.audience(((CommandSender) viewer));

                    try {
                        BukkitUtil.callEvent(forwardEvent).get();
                    } catch (InterruptedException | ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }

                    return TextExaminer.examine(forwardEvent.getExamined(), aud.pointed(), aud.pointed().locale());
                }
            });
        }
    }

    class BukkitEventHandler implements Listener {
        public static final Pattern PATTERN = Pattern.compile("%(?:\\d+\\$)?[-#+ 0,(<]*\\d*(?:\\.\\d+)?(?:[tT])?[a-zA-Z%]");

        static String toMessageFormat(String template) {
            var matcher = PATTERN.matcher(template);
            var result = new StringBuilder();
            var position = 0;
            var index = 0;

            while (matcher.find()) {
                appendLiteral(result, template.substring(position, matcher.start()));

                switch (matcher.group()) {
                    case "%%" -> appendLiteral(result, "%");
                    case "%n" -> appendLiteral(result, System.lineSeparator());
                    default -> result.append('{').append(index++).append('}');
                }

                position = matcher.end();
            }

            appendLiteral(result, template.substring(position));
            return result.toString();
        }

        private static void appendLiteral(StringBuilder result, String text) {
            // Escape literal apostrophes and braces for MessageFormat.
            for (var i = 0; i < text.length(); i++) {
                switch (text.charAt(i)) {
                    case '\'' -> result.append("''");
                    case '{', '}' -> {
                        // Quote consecutive braces together.
                        result.append('\'');
                        do {
                            result.append(text.charAt(i++));
                        } while (i < text.length()
                                && (text.charAt(i) == '{' || text.charAt(i) == '}'));
                        result.append('\'');
                        i--;
                    }
                    default -> result.append(text.charAt(i));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onChatting(AsyncPlayerChatEvent event) {
            if (event.isCancelled()) {
                return;
            }

            var renderer = new LazyChatRenderer(null, toMessageFormat(event.getFormat()));
            var e = new ForwardedChatEvent(event.getPlayer(), renderer, event.getMessage());

            BukkitUtil.callEvent(e);

            forwardCustom(e);
        }
    }
}
