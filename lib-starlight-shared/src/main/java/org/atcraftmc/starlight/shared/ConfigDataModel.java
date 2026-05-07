package org.atcraftmc.starlight.shared;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public interface ConfigDataModel {
    Pattern MOTD_REPL_PATTERN = Pattern.compile("\\{[a-z]+}");

    static String motd(ConfigurationSection setting) {
        var root = setting.getConfigurationSection("motd");

        if (root == null) {
            throw new RuntimeException("invalid config!");
        }

        var template = root.getString("motd-title") + "\n{#reset}" + root.getString("motd-subtitle");
        var matcher = MOTD_REPL_PATTERN.matcher(template);

        while (matcher.find()) {
            var raw = matcher.group();

            var key = raw.replace("{", "").replace("}", "");

            if (key.startsWith("$")) {
                continue;
            }

            String content;

            if (!root.contains(key)) {
                content = key;
            } else if (root.isString(key)) {
                content = root.getString(key);
            } else {
                List<String> list = root.getStringList(key);
                content = list.get(new Random().nextInt(list.size()));
            }

            if (content == null) {
                content = key;
            }

            template = template.replace(raw, content);
        }

        return template;
    }
}
