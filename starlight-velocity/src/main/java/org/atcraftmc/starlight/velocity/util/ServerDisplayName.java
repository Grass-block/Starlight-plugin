package org.atcraftmc.starlight.velocity.util;

import net.kyori.adventure.text.Component;
import org.atcraftmc.starlight.shared.Configurations;

import java.util.HashMap;
import java.util.Map;

public interface ServerDisplayName {
    Map<String, String> SERVER_NAMES = new HashMap<>();

    static void init() {
        var cfg = Configurations.standalone("server-names");

        for (var id : cfg.getKeys(false)) {
            SERVER_NAMES.put(id, cfg.getString(id));
        }
    }

    static String getDisplayName(String serverName) {
        if (!SERVER_NAMES.containsKey(serverName)) {
            return serverName;
        }

        return SERVER_NAMES.get(serverName);
    }

    static Component getDisplayName_C(String serverName) {
        return QLib.textBuilder().buildComponent(getDisplayName(serverName));
    }
}
