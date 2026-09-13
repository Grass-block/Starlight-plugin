package org.atcgroup.starlight.bundle;

import me.gb2022.gluon.pack.ApplicationPackageProvider;
import me.gb2022.gluon.pack.ContentBuilder;
import org.atcgroup.starlight.bundle.ai.AIChatService;
import org.atcgroup.starlight.bundle.ai.AICommandChat;
import org.atcgroup.starlight.bundle.chat.*;
import org.atcgroup.starlight.bundle.commands.*;
import org.atcgroup.starlight.bundle.console.*;
import org.atcgroup.starlight.bundle.display.*;
import org.atcgroup.starlight.bundle.lobby.BackToSpawn;
import org.atcgroup.starlight.bundle.lobby.DefaultInventory;
import org.atcgroup.starlight.bundle.lobby.MapProtect;
import org.atcgroup.starlight.bundle.lobby.PlayerProtect;
import org.atcgroup.starlight.bundle.management.*;
import org.atcgroup.starlight.bundle.music.MusicPlayer;
import org.atcgroup.starlight.bundle.music.MusicService;
import org.atcgroup.starlight.bundle.proxy.*;
import org.atcgroup.starlight.bundle.security.*;
import org.atcgroup.starlight.bundle.security.scan.PluginBackdoorScanner;
import org.atcgroup.starlight.bundle.sideload.InventoryMenu;
import org.atcgroup.starlight.bundle.sideload.RecipeLoader;
import org.atcgroup.starlight.bundle.sideload.ResourcePackLoader;
import org.atcgroup.starlight.bundle.utilities.*;
import org.atcgroup.starlight.bundle.warp.BackToDeath;
import org.atcgroup.starlight.bundle.warp.RTP;
import org.atcgroup.starlight.bundle.warp.TPA;
import org.atcgroup.starlight.bundle.warp.Waypoints;
import org.atcgroup.starlight.bundle.worldguard.*;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.framework.pack.SLPackageProvider;
import org.atcraftmc.starlight.util.EarlyLoading;

@SLPackageProvider
public interface StarlightBukkitBundler {
    @ApplicationPackageProvider(id = "starlight-music")
    static void music(ContentBuilder b) {
        var i = b.getAttachment(PluginPackageAttachment.class);

        b.service(MusicService.class);
        b.module(MusicPlayer.class);
        //b.module(MusicGame.class);

        i.config("starlight-music");
        i.language("starlight-music", "zh_cn");
        i.language("starlight-music", "en_us");
        i.language("starlight-music", "fr_fr");
        i.language("starlight-music", "ja_jp");
        i.language("starlight-music", "ru_ru");
        i.language("starlight-music", "zh_tw");
    }

    @ApplicationPackageProvider(id = "starlight-ai")
    static void ai(ContentBuilder b) {
        var i = b.getAttachment(PluginPackageAttachment.class);

        b.service(AIChatService.class);
        b.module(AICommandChat.class);

        i.language("/starlight-ai", "zh_cn");
    }


    @ApplicationPackageProvider(id = "starlight-chat")
    static void chat(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(ChatAt.class);
        b.module(ChatComponent.class);

        p.config("starlight-chat");
        p.language("/starlight-chat", "zh_cn");
        p.language("/starlight-chat", "zh_tw");
        p.language("/starlight-chat", "en_us");
        p.language("/starlight-chat", "fr_fr");
        p.language("/starlight-chat", "ja_jp");
        p.language("/starlight-chat", "ru_ru");
    }

    @ApplicationPackageProvider(id = "starlight-lobby")
    static void lobby(ContentBuilder b) {
        var i = b.getAttachment(PluginPackageAttachment.class);

        b.module(BackToSpawn.class);
        b.module(DefaultInventory.class);
        b.module(MapProtect.class);
        b.module(PlayerProtect.class);

        i.config("starlight-lobby");

        i.language("/starlight-lobby", "zh_cn");
        i.language("/starlight-lobby", "en_us");
        i.language("/starlight-lobby", "fr_fr");
        i.language("/starlight-lobby", "ja_jp");
        i.language("/starlight-lobby", "ru_ru");
        i.language("/starlight-lobby", "zh_tw");
    }

    @ApplicationPackageProvider(id = "starlight-management")
    static void management(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(Ban.class);
        b.module(ChatFilter.class);
        b.module(ChatReport.class);
        b.module(Maintenance.class);
        b.module(Mute.class);
        b.module(TPSBar.class);
        b.module(ServerInfo.class);
        b.module(KickOnReload.class);
        b.module(PluginManagerCommand.class);
        b.module(VMGarbageCleaner.class);
        b.module(WorldObjectFilter.class);

        p.config("starlight-management");
        p.language("/starlight-management", "zh_cn");
        p.language("/starlight-management", "zh_tw");
        p.language("/starlight-management", "en_us");
        p.language("/starlight-management", "fr_fr");
        p.language("/starlight-management", "ja_jp");
        p.language("/starlight-management", "ru_ru");
    }

    @ApplicationPackageProvider(id = "starlight-console")
    static void console(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(ClearConsole.class);
        b.module(ConsoleExecute.class);
        b.module(CustomLogFormat.class);
        b.module(LogColorPatch.class);
        b.module(StopConfirm.class);

        p.language("/starlight-console", "zh_cn");
        p.language("/starlight-console", "zh_tw");
        p.language("/starlight-console", "en_us");
        p.language("/starlight-console", "fr_fr");
        p.language("/starlight-console", "ja_jp");
        p.language("/starlight-console", "ru_ru");
    }

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

    @ApplicationPackageProvider(id = "starlight-utilities")
    static void utilities(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(BlockUpdateLocker.class);
        b.module(Calculator.class);
        b.module(CameraMovement.class);
        b.module(DynamicViewDistance.class);
        b.module(Hitokoto.class);
        b.module(PlayerPingCommand.class);
        b.module(PositionLock.class);
        b.module(PositionAlign.class);
        b.module(SurroundingRefresh.class);
        b.module(TickManager.class);
        b.module(FreeCam.class);
        b.module(FlyCommand.class);
        b.module(ParticleFont.class);
        b.module(ClientEnvironmentSetting.class);
        b.module(MenuItem.class);
        b.module(InventoryProfile.class);
        b.module(ModernMinecartSync.class);

        p.config("starlight-utilities");
        p.language("/starlight-utilities", "zh_cn");
        p.language("/starlight-utilities", "zh_tw");
        p.language("/starlight-utilities", "en_us");
        p.language("/starlight-utilities", "fr_fr");
        p.language("/starlight-utilities", "ja_jp");
        p.language("/starlight-utilities", "ru_ru");
    }

    @ApplicationPackageProvider(id = "starlight-proxy")
    static void proxy(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(ProxyChatSync.class);
        b.module(GeyserSkinRedirect.class);
        b.module(LegacyForwardingProtect.class);
        b.module(ProxyPing.class);
        b.module(ClientTransferSupport.class);
        b.module(OutProxyInfoSync.class);

        p.config("starlight-proxy");
        p.language("/starlight-proxy", "zh_cn");
        p.language("/starlight-proxy", "zh_tw");
        p.language("/starlight-proxy", "en_us");
        p.language("/starlight-proxy", "fr_fr");
        p.language("/starlight-proxy", "ja_jp");
        p.language("/starlight-proxy", "ru_ru");
    }

    //command extension and triggers
    @ApplicationPackageProvider(id = "starlight-commands")
    static void commands(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(AnimateBlockCommand.class);
        b.module(CommandTabFix.class);

        b.module(EntityMotion.class);
        b.module(Execute.class);
        b.module(HatCommand.class);
        b.module(ItemBinding.class);
        b.module(SelfMessage.class);
        b.module(WorldEditCommands.class);

        p.language("/starlight-commands", "zh_cn");
        p.language("/starlight-commands", "zh_tw");
        p.language("/starlight-commands", "en_us");
        p.language("/starlight-commands", "fr_fr");
        p.language("/starlight-commands", "ja_jp");
        p.language("/starlight-commands", "ru_ru");
    }

    @ApplicationPackageProvider(id = "starlight-security")
    static void security(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(AdvancedPermissionControl.class);
        b.module(ExplosionDefender.class); //todo [DFU] import whitelist region
        b.module(IMGRegulationSync.class);
        b.module(IPDefender.class);
        b.module(PermissionManager.class); //todo [DFU] import permission data
        b.module(WorldEditOperationDefender.class);
        b.module(GuestMode.class);
        b.module(EndProtect.class);
        b.module(PluginBackdoorScanner.class);
        b.module(ItemDefender.class);

        p.config("starlight-security");
        p.language("/starlight-security", "zh_cn");
        p.language("/starlight-security", "zh_tw");
        p.language("/starlight-security", "en_us");
        p.language("/starlight-security", "fr_fr");
        p.language("/starlight-security", "ja_jp");
        p.language("/starlight-security", "ru_ru");
    }


    @ApplicationPackageProvider(id = "starlight-display", description = "Create client's visual look, but not only UI.")
    static void display(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);
        b.module(ActionBarHUD.class);
        b.module(AFK.class);
        b.module(ChatFormat.class);
        b.module(CustomDeathMessage.class);
        b.module(CustomMotd.class);
        b.module(CustomScoreboard.class);
        b.module(DropItemInfo.class);
        //i.module("hover-display", HoverDisplay.class); //todo [DFU] refine + import hover data
        b.module(PlayerNameHeader.class); //todo [DFU] import header
        b.module(TabMenu.class);
        b.module(WelcomeMessage.class);
        b.module(WESessionRenderer.class);
        b.module(CustomKickMessage.class);
        b.module(PlayerJoinMessage.class);
        b.module(ChatAnnounce.class);

        b.service(PlayerWelcomeService.class);

        p.config("starlight-display");
        p.language("/starlight-display", "zh_cn");
        p.language("/starlight-display", "zh_tw");
        p.language("/starlight-display", "en_us");
        p.language("/starlight-display", "fr_fr");
        p.language("/starlight-display", "ja_jp");
        p.language("/starlight-display", "ru_ru");
    }

    @ApplicationPackageProvider(id = "starlight-sideload")
    static void sideload(ContentBuilder b) {
        b.module(RecipeLoader.class);
        b.module(InventoryMenu.class);
        b.module(ResourcePackLoader.class);
    }

    @ApplicationPackageProvider(id = "starlight-worldguard")
    static void worldguard(ContentBuilder b) {
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

        p.config("starlight-worldguard");

        p.language("/starlight-worldguard", "zh_cn");
        p.language("/starlight-worldguard", "en_us");
        p.language("/starlight-worldguard", "fr_fr");
        p.language("/starlight-worldguard", "ja_jp");
        p.language("/starlight-worldguard", "ru_ru");
        p.language("/starlight-worldguard", "zh_tw");
    }

    @EarlyLoading
    static void preload() {
        //deprecated
        WGExtraInfoService.validateFlag();
    }
}
