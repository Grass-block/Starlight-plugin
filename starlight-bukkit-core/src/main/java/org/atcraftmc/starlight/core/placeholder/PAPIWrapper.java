package org.atcraftmc.starlight.core.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.bukkit.entity.Player;

public interface PAPIWrapper {
    Logger LOGGER = SLPluginEnvironment.createLogger("PAPI-TextService");

    static PAPIWrapper getInstance() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            LOGGER.info("using PlaceholderAPI text formatter");
            return new Impl();
        } catch (Exception e) {
            LOGGER.info("using no-PlaceholderAPI text formatter.");
            return new FallbackImpl();
        }
    }

    String handle(String input);

    String handlerPlayer(Player player, String input);

    final class Impl implements PAPIWrapper {
        @Override
        public String handle(String input) {
            try {
                return PlaceholderAPI.setPlaceholders(null, input);
            } catch (Exception ignored) {
                return input;
            }
        }

        @Override
        public String handlerPlayer(Player player, String input) {
            try {
                return PlaceholderAPI.setPlaceholders(player, input);
            } catch (Exception ignored) {
                return input;
            }
        }
    }

    final class FallbackImpl implements PAPIWrapper {
        @Override
        public String handle(String input) {
            return input;
        }

        @Override
        public String handlerPlayer(Player player, String input) {
            return input;
        }
    }
}
