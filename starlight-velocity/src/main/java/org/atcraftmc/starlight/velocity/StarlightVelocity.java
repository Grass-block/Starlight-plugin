package org.atcraftmc.starlight.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.gb2022.commons.container.ObjectContainer;
import me.gb2022.gluon.ModularApplicationContext;
import net.kyori.adventure.text.ComponentLike;
import org.atcraftmc.qlib.config.ConfigContainer;
import org.atcraftmc.qlib.language.LanguageContainer;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.qlib.platform.ForwardingPluginPlatform;
import org.atcraftmc.qlib.platform.PluginPlatform;
import org.atcraftmc.qlib.velocity.VelocityPlatform;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.environment.PathManager;
import org.atcraftmc.starlight.framework.PluginApplication;
import org.atcraftmc.starlight.framework.PluginServiceManager;
import org.atcraftmc.starlight.util.ProductMetadata;
import org.atcraftmc.starlight.util.SLLogProvider;
import org.atcraftmc.starlight.util.dependency.LibraryManager;
import org.atcraftmc.starlight.velocity.core.VelocityPlaceHolderService;
import org.atcraftmc.starlight.velocity.framework.VelocityModuleManager;
import org.slf4j.Logger;

import java.io.File;
import java.util.Locale;
import java.util.Optional;

@Plugin(id = "starlight-velocity", version = "26.1.0", authors = "GrassBlock2022")
public final class StarlightVelocity implements PluginApplication {
    public static final org.apache.logging.log4j.Logger LOGGER = SLPluginEnvironment.createLogger("Starlight-Velocity");
    public static final ObjectContainer<StarlightVelocity> INSTANCE = new ObjectContainer<>();

    private final ModularApplicationContext context = createContext(this);
    private final ProductMetadata metadata = ProductMetadata.createFromResource(this);
    private final LanguageContainer language = new LanguageContainer(this, "starlight-velocity");
    private final ConfigContainer config = ConfigContainer.getInstance();
    private final Logger logger;
    private final ProxyServer server;

    @Inject
    public StarlightVelocity(ProxyServer server, Logger logger) {
        VelocityPlatform.init(server);

        PluginPlatform.global().addLast("starlight-velocity", new StarlightVelocityPlatform());

        this.server = server;
        this.logger = logger;

        INSTANCE.set(this);
    }

    private static ModularApplicationContext createContext(PluginApplication application) {
        var b = PluginApplication.createContext(application);
        b.moduleManager(VelocityModuleManager::new);
        b.serviceManager(PluginServiceManager::new);
        b.logProvider((c)->new SLLogProvider());
        b.applicationName("Starlight");

        return b.build();
    }

    public static LanguageContainer lang() {
        return INSTANCE.get().language();
    }

    public static StarlightVelocity instance() {
        return StarlightVelocity.INSTANCE.get();
    }

    @Override
    public ClassLoader classLoader() {
        return getClass().getClassLoader();
    }

    @Override
    public String name() {
        return "starlight-velocity";
    }

    @Override
    public ProductMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public LibraryManager getLibraryManager() {
        return null;
    }

    @Override
    public LanguageContainer language() {
        return this.language;
    }

    @Override
    public ConfigContainer config() {
        return this.config;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        SLPluginEnvironment.init(this.context, this, "starlight-velocity", new PathManager("starlight-velocity"));
        this.context.registerPackage(this, SLVInternalPackage.class);
        Runtime.getRuntime().addShutdownHook(new Thread(this::onServerStop));
    }

    private void onServerStop() {

    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    @Override
    public String id() {
        return "starlight-velocity";
    }

    @Override
    public org.apache.logging.log4j.Logger logger() {
        return LOGGER;
    }

    @Override
    public String folder() {
        return System.getProperty("user.dir") + "/plugins/starlight-velocity";
    }

    @Override
    public String configId() {
        return "--global";
    }


    private static final class StarlightVelocityPlatform extends ForwardingPluginPlatform {

        @Override
        public void sendMessage(Object o, ComponentLike componentLike) {
            ((Player) o).sendMessage(componentLike);
        }

        @Override
        public MinecraftLocale locale(Object o) {
            return MinecraftLocale.locale(Optional.ofNullable(((Player) o).getPlayerSettings().getLocale()).orElse(Locale.getDefault()));
        }

        @Override
        public String globalFormatMessage(String s) {
            return VelocityPlaceHolderService.format(VelocityPlaceHolderService.format(s));
        }

        @Override
        public String pluginsFolder() {
            return System.getProperty("user.dir") + "/plugins";
        }
    }
}
