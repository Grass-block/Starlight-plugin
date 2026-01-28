package org.atcraftmc.starlight.util;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.function.Function;

public final class PlayerMap<V> extends HashMap<String, V> {
    public void put(Player player, V value) {
        put(player.getUniqueId().toString(), value);
    }

    public V get(Player player) {
        return get(player.getUniqueId().toString());
    }

    public boolean contains(Player player) {
        return this.containsKey(player.getUniqueId().toString());
    }

    public V remove(Player player) {
        return remove(player.getUniqueId().toString());
    }

    public V computeIfAbsent(Player player, Function<Player, ? extends V> gf) {
        return computeIfAbsent(player.getUniqueId().toString(), (k) -> gf.apply(player));
    }
}
