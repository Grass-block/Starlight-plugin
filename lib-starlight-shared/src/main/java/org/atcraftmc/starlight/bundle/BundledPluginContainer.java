package org.atcraftmc.starlight.bundle;

import me.gb2022.gluon.pack.ApplicationPackage;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.framework.SLPluginConcept;
import org.atcraftmc.starlight.util.ProductMetadata;
import org.atcraftmc.starlight.util.dependency.LibraryManager;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public final class BundledPluginContainer implements SLPluginConcept {
    private final String id;
    private final Class<?> handle;
    private final ProductMetadata metadata;
    private final Set<ApplicationPackage> packages = new HashSet<>();

    public BundledPluginContainer(String id, Class<?> handle) {
        this.id = id;
        this.handle = handle;
        this.metadata = ProductMetadata.createFromResource(this);
    }

    public void enable() {
        LibraryManager.prepareEnvironment(SLPluginEnvironment.getLibraryManager(), this);
        SLPluginEnvironment.getContext().registerPackage(this, this.handle);
    }

    public void disable() {
        for (var pkg : this.packages) {
            SLPluginEnvironment.getContext().getPackageManager().removePackage(pkg.meta().id());
        }
    }

    @Override
    public ClassLoader classLoader() {
        return SLPluginEnvironment.getPlugin().classLoader();
    }

    @Override
    public File getFile() {
        return SLPluginEnvironment.getPlugin().getFile();
    }

    @Override
    public ProductMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public String name() {
        return this.id;
    }
}