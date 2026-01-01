package org.atcraftmc.starlight.framework;

import me.gb2022.modular.ModularApplicationContext;
import me.gb2022.modular.module.ModuleContainer;
import me.gb2022.modular.module.ModuleManager;
import org.atcraftmc.starlight.shared.FilePath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PluginModuleManager extends ModuleManager {

    public PluginModuleManager(ModularApplicationContext context) {
        super(context);
    }

    @Override
    public void enable() {
        try {
            this.statusMap.load(new FileInputStream(this.getStatusFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getStatusFile() {
        String path = FilePath.pluginFolder("quark") + "/data/modules.properties";
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            if (file.getParentFile().mkdirs()) {
                this.getLogger().info("created package status file folder.");
            }
            try {
                if (file.createNewFile()) {
                    this.getLogger().info("created package status file.");
                }
            } catch (IOException e) {
                this.getLogger().error("failed to create package status file");
                return file;
            }
            return file;
        }
        return file;
    }

    @Override
    public void saveStatus(Properties meta) {
        try {
            meta.store(new FileOutputStream(this.getStatusFile()), "auto generated file,please don't edit it.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initializeModuleContainer(ModuleContainer handle) {
        handle.addAttachment(new PluginModuleAttachment());
    }
}
