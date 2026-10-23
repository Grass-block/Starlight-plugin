package org.atcraftmc.starlight.api.event;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.atcraftmc.starlight.internal.chat.LazyChatRenderer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.util.function.Function;

@BukkitEvent
public final class ForwardedChatEvent extends CustomEvent implements Cancellable {
    private final LazyChatRenderer renderer;
    private final Player sender;
    private Component message;
    private String rawChatMessage;
    private Function<String, ComponentLike> builder = null;
    private boolean rebuildNeeded = false;
    private boolean cancelled = false;

    public ForwardedChatEvent(Player sender, LazyChatRenderer renderer, Component message) {
        this.sender = sender;
        this.renderer = renderer;
        this.message = message;
        this.rawChatMessage = LegacyComponentSerializer.legacySection().serialize(message);
    }

    public ForwardedChatEvent(Player sender, LazyChatRenderer renderer, String message) {
        this.sender = sender;
        this.renderer = renderer;
        this.message = Component.text(message);
        this.rawChatMessage = message;
    }

    public Component getMessage() {
        return this.message;
    }

    public void setMessage(Component message) {
        this.message = message;
        this.rawChatMessage = LegacyComponentSerializer.legacySection().serialize(message);
    }

    public String getRawChatMessage() {
        return rawChatMessage;
    }

    public void setRawChatMessage(String rawChatMessage) {
        this.rawChatMessage = rawChatMessage;
        this.rebuildNeeded = true;
    }

    public void setBuilder(Function<String, ComponentLike> builder) {
        this.builder = builder;
        this.rebuildNeeded = true;
    }

    public static HandlerList getHandlerList() {
        return getHandlerList(ForwardedChatEvent.class);
    }

    public Player getSender() {
        return sender;
    }

    public LazyChatRenderer getRenderer() {
        return renderer;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public ComponentLike getOutcomeMessage() {
        if (!this.rebuildNeeded) {
            return this.message;
        }

        if (this.builder == null) {
            return Component.text(this.rawChatMessage);
        }

        return this.builder.apply(this.rawChatMessage);
    }
}
