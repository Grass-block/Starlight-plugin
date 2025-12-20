package org.atcraftmc.starlight.velocity.framework;

import me.gb2022.modular.module.ModuleManagerV2;
import me.gb2022.modular.service.ApplicationService;
import me.gb2022.modular.service.ServiceHolder;
import me.gb2022.modular.service.ServiceLayer;
import me.gb2022.modular.service.injection.ServiceInject;
import me.gb2022.modular.service.injection.ServiceProvider;
import org.apache.logging.log4j.LogManager;
import org.atcraftmc.starlight.velocity.StarlightVelocity;
import org.atcraftmc.starlight.velocity.framework.module.SLVModule;
import org.atcraftmc.starlight.velocity.framework.module.SLVModuleHandle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@ApplicationService(id = "module", layer = ServiceLayer.FRAMEWORK)
public class SLVModuleManager extends ModuleManagerV2<SLVModule, SLVModuleHandle> implements SLVService {
    @ServiceInject
    public static final ServiceHolder<SLVModuleManager> INSTANCE = new ServiceHolder<>();
    public static final String DATA_FILE = "%s/data/modules.properties";

    public SLVModuleManager() {
        super(LogManager.getLogger("SLVModuleManager"));
    }

    @ServiceProvider
    public static SLVModuleManager create() {
        return new SLVModuleManager();
    }

    public static SLVModuleManager getInstance() {
        return INSTANCE.get();
    }

    private File getStatusFile() {
        String path = DATA_FILE.formatted(StarlightVelocity.INSTANCE.get().folder());
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            if (file.getParentFile().mkdirs()) {
                this.logger.info("created module status file folder.");
            }
            try {
                if (file.createNewFile()) {
                    this.logger.info("created module status file.");
                }
            } catch (IOException e) {
                this.logger.error("failed to create status file");
                return file;
            }
            return file;
        }
        return file;
    }

    @Override
    public boolean getDefaultModuleStatus() {
        return true;
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
    public void handleException(Throwable throwable) {
        this.logger.catching(throwable);
    }
}
