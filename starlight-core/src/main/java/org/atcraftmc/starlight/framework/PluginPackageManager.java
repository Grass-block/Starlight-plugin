package org.atcraftmc.starlight.framework;

import me.gb2022.gluon.ModularApplicationContext;
import me.gb2022.gluon.pack.ApplicationPackage;
import me.gb2022.gluon.pack.ContentBuilder;
import me.gb2022.gluon.pack.PackageManager;
import org.atcraftmc.qlib.PluginConcept;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.util.Identifiers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public final class PluginPackageManager extends PackageManager {
    private final Set<String> rejected = new HashSet<>();

    public PluginPackageManager(ModularApplicationContext context) {
        super(context);
    }

    @Override
    public void enable() throws Exception {
        try {
            this.statusMap.load(new FileInputStream(this.getStatusFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getStatusFile() {
        String path = SLPluginEnvironment.getPathManager().getCurrentPluginFolder().toString() + "/data/packages.properties";
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
    public boolean isRejectedPackage(ApplicationPackage pack) {
        return this.rejected.contains(pack.meta().id());
    }

    @Override
    public boolean defaultPackageStatus(ApplicationPackage pack) {
        return !this.rejected.contains(pack.meta().id());
    }

    @Override
    public void initializePackageBuilder(ContentBuilder builder) {
        builder.addAttachment(new PluginPackageAttachment());
    }

    @Override
    public boolean isReservedPackage(ApplicationPackage pack) {
        if (Identifiers.external(pack.meta().id()).equals(this.context().holder(PluginConcept.class).id())) {
            return true;
        }

        return super.isReservedPackage(pack);
    }

    public void addRejection(String id) {
        this.rejected.add(id);
    }

    public void removeRejection(String id) {
        this.rejected.remove(id);
    }
}
