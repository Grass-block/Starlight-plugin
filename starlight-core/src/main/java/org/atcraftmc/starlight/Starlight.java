package org.atcraftmc.starlight;

import me.gb2022.commons.Timer;
import me.gb2022.modular.ModularApplicationContext;
import me.gb2022.pluginsX.PluginService;
import net.kyori.adventure.text.ComponentLike;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.bukkit.BukkitPlatform;
import org.atcraftmc.qlib.bukkit.BukkitPluginConcept;
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
import org.atcraftmc.starlight.api.event.CoreEvent;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.TaskService;
import org.atcraftmc.starlight.core.placeholder.PlaceHolderService;
import org.atcraftmc.starlight.environment.PathManager;
import org.atcraftmc.starlight.foundation.TextExaminer;
import org.atcraftmc.starlight.foundation.command.StarlightCommandManager;
import org.atcraftmc.starlight.foundation.platform.APIProfileTest;
import org.atcraftmc.starlight.foundation.platform.BukkitUtil;
import org.atcraftmc.starlight.foundation.platform.PluginUtil;
import org.atcraftmc.starlight.framework.BukkitModuleManager;
import org.atcraftmc.starlight.framework.BukkitServiceManager;
import org.atcraftmc.starlight.framework.PluginApplication;
import org.atcraftmc.starlight.framework.SLPackageManager;
import org.atcraftmc.starlight.internal.command.InternalCommands;
import org.atcraftmc.starlight.metrics.Metrics;
import org.atcraftmc.starlight.util.ProductMetadata;
import org.atcraftmc.starlight.util.dependency.GradleDependency;
import org.atcraftmc.starlight.util.dependency.LibraryManager;
import org.atcraftmc.starlight.util.dependency.MavenRepo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

/**
 * public static Starlight PLUGIN;晚灯火阑珊处，夜难寐，赶工狂。
 */
public final class Starlight extends BukkitPluginConcept implements PluginApplication {
    public static final Logger LOGGER = LogManager.getLogger("Starlight-Core");
    public static Starlight PLUGIN;
    public static LanguageAccess LANGUAGE;
    public final LanguageContainer language = new LanguageContainer(this, ProductInfo.CORE_ID);
    private final ModularApplicationContext context = createContext(this);
    private final CommandManager commandManager = new StarlightCommandManager(this);
    private final ProductMetadata metadata = ProductMetadata.createFromResource(this);
    private LibraryManager libraryManager;
    private String uuid;
    private Metrics metrics;
    private boolean fastBoot;
    private boolean initialized;
    private boolean hasBundler = false;
    private boolean debug = true;

    public static ModularApplicationContext createContext(PluginApplication application) {
        return PluginApplication.createContext(application)
                .serviceManager(BukkitServiceManager::new)
                .moduleManager(BukkitModuleManager::new)
                .build();
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
        return PLUGIN;
    }

    public static LanguageContainer lang() {
        return instance().language();
    }

    @Override
    public ConfigContainer config() {
        return ConfigContainer.getInstance();
    }

    @Override
    public LanguageContainer language() {
        return language;
    }

    @Override
    public ClassLoader classLoader() {
        return getClassLoader();
    }

    @Override
    public File getFile() {
        return super.getFile();
    }

    @Override
    public ProductMetadata getMetadata() {
        return this.metadata;
    }

    private void operation(String operation, Runnable task) {
        LOGGER.info(operation);
        task.run();
    }

    private void hackDataFolder() throws Exception {
        var f_dataFolder = JavaPlugin.class.getDeclaredField("dataFolder");
        f_dataFolder.setAccessible(true);
        f_dataFolder.set(this, new File(folder()));

        var f_configFile = JavaPlugin.class.getDeclaredField("configFile");
        f_configFile.setAccessible(true);
        f_configFile.set(this, new File(folder() + "/config.yml"));
    }

    //----[plugin concept]----
    @Override
    public String id() {
        return ProductInfo.CORE_ID;
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

    //stages
    private void initializePluginEnv() {
        LOGGER.info("Plugin Environment: ");

        APIProfileTest.test();
        var threadedRegions = APIProfileTest.isFoliaServer();
        var modded = APIProfileTest.isMixedServer();
        this.uuid = UUID.randomUUID().toString();
        PLUGIN = this;
        PluginUtil.CORE_REF.set(this);
        LANGUAGE = this.language.access(ProductInfo.CORE_ID);

        LOGGER.info(" - platform: {}", APIProfileTest.getAPIProfile().toString());
        LOGGER.info(" - region scheduler: {}", threadedRegions);
        LOGGER.info(" - modded environment: {}", modded);
        LOGGER.info(" - instance UUID: {}", this.uuid);
        LOGGER.info(" - qlib environment: {}", PluginPlatform.global());
        LOGGER.info(" - bundler mode: {}", this.hasBundler);

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
        this.debug = config.getBoolean("config.plugin.debug");

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

    private void loadFullJar() {
        LOGGER.info("loading full jar...");

        var pluginsPath = System.getProperty("user.dir") + "/plugins/";
        var pluginsDir = new File(pluginsPath);
        var loader = Starlight.class.getClassLoader();

        File jar = null;
        for (File f : Objects.requireNonNull(pluginsDir.listFiles())) {
            if (f.isDirectory() || !f.getName().endsWith(".jar")) {
                continue;
            }

            try {
                if (PluginUtil.getPluginDescription(f).getName().equals(ProductInfo.CORE_ID)) {
                    jar = f;
                }
            } catch (InvalidDescriptionException ignored) {
            }
        }

        if (jar == null) {
            throw new RuntimeException("cannot find plugin!");
        }

        LibraryManager.loadFullJar(this.getClassLoader(), jar);
    }

    @Override
    public void onLoad() {
        try {
            hackDataFolder();
            SLPluginEnvironment.init(this.context, this, "starlight-core", new PathManager("starlight"));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //----[plugin]----
    @Override
    public void onEnable() {
        onLoad();

        PLUGIN = this;

        Timer.restartTiming();

        for (String s : ProductInfo.logo(this).split("\n")) {
            Bukkit.getConsoleSender().sendMessage(s);
        }

        BukkitPlatform.init();
        PluginPlatform.global().addLast("starlight:core", new StarlightBukkitPlatform());

        this.initializePluginEnv();
        this.initializeCoreConfiguration();

        BukkitUtil.callEventUnsafe(new CoreEvent.Launch(this));

        operation("loading libraries...", () -> {
            var repo = getConfig().getString("config.dependency.maven-repo");
            var cache = SLPluginEnvironment.getPathManager().getCurrentPluginFolder().append("cache").toString();

            assert repo != null;
            if (!repo.startsWith("http")) {
                repo = MavenRepo.valueOf(repo).getUrl();
            }

            this.libraryManager = new LibraryManager(repo, cache, !this.fastBoot);

            var deps = new HashSet<>(this.metadata.getDependencies());

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
        });

        if (!this.fastBoot) {
            this.loadFullJar();
        }

        operation("starting services...", () -> {
            this.context.registerPackage(this, SLInternalPackage.class);
        });

        if (this.hasBundler) {
            LOGGER.info("loading bundled packs...");
            //this.bundledPackageLoader.onEnable();
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

        if (this.hasBundler) {
            LOGGER.info("unloading bundled packs...");
            //this.bundledPackageLoader.onDisable();
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
        this.context.shutdown();

        LOGGER.info("done ({} ms)", Timer.passedTime());
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

    public boolean isDebug() {
        return debug;
    }

    public boolean isPluginInitialized() {
        return initialized;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public LibraryManager getLibraryManager() {
        return this.libraryManager;
    }

    public ModularApplicationContext context() {
        return this.context;
    }

    private static final class StarlightBukkitPlatform extends ForwardingPluginPlatform {
        @Override
        public MinecraftLocale locale(Object sender) {
            return LocaleService.locale(sender);
        }

        @Override
        public ComponentLike examineComponent(ComponentLike component, Object pointer, MinecraftLocale locale) {
            return TextExaminer.examine(component.asComponent(), locale);
        }

        @Override
        public String globalFormatMessage(String s) {
            return super.globalFormatMessage(PlaceHolderService.format(PlaceHolderService.format(s)));
        }
    }
}
