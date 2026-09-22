package org.atcgroup.starlight.bundle;

import me.gb2022.gluon.pack.ApplicationPackageProvider;
import me.gb2022.gluon.pack.ContentBuilder;
import org.atcgroup.starlight.bundle.chat.*;
import org.atcgroup.starlight.bundle.commands.*;
import org.atcgroup.starlight.bundle.console.*;
import org.atcgroup.starlight.bundle.display.*;
import org.atcgroup.starlight.bundle.management.*;
import org.atcgroup.starlight.bundle.proxy.*;
import org.atcgroup.starlight.bundle.security.*;
import org.atcgroup.starlight.bundle.security.scan.PluginBackdoorScanner;
import org.atcgroup.starlight.bundle.sideload.InventoryMenu;
import org.atcgroup.starlight.bundle.sideload.RecipeLoader;
import org.atcgroup.starlight.bundle.sideload.ResourcePackLoader;
import org.atcgroup.starlight.bundle.utilities.*;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.framework.pack.SLPackageProvider;

@SLPackageProvider
public interface BaseBundler {
    @ApplicationPackageProvider(id = "starlight-chat")
    static void chat(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(ChatAt.class);
        b.module(ChatComponent.class);
        b.module(Mute.class);
        b.module(ChatFilter.class);
        b.module(ChatReport.class);

        p.config("starlight-chat");
        p.language("/starlight-chat", "zh_cn");
        p.language("/starlight-chat", "zh_tw");
        p.language("/starlight-chat", "en_us");
        p.language("/starlight-chat", "fr_fr");
        p.language("/starlight-chat", "ja_jp");
        p.language("/starlight-chat", "ru_ru");
    }

    @ApplicationPackageProvider(id = "starlight-management")
    static void management(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        b.module(Ban.class);
        b.module(Maintenance.class);
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
        b.module(PlayerNameHeader.class); //todo [DFU] import header
        b.module(TabMenu.class);
        b.module(WelcomeMessage.class);
        b.module(WESessionRenderer.class);
        b.module(CustomKickMessage.class);
        b.module(PlayerJoinMessage.class);
        b.module(ChatAnnounce.class);
        b.module(HoverDisplay.class);

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
}
