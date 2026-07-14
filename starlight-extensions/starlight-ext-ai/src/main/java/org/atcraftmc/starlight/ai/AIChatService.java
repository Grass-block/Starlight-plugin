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
import java.util.Objects;

@ApplicationService(id = "ai-chat", impl = AIChatService.Impl.class)
public interface AIChatService extends BukkitService {
    Logger LOGGER = SLPluginEnvironment.createLogger("AI-Chat");
    @ServiceInject
    ServiceHolder<AIChatService> INSTANCE = new ServiceHolder<>();

    static AIChatService instance() {
        return INSTANCE.get();
    }

    AIChatRequestHandler handler(String name);

    AIChatRequestHandler defaultHandler();

    final class Impl implements AIChatService {
        private final Map<String, AIChatRequestHandler> handlers = new HashMap<>();
        private AIChatRequestHandler defaultHandler;

        @Override
        public AIChatRequestHandler handler(String name) {
            return this.handlers.get(name);
        }

        @Override
        public AIChatRequestHandler defaultHandler() {
            return this.defaultHandler;
        }

        @Override
        public void enable() {
            AIChatRequestHandler.createDefaults();

            var dom = Configurations.standalone("ai-models");
            var defaultId = dom.getString("default");
            var services = dom.getConfigurationSection("services");

            if (services == null) {
                LOGGER.warn("No services has created, please check your configuration.");
                return;
            }

            for (var id : services.getKeys(false)) {
                this.handlers.put(id, AIChatRequestHandler.create(Objects.requireNonNull(services.getConfigurationSection(id))));
            }

            if (this.handlers.isEmpty()) {
                LOGGER.warn("No services has created, please check your configuration.");
            }

            if (!this.handlers.containsKey(defaultId)) {
                var fallbackId = this.handlers.keySet().iterator().next();
                LOGGER.warn("No default exist, falling back to service {}", fallbackId);
                this.defaultHandler = this.handlers.get(fallbackId);
            } else {
                this.defaultHandler = this.handlers.get(defaultId);
            }
        }
    }
}
