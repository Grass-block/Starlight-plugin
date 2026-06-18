package org.atcraftmc.starlight.util;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SingleNotificationContainer implements Listener {
    private final Map<UUID, Set<String>> uuids = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.uuids.remove(event.getPlayer().getUniqueId());
    }

    public void register(UUID uuid, String channel) {
        this.uuids.computeIfAbsent(uuid, k -> new HashSet<>()).add(channel);
    }

    public boolean contains(UUID uuid, String channel) {
        if (!this.uuids.containsKey(uuid)) {
            return false;
        }
        return this.uuids.get(uuid).contains(channel);
    }

    public boolean notify(Player player, String channel) {
        if (this.contains(player.getUniqueId(), channel)) {
            return false;
        }

        register(player.getUniqueId(), channel);

        return true;
    }
}
