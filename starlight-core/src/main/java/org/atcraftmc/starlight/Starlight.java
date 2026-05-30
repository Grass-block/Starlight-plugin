package org.atcraftmc.starlight;

import me.gb2022.commons.Timer;
import me.gb2022.pluginsX.PluginService;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.bukkit.BukkitEventManager;
import org.atcraftmc.qlib.bukkit.BukkitPluginConcept;
import org.atcraftmc.qlib.command.LegacyCommandManager;
import org.atcraftmc.qlib.config.Queries;
import org.atcraftmc.qlib.config.YamlUtil;
import org.atcraftmc.starlight.api.event.CoreEvent;
import org.atcraftmc.starlight.bundle.BundledPackageProvider;
import org.atcraftmc.starlight.config.PathManager;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.platform.APIProfileTest;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.atcraftmc.starlight.core.platform.PluginUtil;
import org.atcraftmc.starlight.framework.PluginPackageManager;
import org.atcraftmc.starlight.framework.SLPackageManager;
import org.atcraftmc.starlight.framework.SLPluginHandle;
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
public final class Starlight extends BukkitPluginConcept implements SLPluginHandle {
    public static final String BUNDLE_LOADER = "org.atcraftmc.starlight.bundler.StarlightBukkitBundler";
    public static final Logger LOGGER = SLPluginEnvironment.createLogger("Core");
    private final ProductMetadata metadata = ProductMetadata.createFromResource(this);
    private final BundledPackageProvider bundledPackageProvider = new BundledPackageProvider(() -> Class.forName(BUNDLE_LOADER));
    private LibraryManager libraryManager;
    private String uuid;
    private Metrics metrics;
    private boolean fastBoot;
    private boolean initialized;

    private StarlightBukkitCore core;

    public Starlight() {
        SLPluginEnvironment.init(this, "starlight-core", new PathManager("starlight"));
    }

    public static void prepareReload() {
        Starlight.instance().onDisable();
        InternalCommands.register();
    }

    public static void reload(CommandSender audience) {
        BukkitUtil.callEventUnsafe(new CoreEvent.Reload());

        var task = (Runnable) () -> {
            var manager = Bukkit.getPluginManager();
            var holder = manager.getPlugins()[0]; //not good but maybe possible
            var service = new PluginService(manager, holder);

            var locale = LocaleService.locale(audience);
            var msg = StarlightBukkitCore.instance().language().item("starlight-core.reload.complete").component(locale);

            if (Starlight.instance().isPluginInitialized()) {
                prepareReload();
            }

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


    //----[plugin]----
    private void loadEnv() {
        try {
            hackDataFolder();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        for (String s : ProductInfo.logo(this).split("\n")) {
            Bukkit.getConsoleSender().sendMessage(s);
        }

        Timer.restartTiming();

        try {
            ProductInfo.METADATA.load(getClass().getClassLoader().getResourceAsStream("product-info.properties"));
        } catch (IOException e) {
            LOGGER.info("FAILED TO LOAD PRODUCT INFO, ABORTING STARTUP!");
            throw new RuntimeException(e);
        }

        loadEnv();

        this.initializePluginEnv();

        this.saveDefaultConfig();
        this.reloadConfig();
        var config = getConfig();
        var metrics = config.getBoolean("config.plugin.metrics");
        var rejection = config.getStringList("config.default-status.disabled-packages");
        this.fastBoot = config.getBoolean("config.plugin.fast-boot");

        var templateResource = Objects.requireNonNull(getClass().getResourceAsStream("/config.yml"));
        var template = YamlConfiguration.loadConfiguration(new InputStreamReader(templateResource));
        YamlUtil.update(config, template, false, 3);
        saveConfig();

        LOGGER.info(" - core version: {}[{}]", ProductInfo.version(), ProductInfo.API_VERSION);
        LOGGER.info(" - fast boot: {}", this.fastBoot);
        LOGGER.info(" - metrics: {}", metrics);
        LOGGER.info(" - disabled package: {}", rejection);

        SLPluginEnvironment.setDebug(config.getBoolean("config.plugin.debug"));

        this.initializeLibraries();
        this.loadFullJar();

        this.core = new StarlightBukkitCore(this);

        if (metrics) {
            this.metrics = new Metrics(this, ProductInfo.BSTATS_ID);
        }

        for (var p : rejection) {
            ((PluginPackageManager) this.core.getGluonContext().getPackageManager()).addRejection(p);
        }

        Queries.setEnvironmentVars(Objects.requireNonNull(config.getConfigurationSection("config.environment")));

        BukkitUtil.callEventUnsafe(new CoreEvent.Launch(this));

        LOGGER.info("Starting services...");
        this.core.init();

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

        if (this.bundledPackageProvider.isPresent()) {
            LOGGER.info("unloading bundled packs...");
            this.bundledPackageProvider.unload();
        }

        LOGGER.info("Closing platform...");
        BukkitUtil.callEventUnsafe(new CoreEvent.PostDispose(this));

        this.core.shutdown();
        this.core = null;

        LOGGER.info("Cleaning up event manager...");
        BukkitEventManager.cleanup(this);

        LOGGER.info("shutting down metrics...");
        try {
            this.getMetrics().shutdown();
        } catch (Exception e) {
            LOGGER.warn("failed to shutdown metrics: {}", e.getMessage());
            LOGGER.catching(e);
        }

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


    //----[plugin concept]----

    @Override
    public LibraryManager getLibraryManager() {
        return this.libraryManager;
    }

    @Override
    public ClassLoader classLoader() {
        return Starlight.class.getClassLoader();
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

    private void initializeLibraries() {
        LOGGER.info("Loading libraries...");

        var repo = getConfig().getString("config.dependency.maven-repo");
        var cache = SLPluginEnvironment.getPathManager().getCurrentPluginFolder().append("cache").toString();

        assert repo != null;
        if (!repo.startsWith("http")) {
            repo = MavenRepo.valueOf(repo).getUrl();
        }

        this.libraryManager = new LibraryManager(repo, cache, !this.fastBoot);

        try {
            Class.forName("net.kyori.adventure.text.Component");
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
        } catch (Throwable e) {
            LOGGER.info("No adventure or starlight-adventure-loader found, loading adventure libraries...");

            var deps = new ArrayList<GradleDependency>();

            deps.add(GradleDependency.fromGradle("net.kyori:adventure-key:4.17.0"));
            deps.add(GradleDependency.fromGradle("net.kyori:option:1.1.0"));
            deps.add(GradleDependency.fromGradle("net.kyori:adventure-api:4.17.0"));
            deps.add(GradleDependency.fromGradle("net.kyori:adventure-text-serializer-gson:4.17.0"));
            deps.add(GradleDependency.fromGradle("net.kyori:adventure-text-serializer-json:4.17.0"));
            deps.add(GradleDependency.fromGradle("net.kyori:adventure-text-serializer-legacy:4.17.0"));
            deps.add(GradleDependency.fromGradle("net.kyori:adventure-text-serializer-plain:4.17.0"));
            deps.add(GradleDependency.fromGradle("net.kyori:adventure-text-minimessage:4.17.0"));

            deps.add(GradleDependency.fromGradle("net.kyori:examination-api:1.3.0"));
            deps.add(GradleDependency.fromGradle("net.kyori:examination-string:1.3.0"));

            var l2 = new LibraryManager(repo, cache, !this.fastBoot);
            l2.resolveDependencies(deps);
            l2.injectLibraries(this);
        }

        this.libraryManager.resolveDependencies(new ArrayList<>(this.metadata.getDependencies()));
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
        return this.core != null;
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
}
