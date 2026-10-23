package org.atcgroup.starlight.bundle.display;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.method.MethodHandle;
import me.gb2022.commons.reflect.method.MethodHandleRO0;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.SharedObjects;
import org.atcraftmc.starlight.api.event.ForwardedChatEvent;
import org.atcraftmc.starlight.core.TextExaminer;
import org.atcraftmc.starlight.core.placeholder.PlaceHolderService;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.util.TemplateExpansion;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Date;

@AutoRegister(Registrations.SERVER_EVENT)
@ApplicationModule(id = "chat-format", version = "1.2.0", description = "Re-format your chat line.")
public final class ChatFormat extends BukkitAbstractModule {
    MethodHandleRO0<World, String> getDimensionId = MethodHandle.select((ctx) -> {
        ctx.attempt(() -> World.class.getMethod("getKey"), (w) -> {
            var origin = w.getKey().toString();
            return origin.replace(":", "-").replace("_", "-").replace(".", "-");
        });
        ctx.dummy((w) -> "minecraft-" + w.getName()
                .replace("world", "overworld")
                .replace("world-nether", "the-nether")
                .replace("world-the-end", "the-end")
                .replace("DIM0", "overworld")
                .replace("DIM1", "the-end")
                .replace("DIM-1", "the-nether"));
    });

    private String text(Player player, TemplateExpansion exp, String template) {
        template = exp.expand(template);
        template = PlaceHolderService.formatPlayer(player, template);
        return template;
    }

    @EventHandler
    public void onPlayerChat(final ForwardedChatEvent event) {
        var expanded = TemplateExpansion.build((b) -> {
            b.replacement("time", SharedObjects.TIME_FORMAT.format(new Date()));
            b.replacement("world", TextExaminer.examinableText("common:world:" + this.getDimensionId.invoke(event.getSender().getWorld())));
        });

        event.getRenderer().template(expanded.expand(config().value("template").string()));

        var t_prefix = config().value("prefix").string();
        if (!t_prefix.isEmpty()) {
            event.getRenderer().prefix(QLib.textBuilder().build(text(event.getSender(), expanded, t_prefix)));
        }

        var t_prefixNear = config().value("prefix-near").string();
        if (!t_prefixNear.isEmpty()) {
            event.getRenderer().prefixNearest(QLib.textBuilder().build(text(event.getSender(), expanded, t_prefixNear)));
        }

        var t_postfix = config().value("postfix").string();
        if (!t_postfix.isEmpty()) {
            event.getRenderer().postFix(QLib.textBuilder().build(text(event.getSender(), expanded, t_postfix)));
        }

        var t_postfixNear = config().value("postfix-near").string();
        if (!t_postfixNear.isEmpty()) {
            event.getRenderer().postFixNearest(QLib.textBuilder().build(text(event.getSender(), expanded, t_postfixNear)));
        }
    }
}

