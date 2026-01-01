package org.atcraftmc.starlight.environment;

import me.gb2022.commons.file.FilePath;

public class PathManager {
    private final String pluginFolderName;

    public PathManager(String folderName) {
        this.pluginFolderName = folderName;
    }

    public String getPluginFolderName() {
        return pluginFolderName;
    }

    public FilePath getPluginListFolder() {
        return FilePath.RUNTIME.append("plugins");
    }

    public FilePath getPluginDataFolder(String id) {
        return getPluginListFolder().append(id);
    }

    public FilePath getCurrentPluginFolder() {
        return getPluginDataFolder(this.pluginFolderName);
    }
}
