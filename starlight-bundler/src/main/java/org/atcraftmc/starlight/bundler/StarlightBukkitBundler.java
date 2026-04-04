package org.atcraftmc.starlight.bundler;

import org.atcraftmc.quark.SLLobbyPackage;
import org.atcraftmc.starlight.SLBasicPackage;
import org.atcraftmc.starlight.SLGamePackage;
import org.atcraftmc.starlight.SLMusicPackage;
import org.atcraftmc.starlight.SLWorldGuardPackage;
import org.atcraftmc.starlight.bundle.BundledPackageProvider;
import org.atcraftmc.starlight.bundle.BundlerRegistry;

@BundlerRegistry
public interface StarlightBukkitBundler {

    @BundlerRegistry
    static void create(BundledPackageProvider provider) {
        provider.add("starlight-base", SLBasicPackage.class);
        provider.add("starlight-game", SLGamePackage.class);
        provider.add("starlight-ext-music", SLMusicPackage.class);
        provider.add("starlight-ext-worldguard", SLWorldGuardPackage.class);
        provider.add("starlight-ext-lobby", SLLobbyPackage.class);
    }
}
