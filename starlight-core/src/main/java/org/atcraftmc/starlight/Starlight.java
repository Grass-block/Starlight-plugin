package org.atcraftmc.starlight;

import me.gb2022.commons.Timer;
import me.gb2022.gluon.ModularApplicationContext;
import me.gb2022.pluginsX.PluginService;
import net.kyori.adventure.text.ComponentLike;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.QLibContext;
import org.atcraftmc.qlib.audience.AudienceService;
import org.atcraftmc.qlib.audience.PointedAudience;
import org.atcraftmc.qlib.bukkit.BukkitPlatform;
import org.atcraftmc.qlib.bukkit.BukkitPluginConcept;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.bukkit.QLibBukkitContext;
import org.atcraftmc.qlib.command.CommandManager;
import org.atcraftmc.qlib.command.LegacyCommandManager;
import org.atcraftmc.qlib.config.ConfigContainer;
import org.atcraftmc.qlib.config.Queries;
import org.atcraftmc.qlib.config.YamlUtil;
import org.atcraftmc.qlib.language.LanguageAccess;
import org.atcraftmc.qlib.language.LanguageContainer;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.qlib.platform.ForwardingPluginPlatform;
import org.atcraftmc.qlib.platform.PluginPlatform;
import org.atcraftmc.qlib.text.pipe.AudienceHandler;
import org.atcraftmc.qlib.text.pipe.LocaleHandler;
import org.atcraftmc.starlight.api.event.CoreEvent;
import org.atcraftmc.starlight.bundle.BundledPackageProvider;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.TaskService;
import org.atcraftmc.starlight.core.placeholder.PlaceHolderService;
import org.atcraftmc.starlight.environment.PathManager;
import org.atcraftmc.starlight.foundation.BukkitAPI;
import org.atcraftmc.starlight.foundation.TextExaminer;
import org.atcraftmc.starlight.foundation.command.StarlightCommandManager;
import org.atcraftmc.starlight.foundation.platform.APIProfileTest;
import org.atcraftmc.starlight.foundation.platform.BukkitUtil;
import org.atcraftmc.starlight.foundation.platform.PluginUtil;
import org.atcraftmc.starlight.framework.BukkitServiceManager;
import org.atcraftmc.starlight.framework.PluginApplication;
import org.atcraftmc.starlight.framework.PluginPackageManager;
import org.atcraftmc.starlight.framework.SLPackageManager;
import org.atcraftmc.starlight.framework.module.BukkitModuleManager;
import org.atcraftmc.starlight.internal.command.InternalCommands;
import org.atcraftmc.starlight.metrics.Metrics;
import org.atcraftmc.starlight.util.ProductMetadata;
import org.atcraftmc.starlight.util.SLLogProvider;
import org.atcraftmc.starlight.util.dependency.GradleDependency;
import org.atcraftmc.starlight.util.dependency.LibraryManager;
import org.atcraftmc.starlight.util.dependency.MavenRepo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * <h3>This SHIT could work... Wait WTF how does it worked???</h3>
 * ——GrassBlock2022, Main developer
 */
public final class Starlight extends BukkitPluginConcept implements PluginApplication {
    public static final Logger LOGGER = SLPluginEnvironment.createLogger("Core");

    private final ModularApplicationContext context = createContext(this);
    private final QLibContext qLibContext = new QLibBukkitContext(this);
    public final LanguageAccess coreLanguage = this.qLibContext.language().access("starlight-core");

    private final ProductMetadata metadata = ProductMetadata.createFromResource(this);
    private final CommandManager commandManager = new StarlightCommandManager(this);
    private final BundledPackageProvider bundledPackageProvider = new BundledPackageProvider(() -> Class.forName(
            "org.atcraftmc.starlight.bundler.StarlightBukkitBundler"));

    private LibraryManager libraryManager;
    private String uuid;
    private Metrics metrics;
    private boolean fastBoot;
    private boolean initialized;

    public static ModularApplicationContext createContext(PluginApplication application) {
        return PluginApplication.createContext(application)
                .serviceManager(BukkitServiceManager::new)
                .moduleManager(BukkitModuleManager::new)
                .logProvider((c) -> new SLLogProvider())
                .applicationName("Starlight")
                .build();
    }

    public static void prepareReload() {
        Starlight.instance().onDisable();
        try {
            PluginPlatform.global().addLast("starlight:core", new Starlight.StarlightBukkitPlatform());
        } catch (Exception ignored) {
        }
        InternalCommands.register();
    }

    public static void reload(CommandSender audience) {
        BukkitUtil.callEventUnsafe(new CoreEvent.Reload());

        var task = (Runnable) () -> {
            var manager = Bukkit.getPluginManager();
            var holder = manager.getPlugins()[0]; //not good but maybe possible
            var service = new PluginService(manager, holder);

            var locale = LocaleService.locale(audience);
            var msg = Starlight.instance().language().item("starlight-core.reload.complete").component(locale);

            var serverPacks = SLPackageManager.getSubPacksFromServer();
            var folderPacks = SLPackageManager.getSubPacksFromFolder();
            var modernPluginManager = PluginUtil.INSTANCE;
            var coreFile = modernPluginManager.getFile(ProductInfo.CORE_ID);

            if (coreFile == null) {
                coreFile = modernPluginManager.getFile("starlight-bundler");
            }

            for (var id : serverPacks) {
                service.unload(id);
            }

            service.unload(ProductInfo.CORE_ID);

            service.load(coreFile);

            for (var file : folderPacks) {
                service.load(file);
            }

            audience.sendMessage(msg);

            LegacyCommandManager.sync();
        };

        task.run();

        BukkitUtil.callEventUnsafe(new CoreEvent.PostReload());
    }

    public static Starlight instance() {
        return ((Starlight) SLPluginEnvironment.getPlugin());
    }

    public static LanguageContainer lang() {
        return instance().language();
    }

    public static AudienceService<CommandSender> audiences() {
        return (AudienceService<CommandSender>) instance().qLibContext.audiences();
    }


    //----[plugin]----
    private void loadEnv() {
        try {
            hackDataFolder();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SLPluginEnvironment.init(this.context, this, "starlight-core", new PathManager("starlight"));
        BukkitAPI.init();
    }


    @Override
    public void onLoad() {
        loadEnv();

        if (this.bundledPackageProvider.isPresent()) {
            this.bundledPackageProvider.preload();
            LOGGER.info("Completed early-loading process.");
        }
    }

    @Override
    public void onEnable() {
        Timer.restartTiming();

        loadEnv();

        for (String s : ProductInfo.logo(this).split("\n")) {
            Bukkit.getConsoleSender().sendMessage(s);
        }

        this.initializePluginEnv();
        this.initializeCoreConfiguration();

        BukkitUtil.callEventUnsafe(new CoreEvent.Launch(this));

        this.initializeLibraries();

        if (!this.fastBoot) {
            this.loadFullJar();
        }

        LOGGER.info("Starting services...");
        this.context.registerPackage(this, SLInternalPackage.class);

        if (this.bundledPackageProvider.isPresent()) {
            LOGGER.info("loading bundled packs...");
            this.bundledPackageProvider.load();
        }

        LOGGER.info("done. ({} ms)", Timer.passedTime());
        this.initialized = true;

        BukkitUtil.callEventUnsafe(new CoreEvent.PostLaunch(this));
    }

    @Override
    public void onDisable() {
        if (!this.initialized) {
            InternalCommands.unregister();
            return;
        }
        this.initialized = false;

        BukkitUtil.callEventUnsafe(new CoreEvent.Dispose(this));
        Timer.restartTiming();
        LOGGER.info("stopping(v{}-{})...", ProductInfo.version(), ProductInfo.API_VERSION);

        if (this.bundledPackageProvider.isPresent()) {
            LOGGER.info("unloading bundled packs...");
            this.bundledPackageProvider.unload();
        }

        try {
            this.getMetrics().shutdown();
        } catch (Exception e) {
            LOGGER.warn("failed to shutdown metrics: {}", e.getMessage());
            LOGGER.catching(e);
        }

        LOGGER.info("Metrics shutdown successfully");

        LOGGER.info("broadcasting dispose event...");
        TaskService.runFinalizeTask();

        LOGGER.info("broadcasting dispose event...");
        BukkitUtil.callEventUnsafe(new CoreEvent.PostDispose(this));
        PluginPlatform.global().remove("starlight:core");
        try {
            this.qLibContext.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.context.shutdown();

        LOGGER.info("done ({} ms)", Timer.passedTime());
    }


    //----[plugin concept]----
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


    //----[bukkit plugin concept]----
    @Override
    public ConfigContainer config() {
        return this.qLibContext.config();
    }

    @Override
    public LanguageContainer language() {
        return this.qLibContext.language();
    }

    @Override
    public LibraryManager getLibraryManager() {
        return this.libraryManager;
    }

    @Override
    public ClassLoader classLoader() {
        return getClassLoader();
    }

    @Override
    public @NotNull File getFile() {
        return super.getFile();
    }

    @Override
    public ProductMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public String name() {
        return "starlight-core";
    }


    //----[initialization]----
    private void initializePluginEnv() {
        BukkitPlatform.init();

        QLibPlatform.register();

        PluginPlatform.global().addLast("starlight:core", new StarlightBukkitPlatform());

        LOGGER.info("Plugin Environment: ");

        APIProfileTest.test();
        var threadedRegions = APIProfileTest.isFoliaServer();
        var modded = APIProfileTest.isMixedServer();
        this.uuid = UUID.randomUUID().toString();
        PluginUtil.CORE_REF.set(this);

        LOGGER.info(" - platform: {}", APIProfileTest.getAPIProfile().toString());
        LOGGER.info(" - region scheduler: {}", threadedRegions);
        LOGGER.info(" - modded environment: {}", modded);
        LOGGER.info(" - instance UUID: {}", this.uuid);
        LOGGER.info(" - bundler mode: {}", this.bundledPackageProvider.isPresent());

        this.context.initialize();

        if (threadedRegions) {
            LOGGER.warn("detected Folia type(Threaded Regions API) environment. using threadedRegions task system.");
        }
        if (modded) {
            LOGGER.warn("detected Arclight type(Forge API) environment. try restart your server rather than /starlight reload.");
        }
        if (isFastBoot() && modded) {
            LOGGER.warn("fastboot are not available on Arclight type(Forge API) platform! disabling fast-boot.");
            this.fastBoot = false;
        }
        if (isFastBoot()) {
            LOGGER.warn("using Fast-Boot environment, hot-reload may not function well. RESTART your server if any error occurred.");
        }
    }

    private void initializeCoreConfiguration() {
        this.saveDefaultConfig();
        this.reloadConfig();

        var config = getConfig();
        var metrics = config.getBoolean("config.plugin.metrics");
        this.fastBoot = config.getBoolean("config.plugin.fast-boot");
        SLPluginEnvironment.setDebug(config.getBoolean("config.plugin.debug"));

        var rejection = config.getStringList("config.default-status.disabled-packages");

        for (var p : rejection) {
            ((PluginPackageManager) this.context.getPackageManager()).addRejection(p);
        }
        LOGGER.info(" - disabled package: {}", rejection);

        try {
            ProductInfo.METADATA.load(getClass().getClassLoader().getResourceAsStream("product-info.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var templateResource = Objects.requireNonNull(getClass().getResourceAsStream("/config.yml"));
        var template = YamlConfiguration.loadConfiguration(new InputStreamReader(templateResource));

        YamlUtil.update(config, template, false, 3);

        saveConfig();

        Queries.setEnvironmentVars(Objects.requireNonNull(config.getConfigurationSection("config.environment")));

        LOGGER.info(" - core version: {}[{}]", ProductInfo.version(), ProductInfo.API_VERSION);
        LOGGER.info(" - fast boot: {}", this.fastBoot);
        LOGGER.info(" - metrics: {}", metrics);

        if (metrics) {
            this.metrics = new Metrics(this, ProductInfo.BSTATS_ID);
        }
    }

    private void initializeLibraries() {
        LOGGER.info("Loading libraries...");

        var repo = getConfig().getString("config.dependency.maven-repo");
        var cache = SLPluginEnvironment.getPathManager().getCurrentPluginFolder().append("cache").toString();

        assert repo != null;
        if (!repo.startsWith("http")) {
            repo = MavenRepo.valueOf(repo).getUrl();
        }

        this.libraryManager = new LibraryManager(repo, cache, !this.fastBoot);

        var deps = new ArrayList<>(this.metadata.getDependencies());

        try {
            Class.forName("net.kyori.adventure.Adventure");
        } catch (ClassNotFoundException e) {
            this.logger().info("failed to locate library, injecting adventure API...");
            deps.add(GradleDependency.fromGradle("net.kyori:adventure-api:4.17.0"));
            deps.add(GradleDependency.fromGradle("net.kyori:adventure-text-serializer-gson:4.17.0"));
            deps.add(GradleDependency.fromGradle("net.kyori:adventure-text-serializer-legacy:4.17.0"));
            deps.add(GradleDependency.fromGradle("net.kyori:adventure-text-minimessage:4.17.0"));
        }

        this.libraryManager.resolveDependencies(deps);
        this.libraryManager.injectLibraries(this);
    }

    private void loadFullJar() {
        LOGGER.info("loading full jar...");

        var pluginsPath = System.getProperty("user.dir") + "/plugins/";
        var pluginsDir = new File(pluginsPath);

        File jar = null;
        for (File f : Objects.requireNonNull(pluginsDir.listFiles())) {
            if (f.isDirectory() || !f.getName().endsWith(".jar")) {
                continue;
            }

            try {
                var n = PluginUtil.getPluginDescription(f).getName();
                var n1 = this.getDescription().getName();
                var b1 = n.equals(ProductInfo.CORE_ID);
                var b2 = n.equals("starlight-bundler");
                var b3 = n.equals(n1);

                if (b1 || b2 || b3) {
                    jar = f;
                }
            } catch (InvalidDescriptionException ignored) {
            }
        }

        if (jar == null) {
            throw new RuntimeException("cannot find plugin!");
        }

        getLibraryManager().loadFullJar(this.getClassLoader(), jar, true);
    }


    //----[properties]----
    public String getInstanceUUID() {
        return this.uuid;
    }

    public boolean isFastBoot() {
        return this.fastBoot;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public boolean isPluginInitialized() {
        return initialized;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ModularApplicationContext context() {
        return this.context;
    }

    public LanguageAccess coreLanguage() {
        return this.coreLanguage;
    }

    public boolean isBundler() {
        return this.bundledPackageProvider.isPresent();
    }

    private void hackDataFolder() throws Exception {
        var f_dataFolder = JavaPlugin.class.getDeclaredField("dataFolder");
        f_dataFolder.setAccessible(true);
        f_dataFolder.set(this, new File(folder()));

        var f_configFile = JavaPlugin.class.getDeclaredField("configFile");
        f_configFile.setAccessible(true);
        f_configFile.set(this, new File(folder() + "/config.yml"));
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

    public static final class StarlightBukkitPlatform extends ForwardingPluginPlatform {
        @Override
        public MinecraftLocale locale(Object sender) {
            return LocaleService.locale(sender);
        }
    }
}
