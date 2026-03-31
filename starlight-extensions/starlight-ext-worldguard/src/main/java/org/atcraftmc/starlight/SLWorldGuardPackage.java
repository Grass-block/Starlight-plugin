package org.atcraftmc.starlight;

import me.gb2022.gluon.pack.ApplicationPackageProvider;
import me.gb2022.gluon.pack.ContentBuilder;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.framework.pack.MultiPackageProvider;
import org.atcraftmc.starlight.worldguard.WorldGuardClaimCommand;
import org.atcraftmc.starlight.worldguard.WorldGuardRegionHUD;
import org.atcraftmc.starlight.worldguard.WorldGuardRegionService;
import org.atcraftmc.starlight.worldguard.WorldGuardWECheck;

public final class SLWorldGuardPackage extends MultiPackageProvider {

    @ApplicationPackageProvider(id = "starlight-worldguard")
    public static void worldguard(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.service(WorldGuardRegionService.class);
        b.module(WorldGuardRegionHUD.class);
        b.module(WorldGuardWECheck.class);
        b.module(WorldGuardClaimCommand.class);

        p.language("starlight-worldguard", "zh_cn");
        p.config("starlight-worldguard");
    }
}
