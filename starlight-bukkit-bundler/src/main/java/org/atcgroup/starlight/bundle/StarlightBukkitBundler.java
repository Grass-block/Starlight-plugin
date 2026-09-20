package org.atcgroup.starlight.bundle;

import org.atcraftmc.starlight.bundle.BundledPackageProvider;
import org.atcraftmc.starlight.bundle.BundlerRegistry;
import org.atcraftmc.starlight.framework.pack.SLPackageProvider;

@SLPackageProvider
@BundlerRegistry
public final class StarlightBukkitBundler {

    @BundlerRegistry
    public static void create(BundledPackageProvider provider) {
        provider.add("starlight-bukkit-bundler", StarlightBukkitBundler.class, BaseBundler.class, ExtensionBundler.class, LobbyBundler.class);
    }
}
