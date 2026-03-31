package org.atcraftmc.starlight.framework;

import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.ProductInfo;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.foundation.platform.PluginUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public interface SLPackageManager {
    Logger LOGGER = SLPluginEnvironment.createLogger("PackageManager");
    String CORE_PKG_ID = "starlight-core";

    static List<String> getSubPacksFromServer() {
        List<String> list = new ArrayList<>();
        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            if (!verify(p)) {
                continue;
            }
            list.add(p.getName());
        }
        return list;
    }

    static List<File> getSubPacksFromFolder() {
        List<File> list = new ArrayList<>();
        for (File f : PluginUtil.getAllPluginFiles()) {
            if (!verify(f)) {
                continue;
            }
            list.add(f);
        }
        return list;
    }

    static boolean verify(Plugin p) {
        if (p.getName().equals(ProductInfo.CORE_ID)) {
            return false;
        }

        return p.getResource(p.getName() + ".product-meta.json") != null;
    }

    static boolean verify(File f) {
        String id;
        try {
            id = PluginUtil.getPluginDescription(f).getName();
        } catch (InvalidDescriptionException e) {
            LOGGER.catching(e);
            return false;
        }

        if (id.equals(ProductInfo.CORE_ID)) {
            return false;
        }
        try {
            JarFile jf = new JarFile(f);
            if (jf.getJarEntry(id + ".product-meta.json") == null) {
                jf.close();
                return false;
            }
            jf.close();

            return true;
        } catch (IOException e) {
            LOGGER.catching(e);
            return false;
        }
    }

    @SuppressWarnings("unused")
    static void reload() {
        for (String s : getSubPacksFromServer()) {
            PluginUtil.unload(s);
        }
        for (File f : getSubPacksFromFolder()) {
            PluginUtil.load(f.getName());
        }
    }
}
