package org.atcraftmc.starlight.framework.pack;

import me.gb2022.gluon.pack.ApplicationPackage;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.atcraftmc.starlight.framework.SLPluginHandle;
import org.atcraftmc.starlight.util.EarlyLoadingManager;
import org.atcraftmc.starlight.util.ProductMetadata;
import org.atcraftmc.starlight.util.dependency.LibraryManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public abstract class MultiPackageProvider extends JavaPlugin implements PackageProvider, SLPluginHandle {
    private final ProductMetadata metadata = ProductMetadata.createFromResource(this);
    private Set<ApplicationPackage> packages = new HashSet<>();
    private String coreInstanceId;

    @Override
    public final void onEnable() {
        LibraryManager.prepareEnvironment(Starlight.instance().getLibraryManager(),this);

        this.coreInstanceId = Starlight.instance().getInstanceUUID();
        if (!this.isCoreExist()) {
            return;
        }
        this.packages = createPackages();
    }

    @Override
    public void onLoad() {
        EarlyLoadingManager.scan(this.getClass(), this);
    }

    @Override
    public final @Nullable InputStream getResource(@NotNull String filename) {
        return super.getResource(filename);
    }

    @Override
    public LibraryManager getLibraryManager() {
        return Starlight.instance().getLibraryManager();
    }

    public final Set<ApplicationPackage> createPackages() {
        return StarlightBukkitCore.instance().getGluonContext().registerPackage(this, this.getClass());
    }

    @Override
    public final ClassLoader classLoader() {
        return super.getClassLoader();
    }

    @Override
    public final @NotNull File getFile() {
        return super.getFile();
    }

    @Override
    public final ProductMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public final void onDisable() {
        if (!this.isCoreContextMatch()) {
            return;
        }
        for (var p : this.packages) {
            StarlightBukkitCore.instance().getGluonContext().getPackageManager().removePackage(p.meta().id());
        }
    }

    @Override
    public final String name() {
        return getName();
    }

    @Override
    public final String getCoreInstanceId() {
        return coreInstanceId;
    }
}
