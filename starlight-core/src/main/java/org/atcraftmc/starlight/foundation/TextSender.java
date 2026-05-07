package org.atcraftmc.starlight.foundation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.platform.PluginPlatform;
import org.atcraftmc.qlib.texts.ComponentBlock;
import org.atcraftmc.starlight.core.LocaleService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joml.Vector3i;

public interface TextSender {
    //message
    static void sendMessage(CommandSender sender, ComponentLike message) {
        QLib.audience(sender).sendMessage(message);
    }

    static void sendBlock(CommandSender sender, ComponentBlock block) {
        for (Component line : block) {
            sendMessage(sender, line);
        }
    }

    static void sendMessage(ComponentLike component) {
        CommandSender sender = Bukkit.getConsoleSender();
        sendMessage(sender, component);
    }

    static void sendBlock(ComponentBlock component) {
        sendBlock(Bukkit.getConsoleSender(), component);
    }


    //title
    static void sendTitle(Player viewer, ComponentLike title, ComponentLike subtitle, int in, int stay, int out) {
        BukkitAPI.TITLE.get().invoke(viewer, title, subtitle, new Vector3i(in, stay, out));
    }

    static void title(Player p, ComponentLike component, int in, int stay, int out) {
        sendTitle(p, component, Component.text(""), in, stay, out);
    }

    static void subtitle(Player p, ComponentLike component, int in, int stay, int out) {
        sendTitle(p, Component.text(""), component, in, stay, out);
    }

    //misc
    static void sendChatColor(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
