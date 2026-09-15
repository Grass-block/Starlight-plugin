package org.atcgroup.starlight.bundle;

import org.atcgroup.starlight.bundle.worldguard.*;
import org.atcraftmc.starlight.bundle.BundledPackageProvider;
import org.atcraftmc.starlight.bundle.BundlerRegistry;
import org.atcraftmc.starlight.framework.pack.SLPackageProvider;
import org.atcraftmc.starlight.util.EarlyLoading;

@SLPackageProvider
@BundlerRegistry
public final class StarlightBukkitBundler {

    @BundlerRegistry
    public static void create(BundledPackageProvider provider) {
        provider.add("starlight-bukkit-bundler", StarlightBukkitBundler.class);
        provider.add("starlight-bukkit-base", BaseBundler.class);
        provider.add("starlight-bukkit-extension", ExtensionBundler.class);
        provider.add("starlight-bukkit-lobby", LobbyBundler.class);
    }

    @EarlyLoading
    public static void preload() {
        //deprecated
        WGExtraInfoService.validateFlag();
    }
}
