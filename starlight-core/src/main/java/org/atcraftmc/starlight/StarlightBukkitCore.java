package org.atcraftmc.starlight;

import me.gb2022.gluon.ModularApplicationContext;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.QLibContext;
import org.atcraftmc.qlib.bukkit.BukkitPlatform;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.bukkit.QLibBukkitContext;
import org.atcraftmc.qlib.command.CommandManager;
import org.atcraftmc.qlib.config.ConfigContainer;
import org.atcraftmc.qlib.language.LanguageAccess;
import org.atcraftmc.qlib.language.LanguageContainer;
import org.atcraftmc.qlib.platform.PluginPlatform;
import org.atcraftmc.qlib.text.pipe.AudienceHandler;
import org.atcraftmc.qlib.text.pipe.LocaleHandler;
import org.atcraftmc.starlight.core.BukkitAPI;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.TextExaminer;
import org.atcraftmc.starlight.core.command.StarlightCommandManager;
import org.atcraftmc.starlight.core.placeholder.PlaceHolderService;
import org.atcraftmc.starlight.framework.BukkitServiceManager;
import org.atcraftmc.starlight.framework.PluginApplication;
import org.atcraftmc.starlight.framework.module.BukkitModuleManager;
import org.atcraftmc.starlight.util.SLLogProvider;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class StarlightBukkitCore implements PluginApplication {
    public static final Logger LOGGER = SLPluginEnvironment.createLogger("Core");
    public static StarlightBukkitCore INSTANCE;
    public final LanguageAccess coreLanguage;
    private final Starlight plugin;
    private final QLibContext qLibContext;
    private final CommandManager commandManager;
    private final ModularApplicationContext gluonContext = StarlightBukkitCore.createContext(this);

    public StarlightBukkitCore(Starlight plugin) {
        this.plugin = plugin;
        this.qLibContext = QLibBukkitContext.getInstance(this.plugin);
        this.coreLanguage = this.qLibContext.language().access("starlight-core");
        this.commandManager = new StarlightCommandManager(this);

        SLPluginEnvironment.init(this.gluonContext, this);

        BukkitPlatform.init();
        QLibPlatform.register();
        BukkitAPI.init();
        INSTANCE = this;
    }

    public static ModularApplicationContext createContext(PluginApplication application) {
        return PluginApplication.createContext(application)
                .serviceManager(BukkitServiceManager::new)
                .moduleManager(BukkitModuleManager::new)
                .logProvider((c) -> new SLLogProvider())
                .applicationName("Starlight")
                .build();
    }

    public static StarlightBukkitCore instance() {
        return INSTANCE;
    }

    public static LanguageContainer lang() {
        return INSTANCE.qLibContext.language();
    }

    public void init() {
        this.gluonContext.initialize();
        this.qLibContext.init();
        this.gluonContext.registerPackage(this, SLInternalPackage.class);
    }

    public void shutdown() {
        try {
            this.qLibContext.close();
        } catch (Exception e) {
            LOGGER.catching(e);
        }
        try {
            this.gluonContext.shutdown();
        } catch (Exception e) {
            LOGGER.catching(e);
        }
        PluginPlatform.global().remove("starlight:core");
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ModularApplicationContext getGluonContext() {
        return this.gluonContext;
    }

    public LanguageAccess coreLanguage() {
        return this.coreLanguage;
    }

    @Override
    public ConfigContainer config() {
        return this.qLibContext.config();
    }

    @Override
    public LanguageContainer language() {
        return this.qLibContext.language();
    }

    @Override
    public String id() {
        return "starlight";
    }

    @Override
    public String folder() {
        return System.getProperty("user.dir") + "/plugins/starlight";
    }

    @Override
    public String configId() {
        return ProductInfo.CORE_ID;
    }

    @Override
    public Logger logger() {
        return LOGGER;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    interface QLibPlatform {
        LocaleHandler LOCALE_HANDLER = LocaleHandler.wrap((a, c) -> LocaleService.locale(a.getPointer(CommandSender.class)));

        AudienceHandler.MessageRenderer GLOBAL_VARS_TEXT_RENDERER = (audience, message) -> {
            message = PlaceHolderService.format(PlaceHolderService.format(message));

            if ((audience.getPointer(CommandSender.class) instanceof Player p)) {
                message = PlaceHolderService.formatPlayer(p, message);
            }

            return message;
        };

        AudienceHandler.MessageRenderer CHAT_COLOR_TEXT_RENDERER = (audience, message) -> {
            if (message == null) {
                return "null";
            }
            return ChatColor.translateAlternateColorCodes('&', message);
        };

        AudienceHandler.MessageProcessor COMPONENT_EXAMINER = (audience, component) -> {
            var locale = audience.locale();
            return TextExaminer.examine(component, audience, locale);
        };

        static void register() {
            QLib.textEngine().getMessagePreRenderPipeline().addLast("starlight:global-vars", GLOBAL_VARS_TEXT_RENDERER);
            QLib.textEngine().getMessageRenderPipeline().addLast("starlight:global-vars", GLOBAL_VARS_TEXT_RENDERER);
            QLib.textEngine().getMessageRenderPipeline().addLast("starlight:chat-color", CHAT_COLOR_TEXT_RENDERER);
            QLib.textEngine().getLocalePipeline().addLast("starlight:core", LOCALE_HANDLER);
            QLib.textEngine().getMessageProcessPipeline().addLast("starlight:examine", COMPONENT_EXAMINER);
        }
    }
}
