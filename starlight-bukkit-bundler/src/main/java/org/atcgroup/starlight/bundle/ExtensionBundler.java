package org.atcgroup.starlight.bundle;

import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.gluon.pack.ApplicationPackageProvider;
import me.gb2022.gluon.pack.ContentBuilder;
import net.milkbowl.vault.economy.Economy;
import org.atcgroup.starlight.bundle.ai.AIChatService;
import org.atcgroup.starlight.bundle.ai.AICommandChat;
import org.atcgroup.starlight.bundle.economy.EconomyRewardProvider;
import org.atcgroup.starlight.bundle.economy.EconomyService;
import org.atcgroup.starlight.bundle.mission.CommissionService;
import org.atcgroup.starlight.bundle.mission.RewardService;
import org.atcgroup.starlight.bundle.music.MusicPlayer;
import org.atcgroup.starlight.bundle.music.MusicService;
import org.atcgroup.starlight.bundle.oddities.ChinesePeopleCanFly;
import org.atcgroup.starlight.bundle.oddities.CustomVehicle;
import org.atcgroup.starlight.bundle.oddities.Elevator;
import org.atcgroup.starlight.bundle.tweaks.*;
import org.atcgroup.starlight.bundle.warp.BackToDeath;
import org.atcgroup.starlight.bundle.warp.RTP;
import org.atcgroup.starlight.bundle.warp.TPA;
import org.atcgroup.starlight.bundle.warp.Waypoints;
import org.atcgroup.starlight.bundle.worldguard.*;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.framework.pack.SLPackageProvider;
import org.bukkit.Bukkit;

@SLPackageProvider
public interface ExtensionBundler {
    @ApplicationPackageProvider(id = "starlight-warps")
    static void warps(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(Waypoints.class);
        b.module(RTP.class);
        b.module(TPA.class);
        b.module(BackToDeath.class);

        p.config("starlight-warps");
        p.language("/starlight-warps", "zh_cn");
        p.language("/starlight-warps", "zh_tw");
        p.language("/starlight-warps", "en_us");
        p.language("/starlight-warps", "fr_fr");
        p.language("/starlight-warps", "ja_jp");
        p.language("/starlight-warps", "ru_ru");
    }

    @ApplicationPackageProvider(id = "starlight-tweaks")
    static void tweak(ContentBuilder b) {
        var i = b.getAttachment(PluginPackageAttachment.class);

        i.config("starlight-tweaks");
        i.language("/starlight-tweaks", "zh_cn");
        i.language("/starlight-tweaks", "en_us");
        i.language("/starlight-tweaks", "fr_fr");
        i.language("/starlight-tweaks", "ja_jp");
        i.language("/starlight-tweaks", "ru_ru");
        i.language("/starlight-tweaks", "zh_tw");

        b.module(CropClickHarvest.class);
        b.module(DispenserInteraction.class);
        b.module(DoubleDoorSync.class);
        b.module(RealisticSleep.class);
        b.module(VeinMiner.class);
        b.module(PortableFunctionalBlocks.class);
        b.module(PortableShulkerBox.class);
        b.module(ItemDropSecure.class);
        b.module(StairSeat.class);
        b.module(RealisticMinecart.class);
        b.module(SitOnPlayer.class);
        b.module(QuickDeposit.class);
        b.module(ExtraDamage.class);
    }

    @ApplicationPackageProvider(id = "starlight-oddities")
    static void oddities(ContentBuilder b) {
        var i = b.getAttachment(PluginPackageAttachment.class);

        i.config("starlight-oddities");
        i.language("/starlight-oddities", "zh_cn");
        i.language("/starlight-oddities", "en_us");
        i.language("/starlight-oddities", "fr_fr");
        i.language("/starlight-oddities", "ja_jp");
        i.language("/starlight-oddities", "ru_ru");
        i.language("/starlight-oddities", "zh_tw");

        b.module(Elevator.class);
        b.module(ChinesePeopleCanFly.class);

        b.module(CustomVehicle.class);
    }

    @ApplicationPackageProvider(id = "starlight-music")
    static void music(ContentBuilder b) {
        var i = b.getAttachment(PluginPackageAttachment.class);

        b.service(MusicService.class);
        b.module(MusicPlayer.class);
        //b.module(MusicGame.class);

        i.config("starlight-music");
        i.language("/starlight-music", "zh_cn");
        i.language("/starlight-music", "en_us");
        i.language("/starlight-music", "fr_fr");
        i.language("/starlight-music", "ja_jp");
        i.language("/starlight-music", "ru_ru");
        i.language("/starlight-music", "zh_tw");
    }

    @ApplicationPackageProvider(id = "starlight-ai")
    static void ai(ContentBuilder b) {
        var i = b.getAttachment(PluginPackageAttachment.class);

        b.service(AIChatService.class);
        b.module(AICommandChat.class);

        i.language("/starlight-ai", "zh_cn");
    }

    @ApplicationPackageProvider(id = "starlight-worldguard")
    static void worldguard(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.compatibilityProvider(() -> {
            Compatibility.requirePlugin("WorldGuard");
            Compatibility.requirePlugin("WorldEdit");
        });

        b.service(WGRegionService.class);
        b.service(WGCommandService.class);
        b.service(WGPlotInfoService.class);

        b.module(WGRegionHUD.class);
        b.module(WorldGuardWECheck.class);
        b.module(WGClaimCommand.class);
        b.module(WGCustomName.class);
        b.module(WGSpawnTeleport.class);

        p.config("starlight-worldguard");

        p.language("/starlight-worldguard", "zh_cn");
        p.language("/starlight-worldguard", "en_us");
        p.language("/starlight-worldguard", "fr_fr");
        p.language("/starlight-worldguard", "ja_jp");
        p.language("/starlight-worldguard", "ru_ru");
        p.language("/starlight-worldguard", "zh_tw");
    }

    //还没做完
    @ApplicationPackageProvider(id = "starlight-economy")
    static void economy(ContentBuilder b) {
        b.compatibilityProvider(() -> {
            Compatibility.requireClass(() -> Class.forName("net.milkbowl.vault.economy.Economy"));

            if (Bukkit.getServicesManager().getRegistration(Economy.class) == null) {
                throw new APIIncompatibleException("No 'economy' service found!");
            }
        });

        b.service(EconomyService.class);
        b.module(EconomyRewardProvider.class);
    }

    //还没做完
    //@ApplicationPackageProvider(id = "starlight-mission")
    static void mission(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.service(CommissionService.class);
        b.service(RewardService.class);

        p.language("/starlight-mission", "zh_cn");
        p.language("/starlight-mission", "zh_tw");
        p.language("/starlight-mission", "en_us");
        p.language("/starlight-mission", "fr_fr");
        p.language("/starlight-mission", "ja_jp");
        p.language("/starlight-mission", "ru_ru");
    }
}
