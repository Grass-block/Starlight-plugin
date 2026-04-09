package org.atcraftmc.starlight;

import me.gb2022.gluon.pack.ApplicationPackageProvider;
import me.gb2022.gluon.pack.ContentBuilder;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.framework.pack.MultiPackageProvider;
import org.atcraftmc.starlight.util.EarlyLoading;
import org.atcraftmc.starlight.worldguard.*;

public final class SLWorldGuardPackage extends MultiPackageProvider {

    @ApplicationPackageProvider(id = "starlight-worldguard")
    public static void worldguard(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.service(WorldGuardRegionService.class);
        b.service(WorldGuardExtraInfoService.class);

        b.module(WGRegionHUD.class);
        b.module(WorldGuardWECheck.class);
        b.module(WGClaimCommand.class);
        b.module(WGCustomName.class);

        p.language("starlight-worldguard", "zh_cn");
        p.config("starlight-worldguard");
    }

    @EarlyLoading
    public static void preload() {
        WorldGuardExtraInfoService.validateFlag();
    }
}
