package org.atcraftmc.starlight.shared;

import org.atcraftmc.qlib.PluginConcept;

public class SLPluginEnvironment {
    private static PluginConcept coreRef;
    private static String dataFolder;
    private static String pluginId;

    public static void setCoreRef(PluginConcept coreRef) {
        SLPluginEnvironment.coreRef = coreRef;
    }

    public static void setDataFolder(String dataFolder) {
        SLPluginEnvironment.dataFolder = dataFolder;
    }

    public static void setPluginId(String pluginId) {
        SLPluginEnvironment.pluginId = pluginId;
    }

    public static String getPluginId() {
        return pluginId;
    }

    public static PluginConcept getCoreRef() {
        return coreRef;
    }

    public static String getDataFolder() {
        return dataFolder;
    }
}
