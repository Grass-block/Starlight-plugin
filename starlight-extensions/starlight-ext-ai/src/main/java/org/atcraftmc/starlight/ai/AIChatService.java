package org.atcraftmc.starlight.ai;

import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.ai.request.AIChatRequestHandler;
import org.atcraftmc.starlight.config.Configurations;
import org.atcraftmc.starlight.framework.BukkitService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApplicationService(id = "ai-chat")
public class AIChatService implements BukkitService {
    public static final Logger LOGGER = SLPluginEnvironment.createLogger("AI-Chat");

    @ServiceInject
    public static final ServiceHolder<AIChatService> INSTANCE = new ServiceHolder<>();
    private final Map<String, AIChatRequestHandler> handlers = new HashMap<>();

    static AIChatService instance() {
        return INSTANCE.get();
    }

    public AIChatRequestHandler handler(String name) {
        return this.handlers.get(name);
    }

    @Override
    public void enable() {
        AIChatRequestHandler.createDefaults();

        var list = Configurations.groupedYML("ai-model", Set.of("example-astrobot.yml", "example-openai.yml"));

        for (var c : list.entrySet()) {
            var id = c.getKey();
            if (id.startsWith("example")) {
                return;
            }
            var config = c.getValue();

            this.handlers.put(id, AIChatRequestHandler.create(config));
        }

        if (this.handlers.isEmpty()) {
            LOGGER.warn("No services has created, please check your configuration.");
        }
    }
}
