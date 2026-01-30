package org.atcraftmc.starlight;

import me.gb2022.modular.pack.ApplicationPackageProvider;
import me.gb2022.modular.pack.ContentBuilder;
import org.atcraftmc.starlight.chat.ChatAt;
import org.atcraftmc.starlight.chat.ChatComponent;
import org.atcraftmc.starlight.commands.*;
import org.atcraftmc.starlight.console.*;
import org.atcraftmc.starlight.core.VisualScoreboardService;
import org.atcraftmc.starlight.display.*;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.framework.pack.MultiPackageProvider;
import org.atcraftmc.starlight.management.*;
import org.atcraftmc.starlight.proxy.*;
import org.atcraftmc.starlight.security.*;
import org.atcraftmc.starlight.sideload.InventoryMenu;
import org.atcraftmc.starlight.sideload.RecipeLoader;
import org.atcraftmc.starlight.utilities.*;
import org.atcraftmc.starlight.warp.BackToDeath;
import org.atcraftmc.starlight.warp.RTP;
import org.atcraftmc.starlight.warp.TPA;
import org.atcraftmc.starlight.warp.Waypoints;

public final class SLBasicPackage extends MultiPackageProvider {

    @ApplicationPackageProvider(id = "starlight-management")
    public static void management(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(Ban.class);
        b.module(ChatFilter.class);
        b.module(ChatReport.class);
        b.module(Maintenance.class);
        b.module(Mute.class); //todo [DFU] import mute status
        b.module(TPSBar.class);
        b.module(ServerInfo.class);
        b.module(KickOnReload.class);
        b.module(PluginManagerCommand.class);
        b.module(VMGarbageCleaner.class);

        p.config("starlight-management");
        p.language("starlight-management", "zh_cn");
    }


    @ApplicationPackageProvider(id = "starlight-console")
    public static void console(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(ClearConsole.class);
        b.module(ConsoleExecute.class);
        b.module(CustomLogFormat.class);
        b.module(LogColorPatch.class);
        b.module(StopConfirm.class);

        p.language("starlight-console", "zh_cn");
    }

    @ApplicationPackageProvider(id = "starlight-warps")
    public static void warps(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(Waypoints.class);
        b.module(RTP.class);
        b.module(TPA.class);
        b.module(BackToDeath.class);

        p.config("starlight-warps");
        p.language("starlight-warps", "zh_cn");
        p.language("starlight-warps", "en_us");
    }

    @ApplicationPackageProvider(id = "starlight-utilities")
    public static void utilities(ContentBuilder b) {
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

        p.config("starlight-utilities");
        p.language("starlight-utilities", "zh_cn");
    }

    @ApplicationPackageProvider(id = "starlight-proxy")
    public static void proxy(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(ProxyChatSync.class);
        b.module(GeyserSkinRedirect.class);
        b.module(LegacyForwardingProtect.class);
        b.module(ProxyPing.class);
        b.module(ClientTransferSupport.class);

        p.config("starlight-proxy");
        p.language("starlight-proxy", "zh_cn");
    }

    //command extension and triggers
    @ApplicationPackageProvider(id = "starlight-commands")
    public static void commands(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(AnimateBlockCommand.class);
        b.module(CommandTabFix.class);

        b.module(EntityMotion.class);
        b.module(Execute.class);
        b.module(HatCommand.class);
        b.module(ItemBinding.class);
        b.module(SelfMessage.class);
        b.module(WorldEditCommands.class);

        p.language("starlight-commands", "zh_cn");
    }

    @ApplicationPackageProvider(id = "starlight-security")
    public static void security(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(AdvancedPermissionControl.class);
        b.module(ExplosionDefender.class); //todo [DFU] import whitelist region
        b.module(IMGRegulationSync.class);
        b.module(IPDefender.class); //todo [DFU] miss
        b.module(PermissionManager.class); //todo [DFU] import permission data
        b.module(WorldEditOperationDefender.class);
        b.module(GuestMode.class);

        p.config("starlight-security");
        p.language("starlight-security", "zh_cn");
    }


    @ApplicationPackageProvider(id = "starlight-chat")
    public static void chat(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(ChatAt.class);
        b.module(ChatComponent.class);

        p.config("starlight-chat");
        p.language("starlight-chat", "zh_cn");
    }

    @ApplicationPackageProvider(id = "starlight-display", description = "Create client's visual look, but not only UI.")
    public static void display(ContentBuilder b) {
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

        b.service(PlayerWelcomeService.class); //todo [DFU] import status-> first-join-detection

        p.config("starlight-display");
        p.language("starlight-display", "zh_cn");
        p.language("starlight-display", "en_us");
    }

    @ApplicationPackageProvider(id = "starlight-sideload")
    public static void sideload(ContentBuilder b) {
        b.module(RecipeLoader.class);
        b.module(InventoryMenu.class);
    }
}
