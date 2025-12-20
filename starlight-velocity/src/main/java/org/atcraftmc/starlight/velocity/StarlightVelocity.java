package org.atcraftmc.starlight.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import me.gb2022.apm.remote.RemoteMessenger;
import me.gb2022.commons.container.ObjectContainer;
import net.kyori.adventure.text.ComponentLike;
import org.apache.logging.log4j.LogManager;
import org.atcraftmc.qlib.PluginConcept;
import org.atcraftmc.qlib.PluginPlatform;
import org.atcraftmc.qlib.config.ConfigContainer;
import org.atcraftmc.qlib.config.StandaloneConfiguration;
import org.atcraftmc.qlib.language.LanguageContainer;
import org.atcraftmc.qlib.language.StandaloneLanguagePack;
import org.atcraftmc.qlib.texts.placeholder.PlaceHolder;
import org.atcraftmc.starlight.shared.SLPluginEnvironment;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

@Plugin(id = "starlight-velocity")
public final class StarlightVelocity implements PluginConcept {
    public static final ObjectContainer<StarlightVelocity> INSTANCE = new ObjectContainer<>();

    private final ModuleManager moduleManager = new ModuleManager(this);
    private final Config config0 = new Config(this);
    private final LanguageContainer lang = new LanguageContainer();

    private final Logger logger;
    private final ProxyServer server;

    @Inject
    public StarlightVelocity(ProxyServer server, Logger logger) {
        PluginPlatform.setPlatform(new StarlightVelocityPlatform());

        this.server = server;
        this.logger = logger;

        INSTANCE.set(this);
    }

    public static LanguageContainer lang() {
        return INSTANCE.get().lang;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        SLPluginEnvironment.setPluginId("starlight-velocity");
        SLPluginEnvironment.setCoreRef(this);
        SLPluginEnvironment.setDataFolder(folder());

        var config = new StandaloneConfiguration(this);
        config.load();

        this.config0.load();

        PlaceHolder.init();

        var locales = new String[]{"zh_cn"};

        for (var locale : locales) {
            var pack = new StandaloneLanguagePack(locale, this);
            pack.load();

            this.lang.inject(pack);
        }

        ConfigContainer.getInstance().inject(config);

        this.regManager.deferredInit();
        this.messenger.init();
        this.moduleManager.enable();
        Runtime.getRuntime().addShutdownHook(new Thread(this::onServerStop));
    }

    private void onServerStop() {
        this.messenger.stop();
        this.moduleManager.disable();
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    public ProxyModuleRegManager getRegManager() {
        return regManager;
    }

    public VelocityCommandManager getCommandManager() {
        return commandManager;
    }

    public RemoteMessenger getMessenger() {
        return messenger.getConnector();
    }

    public Path getDataDirectory() {
        return Path.of(folder());
    }

    public Config getConfig() {
        return config0;
    }

    @Override
    public String id() {
        return "quark-velocity";
    }

    @Override
    public org.apache.logging.log4j.Logger logger() {
        return LogManager.getLogger("quark-velocity");
    }

    @Override
    public String folder() {
        return System.getProperty("user.dir") + "/plugins/starlight-velocity";
    }

    @Override
    public String configId() {
        return "--global";
    }


    private final class StarlightVelocityPlatform implements PluginPlatform {

        @Override
        public void sendMessage(Object o, ComponentLike componentLike) {
            ((Player) o).sendMessage(componentLike);
        }

        @Override
        public Locale locale(Object o) {
            return Optional.ofNullable(((Player) o).getPlayerSettings().getLocale()).orElse(Locale.getDefault());
        }

        @Override
        public String globalFormatMessage(String s) {
            return PlaceHolder.format(PlaceHolder.format(s));
        }

        @Override
        public void broadcastLine(Function<Locale, ComponentLike> function, boolean b, boolean b1) {
            for (var player : getServer().getAllPlayers()) {
                sendMessage(player, function.apply(locale(player)));
            }
        }

        @Override
        public PluginConcept defaultPlugin() {
            return StarlightVelocity.this;
        }

        @Override
        public String pluginsFolder() {
            return System.getProperty("user.dir") + "/plugins";
        }
    }
}
