package org.atcraftmc.starlight.framework;

import me.gb2022.gluon.ModularApplicationContext;
import me.gb2022.gluon.ObjectOperationResult;
import me.gb2022.gluon.module.ModuleContainer;
import me.gb2022.gluon.module.ModuleManager;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.util.PluginAutoRegManager;
import org.atcraftmc.starlight.util.PluginDependencyInjector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PluginModuleManager extends ModuleManager {
    private final PluginDependencyInjector dependencyInjector = createDependencyInjector();
    private final PluginAutoRegManager autoRegManager = createAutoRegManager();

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

    public PluginDependencyInjector createDependencyInjector() {
        return new PluginDependencyInjector();
    }

    public PluginAutoRegManager createAutoRegManager() {
        return new PluginAutoRegManager();
    }

    private File getStatusFile() {
        String path = SLPluginEnvironment.getPathManager().getCurrentPluginFolder().toString() + "/data/modules.properties";
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            if (file.getParentFile().mkdirs()) {
                this.logger.info("created package status file folder.");
            }
            try {
                if (file.createNewFile()) {
                    this.logger.info("created package status file.");
                }
            } catch (IOException e) {
                this.logger.error("failed to create package status file");
                return file;
            }
            return file;
        }
        return file;
    }

    @Override
    public void handlePreEnable(ModuleContainer handle) {
        super.handlePreEnable(handle);
        this.dependencyInjector.inject(handle.getHandle(PluginModule.class));
        this.autoRegManager.attachModuleContainer(handle);
    }

    @Override
    public void handlePostDisable(ModuleContainer handle, ObjectOperationResult result) {
        super.handlePostDisable(handle, result);
        this.autoRegManager.detachModuleContainer(handle);
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
