package org.atcraftmc.starlight.internal.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public
interface PlayerChatRenderer {
    Component render(Player source, Component sourceName, Component message, Audience viewer);

    @FunctionalInterface
    interface ChatContentProvider extends Function<CommandSender, ComponentLike> {
        static ChatContentProvider fixed(ComponentLike component) {
            return (p) -> component;
        }
    }

    @FunctionalInterface
    interface ChatContentTransformer extends BiFunction<CommandSender, ComponentLike, ComponentLike> {
        static ChatContentTransformer fixed(Function<ComponentLike, ComponentLike> transformer) {
            return (p, c) -> transformer.apply(c);
        }
    }
}
