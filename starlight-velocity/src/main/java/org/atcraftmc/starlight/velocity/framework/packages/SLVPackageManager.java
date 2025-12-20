package org.atcraftmc.starlight.velocity.framework.packages;

import me.gb2022.commons.TriState;
import me.gb2022.modular.pack.AbstractPackageManager;
import me.gb2022.modular.pack.PackageManager;
import me.gb2022.modular.service.ApplicationService;
import me.gb2022.modular.service.ServiceHolder;
import me.gb2022.modular.service.ServiceLayer;
import me.gb2022.modular.service.injection.ServiceInject;
import me.gb2022.modular.service.injection.ServiceProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.velocity.StarlightVelocity;
import org.atcraftmc.starlight.velocity.framework.SLVService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@ApplicationService(id = "package", impl = SLVPackageManager.Impl.class, layer = ServiceLayer.FRAMEWORK)
public interface SLVPackageManager extends PackageManager<SLVPackage>, SLVService {
    Logger LOGGER = LogManager.getLogger("SLVPackageManager");

    @ServiceInject
    ServiceHolder<SLVPackageManager> INSTANCE = new ServiceHolder<>();

    @ServiceProvider
    static SLVPackageManager create() {
        return new Impl();
    }

    static SLVPackageManager getInstance() {
        return INSTANCE.get();
    }

    static void registerPackage(SLVPackage pkg) {
        INSTANCE.get().addPackage(pkg);
    }

    static void unregisterPackage(SLVPackage pkg) {
        INSTANCE.get().removePackage(pkg.getId());
    }

    static SLVPackage getModule(String id) {
        return INSTANCE.get().get(id);
    }

    static Set<SLVPackage> getByStatus(TriState status) {
        var result = new HashSet<SLVPackage>();
        for (String id : INSTANCE.get().getPackages().keySet()) {
            if (getPackageStatus(id) != status) {
                continue;
            }
            result.add(getPackage(id));
        }
        return result;
    }

    static SLVPackage getPackage(String id) {
        return INSTANCE.get().get(id);
    }

    static Set<String> getIdsByStatus(TriState status) {
        Set<String> result = new HashSet<>();
        for (String id : INSTANCE.get().getPackages().keySet()) {
            if (getPackageStatus(id) != status) {
                continue;
            }
            result.add(id);
        }
        return result;
    }

    //status
    static boolean isPackageEnabled(String id) {
        return getPackageStatus(id) == TriState.FALSE;
    }

    static TriState getPackageStatus(String id) {
        return INSTANCE.get().getStatus(id);
    }

    static Map<String, SLVPackage> getAllPackages() {
        return INSTANCE.get().getPackages();
    }

    final class Impl extends AbstractPackageManager<SLVPackage> implements SLVPackageManager {
        @Override
        public void saveStatus(Properties meta) {
            try {
                meta.store(new FileOutputStream(this.getStatusFile()), "auto generated file,please don't edit it.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void handleException(Throwable e) {
            LOGGER.catching(e);
        }

        @Override
        public Logger getLogger() {
            return LOGGER;
        }

        @Override
        public boolean isReservedPackage(SLVPackage pack) {
            return false;
        }

        @Override
        public boolean defaultPackageStatus(SLVPackage pack) {
            return true;
        }

        private File getStatusFile() {
            String path = StarlightVelocity.INSTANCE.get().folder() + "/data/packages.properties";
            File file = new File(path);
            if (!file.exists() || file.length() == 0) {
                if (file.getParentFile().mkdirs()) {
                    LOGGER.info("created package status file folder.");
                }
                try {
                    if (file.createNewFile()) {
                        LOGGER.info("created package status file.");
                    }
                } catch (IOException e) {
                    LOGGER.error("failed to create package status file");
                    return file;
                }
                return file;
            }
            return file;
        }
    }
}
