package org.atcraftmc.starlight.ai.request;

import org.atcraftmc.starlight.ai.chat.ChatRequest;
import org.atcraftmc.starlight.ai.chat.ChatResponse;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public interface AIChatRequestHandler {
    Map<String, Factory> FACTORIES = new HashMap<>();

    static void register(String name, Factory factory) {
        FACTORIES.put(name, factory);
    }

    static AIChatRequestHandler create(ConfigurationSection section) {
        if (!section.contains("service")) {
            throw new IllegalArgumentException("Configuration section must contain 'service' key");
        }

        var factory = FACTORIES.get(Objects.requireNonNull(section.getString("service")));

        if (factory == null) {
            throw new IllegalArgumentException("Unknown service " + Objects.requireNonNull(section.getString("service")));
        }

        return factory.apply(section);
    }

    static void createDefaults() {
        register("openai", section -> {
            var url = Objects.requireNonNull(section.getString("base-url"));
            var key = Objects.requireNonNull(section.getString("api-key"));
            var model = Objects.requireNonNull(section.getString("model"));
            var maxTokens = section.getInt("max-tokens", 256);
            var temperature = section.getDouble("temperature",0.7);

            return new OpenAICompatibleHandler(url, key, model, maxTokens, temperature);
        });
        register("astrbot", section -> {
            var url = Objects.requireNonNull(section.getString("base-url"));
            var key = Objects.requireNonNull(section.getString("api-key"));

            return new AstroBotHandler(url, key);
        });
    }

    ChatResponse chat(ChatRequest request);

    interface Factory extends Function<ConfigurationSection, AIChatRequestHandler> {

    }
}
