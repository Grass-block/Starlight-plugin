package org.atcraftmc.starlight;

import me.gb2022.gluon.ModularApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.config.PathManager;
import org.atcraftmc.starlight.framework.PluginApplication;
import org.atcraftmc.starlight.util.ForwardingLogger;
import org.atcraftmc.starlight.util.dependency.LibraryManager;

public final class SLPluginEnvironment {
    public static final Logger ROOT_LOGGER = LogManager.getLogger("Starlight");

    private static PluginApplication plugin;
    private static PathManager pathManager;
    private static ModularApplicationContext context;
    private static String pluginId;
    private static String corePackageName;

    private static boolean debug = false;

    public static void init(ModularApplicationContext c, PluginApplication p, String cpn, PathManager fm) {
        plugin = p;
        pluginId = p.id();
        corePackageName = cpn;
        pathManager = fm;
        context = c;
    }

    public static <I> I returnSafely(I object) {
        if (object == null) {
            throw new RuntimeException("Plugin environment is not initialized!");
        }

        return object;
    }

    public static String getPluginId() {
        return returnSafely(pluginId);
    }

    public static String getCorePackageName() {
        return returnSafely(corePackageName);
    }

    public static PluginApplication getPlugin() {
        return returnSafely(plugin);
    }

    public static ModularApplicationContext getContext() {
        return context;
    }

    public static PathManager getPathManager() {
        return returnSafely(pathManager);
    }

    public static LibraryManager getLibraryManager() {
        return getPlugin().getLibraryManager();
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean d) {
        debug = d;
    }

    public static Logger createLogger(String id) {
        return ForwardingLogger.prefixed(ROOT_LOGGER, id);
    }
}
