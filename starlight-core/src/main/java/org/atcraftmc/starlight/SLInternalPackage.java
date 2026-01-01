package org.atcraftmc.starlight;

import me.gb2022.modular.pack.ApplicationPackageProvider;
import me.gb2022.modular.pack.ContentBuilder;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.TaskService;
import org.atcraftmc.starlight.core.WESessionTrackService;
import org.atcraftmc.starlight.core.custom.CustomBlockService;
import org.atcraftmc.starlight.core.data.ModuleDataService;
import org.atcraftmc.starlight.core.data.PlayerDataService;
import org.atcraftmc.starlight.core.permission.PermissionService;
import org.atcraftmc.starlight.core.placeholder.PlaceHolderService;
import org.atcraftmc.starlight.core.ui.UIManager;
import org.atcraftmc.starlight.data.record.RecordService;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.internal.*;
import org.atcraftmc.starlight.shared.service.JDBCService;
import org.atcraftmc.starlight.shared.service.RemoteMessageService;

public interface SLInternalPackage {
    static void modules(ContentBuilder b) {
        b.module(VersionLogViewer.class);
        b.module(ModrinthVersionCheck.class);
        b.module(CustomLanguagePackLoader.class);
        b.module(InstallationCheck.class);

        b.module(PAPISupport.class);
        b.module(ProtocolLibPlatformInjector.class);
        b.module(PlatformPatcher.class);
    }

    static void configs(ContentBuilder b) {
        var p = b.getAttachment(PluginPackageAttachment.class);

        p.language("starlight-core", "zh_cn");
        p.language("starlight-core", "en_us");
        p.language("common", "zh_cn");
        p.language("common", "en_us");
        p.config("starlight-core");
    }

    @ApplicationPackageProvider(id = "starlight-core", internal = true, description = "Internal package.")
    static void core(ContentBuilder b) {
        //foundation
        b.service(PlayerIdentificationService.class);
        b.service(JDBCService.class);
        b.service(TaskService.class);

        b.service(LocaleService.class);
        b.service(PermissionService.class);
        b.service(ModuleDataService.class);//legacy
        b.service(PlayerDataService.class);
        b.service(ProductService.class);

        b.service(CacheService.class);
        b.service(RecordService.class);
        b.service(PlaceHolderService.class);
        b.service(RemoteMessageService.class);
        b.service(UIManager.class);

        b.service(ChatForwardingService.class);
        b.service(PlayerBanService.class);
        b.service(CustomBlockService.class);
        b.service(WESessionTrackService.class);

        b.service(InternalServices.BungeeChannelSupplier.class);
        b.service(InternalServices.InternalCommandsProvider.class);
        b.service(InternalServices.CommandEventService.class);

        modules(b);
        configs(b);
    }
}