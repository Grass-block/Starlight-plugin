package org.atcraftmc.starlight.internal.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.atcraftmc.qlib.bukkit.QLib;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class LazyChatRenderer implements PlayerChatRenderer {
    private final List<ChatContentProvider> prefixes = new ArrayList<>();
    private final List<ChatContentProvider> postFixes = new ArrayList<>();
    private final List<ChatContentTransformer> messageTransformers = new ArrayList<>();
    private final PlayerChatRenderer wrapped;
    private boolean modified = false;
    private boolean needRender = false;
    private String template;

    public LazyChatRenderer(PlayerChatRenderer wrapped, String template) {
        this.template = template;
        this.wrapped = wrapped;
    }

    public LazyChatRenderer(PlayerChatRenderer wrapped) {
        this(wrapped, "<{0}> {1}");
    }

    public LazyChatRenderer prefix(ComponentLike comp) {
        return prefix(ChatContentProvider.fixed(comp));
    }

    public LazyChatRenderer prefix(ChatContentProvider provider) {
        this.prefixes.add(0, provider);
        this.modified = true;
        return this;
    }

    public LazyChatRenderer prefixNearest(ChatContentProvider provider) {
        this.prefixes.add(provider);
        this.modified = true;
        return this;
    }

    public LazyChatRenderer prefixNearest(ComponentLike comp) {
        return prefixNearest(ChatContentProvider.fixed(comp));
    }

    public LazyChatRenderer postFix(ChatContentProvider transformer) {
        this.postFixes.add(transformer);
        this.modified = true;
        return this;
    }

    public LazyChatRenderer postFix(ComponentLike comp) {
        return postFix(ChatContentProvider.fixed(comp));
    }

    public LazyChatRenderer postFixNearest(ChatContentProvider transformer) {
        this.postFixes.add(0, transformer);
        this.modified = true;
        return this;
    }

    public LazyChatRenderer postFixNearest(ComponentLike comp) {
        return postFixNearest(ChatContentProvider.fixed(comp));
    }

    public LazyChatRenderer transformer(ChatContentTransformer transformer) {
        this.messageTransformers.add(transformer);
        this.needRender = true;
        this.modified = true;
        return this;
    }

    public LazyChatRenderer template(String template) {
        this.template = template;
        this.needRender = true;
        this.modified = true;
        return this;
    }

    @Override
    public Component render(Player source, Component sourceName, Component message, Audience viewer) {
        if (this.wrapped != null && !this.modified) {
            return this.wrapped.render(source, sourceName, message, viewer);
        }

        var view = ((CommandSender) viewer);
        var builder = Component.text();

        for (var c : this.prefixes) {
            builder.append(c.apply(view));
        }

        if (this.wrapped == null || this.needRender) {
            var msg = ((ComponentLike) message);

            for (var t : this.messageTransformers) {
                msg = t.apply(view, msg);
            }

            builder.append(QLib.textBuilder().buildComponent(this.template, sourceName, msg.asComponent()));
        } else {
            builder.append(this.wrapped.render(source, sourceName, message, viewer));
        }

        for (var c : this.postFixes) {
            builder.append(c.apply(view));
        }

        return builder.build();
    }
}
