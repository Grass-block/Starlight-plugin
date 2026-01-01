package org.atcraftmc.starlight.framework.pack;

import me.gb2022.modular.pack.ApplicationPackage;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.framework.SLPluginConcept;
import org.atcraftmc.starlight.util.ProductMetadata;
import org.atcraftmc.starlight.util.dependency.LibraryManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public abstract class MultiPackageProvider extends JavaPlugin implements PackageProvider, SLPluginConcept {
    private final ProductMetadata metadata = ProductMetadata.createFromResource(this);
    private Set<ApplicationPackage> packages = new HashSet<>();
    private String coreInstanceId;

    @Override
    public void onEnable() {
        LibraryManager.prepareEnvironment(Starlight.instance().getLibraryManager(), this);

        this.coreInstanceId = Starlight.instance().getInstanceUUID();
        if (!this.isCoreExist()) {
            return;
        }
        this.packages = createPackages();
    }

    @Override
    public @Nullable InputStream getResource(@NotNull String filename) {
        return super.getResource(filename);
    }

    public Set<ApplicationPackage> createPackages() {
        return Starlight.instance().context().registerPackage(this, this.getClass());
    }

    @Override
    public ClassLoader classLoader() {
        return super.getClassLoader();
    }

    @Override
    public @NotNull File getFile() {
        return super.getFile();
    }

    @Override
    public ProductMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public void onDisable() {
        if (!this.isCoreContextMatch()) {
            return;
        }
        for (var p : this.packages) {
            Starlight.instance().context().getPackageManager().removePackage(p.meta().id());
        }
    }

    @Override
    public String getCoreInstanceId() {
        return coreInstanceId;
    }
}
