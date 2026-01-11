package org.atcraftmc.starlight.velocity.util;

import com.velocitypowered.api.proxy.Player;
import org.atcraftmc.starlight.velocity.StarlightVelocity;

public interface VelocityUtil {
    static void registerListener(Object listener) {
        var plugin = StarlightVelocity.instance();
        plugin.getServer().getEventManager().register(plugin, listener);
    }

    static void unregisterListener(Object listener) {
        var plugin = StarlightVelocity.instance();
        plugin.getServer().getEventManager().unregisterListener(plugin, listener);
    }

    static void clearListeners() {
        var plugin = StarlightVelocity.instance();
        plugin.getServer().getEventManager().unregisterListeners(plugin);
    }


    static boolean isSameServer(Player p1, Player p2) {
        var c1 = p1.getCurrentServer();
        var c2 = p2.getCurrentServer();

        if (c1.isEmpty() && c2.isEmpty()) {
            return true;
        }

        if (c1.isEmpty()) {
            return false;
        }

        if (c2.isEmpty()) {
            return false;
        }

        return c1.get().getServerInfo().equals(c2.get().getServerInfo());
    }
}
