package org.atcraftmc.starlight.bundle;

import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.util.EarlyLoadingManager;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public final class BundledPackageProvider {
    public static final Logger LOGGER = SLPluginEnvironment.createLogger("BundleLoader");
    private final Class<?> handle;
    private final Set<BundledPluginContainer> containers = new HashSet<>();

    public BundledPackageProvider(ClassGetter getter) {
        var h = ((Class<?>) null);

        try {
            h = getter.get();
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.handle = h;

        if (this.handle != null) {
            if (!this.handle.isAnnotationPresent(BundlerRegistry.class)) {
                throw new IllegalArgumentException("BundledPackageProvider requires BundlerRegistry!");
            }
        }
    }

    public void add(String id, Class<?> registry) {
        this.containers.add(new BundledPluginContainer(id, registry));
    }

    public boolean isPresent() {
        return this.handle != null;
    }

    public void load() {
        if (!isPresent()) {
            return;
        }

        for (var c : this.containers) {
            c.enable();
        }
    }

    public void unload() {
        if (!isPresent()) {
            return;
        }

        for (var c : this.containers) {
            c.disable();
        }
    }

    public void preload() {
        if(this.handle != null) {
            for (var method : this.handle.getDeclaredMethods()) {
                method.setAccessible(true);

                if (!method.isAnnotationPresent(BundlerRegistry.class)) {
                    continue;
                }

                try {
                    method.invoke(null, this);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOGGER.info("failed to load package {}", method.getName());
                    LOGGER.catching(e);
                }
            }
        }

        for (var c : this.containers) {
            EarlyLoadingManager.scan(c.getHandle(), this);
        }
    }


    @FunctionalInterface
    public interface ClassGetter {
        Class<?> get() throws Exception;
    }
}
