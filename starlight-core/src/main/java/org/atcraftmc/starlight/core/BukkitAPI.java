package org.atcraftmc.starlight.core;

import me.gb2022.commons.reflect.method.MethodHandleO1;
import me.gb2022.commons.reflect.method.MethodHandleO3;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.atcraftmc.qlib.platform.api.APIComponent;
import org.atcraftmc.qlib.platform.api.APIManager;
import org.bukkit.entity.Player;
import org.joml.Vector3i;

import java.time.Duration;

@SuppressWarnings("Convert2MethodRef")
public interface BukkitAPI {
    APIManager API_MANAGER = new APIManager();

    APIComponent<MethodHandleO3<Player, ComponentLike, ComponentLike, Vector3i>> TITLE = API_MANAGER.registerAPI("qlib:player:title");
    APIComponent<MethodHandleO1<Player, ComponentLike>> ACTIONBAR_TITLE = API_MANAGER.registerAPI("qlib:player:actiobar-title");

    static void init() {
        TITLE.addLast("qlib:paper", () -> Player.class.getMethod("showTitle", Title.class), (p, t, s, v) -> {
            var in = Duration.ofMillis(v.x() * 50L);
            var stay = Duration.ofMillis(v.y() * 50L);
            var out = Duration.ofMillis(v.z() * 50L);
            var time = Title.Times.times(in, stay, out);

            p.showTitle(Title.title(t.asComponent(), s.asComponent(), time));
        });
        TITLE.addLast("qlib:bukkit", () -> true, (p, t, s, v) -> {
            var title = ComponentSerializer.legacy(t);
            var subtitle = ComponentSerializer.legacy(s);

            p.sendTitle(title, subtitle, v.x(), v.y(), v.z());
        });

        ACTIONBAR_TITLE.addLast("qlib:paper", () -> Player.class.getMethod("sendActionBar", Component.class), (p, c) -> p.sendActionBar(c));
        ACTIONBAR_TITLE.addLast("qlib:spigot", () -> Player.class.getMethod("sendActionBar", BaseComponent[].class), (p, c) -> {
            var bc = ComponentSerializer.bungee(c);
            p.sendActionBar(bc);
        });
        ACTIONBAR_TITLE.addLast("qlib:spigot-indirect", () -> Player.class.getMethod("sendActionBar", BaseComponent[].class), (p, c) -> {
            var bc = ComponentSerializer.bungee(c);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, bc);
        });
        ACTIONBAR_TITLE.addLast("qlib:no-op", () -> true, (p, c) -> {});
    }
}
