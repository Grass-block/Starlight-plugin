package org.atcraftmc.starlight;

import org.atcraftmc.qlib.texts.placeholder.GlobalPlaceHolder;
import org.atcraftmc.qlib.texts.placeholder.GloballyPlaceHolder;

public interface PlaceHolders {
    static GloballyPlaceHolder chat() {
        GloballyPlaceHolder holder = new GloballyPlaceHolder();

        holder.register("&0", GlobalPlaceHolder.value("§0"), "black");
        holder.register("&1", GlobalPlaceHolder.value("§1"), "dark-blue", "dark_blue");
        holder.register("&2", GlobalPlaceHolder.value("§2"), "dark-green", "dark_green");
        holder.register("&3", GlobalPlaceHolder.value("§3"), "dark-aqua", "dark_aqua");
        holder.register("&4", GlobalPlaceHolder.value("§4"), "dark-red", "dark_red");
        holder.register("&5", GlobalPlaceHolder.value("§5"), "dark-purple", "dark_purple");
        holder.register("&6", GlobalPlaceHolder.value("§6"), "gold");
        holder.register("&7", GlobalPlaceHolder.value("§7"), "gray", "light-gray", "light_gray");
        holder.register("&8", GlobalPlaceHolder.value("§8"), "dark-gray", "dark_gray");
        holder.register("&9", GlobalPlaceHolder.value("§9"), "blue");
        holder.register("&a", GlobalPlaceHolder.value("§a"), "green", "light-green", "light_green");
        holder.register("&b", GlobalPlaceHolder.value("§b"), "aqua", "light-blue", "light-aqua", "light_aqua", "light_blue");
        holder.register("&c", GlobalPlaceHolder.value("§c"), "red", "light-red", "light_red");
        holder.register("&d", GlobalPlaceHolder.value("§d"), "purple", "light-purple", "light_purple");
        holder.register("&e", GlobalPlaceHolder.value("§e"), "yellow");
        holder.register("&f", GlobalPlaceHolder.value("§f"), "white");

        holder.register("&k", GlobalPlaceHolder.value("§k"), "magic", "obfuscate");
        holder.register("&l", GlobalPlaceHolder.value("§l"), "bold");
        holder.register("&m", GlobalPlaceHolder.value("§m"), "delete", "strikethrough");
        holder.register("&n", GlobalPlaceHolder.value("§n"), "underline");
        holder.register("&o", GlobalPlaceHolder.value("§o"), "italic");
        holder.register("&r", GlobalPlaceHolder.value("§r"), "reset", "unset");

        holder.register("return", GlobalPlaceHolder.value("\n"));

        return holder;
    }
}
