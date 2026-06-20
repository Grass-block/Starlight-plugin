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

        //deprecated
        b.service(WGExtraInfoService.class);

        b.service(WGRegionService.class);
        b.service(WGCommandService.class);
        b.service(WGPlotInfoService.class);

        b.module(WGRegionHUD.class);
        b.module(WorldGuardWECheck.class);
        b.module(WGClaimCommand.class);
        b.module(WGCustomName.class);
        b.module(WGSpawnTeleport.class);

        p.language("starlight-worldguard", "zh_cn");
        p.language("starlight-worldguard", "en_us");
        p.language("starlight-worldguard", "fr_fr");
        p.language("starlight-worldguard", "ja_jp");
        p.language("starlight-worldguard", "ru_ru");
        p.language("starlight-worldguard", "zh_tw");
        p.config("starlight-worldguard");
    }

    @EarlyLoading
    public static void preload() {
        //deprecated
        //WGExtraInfoService.validateFlag();
    }
}
