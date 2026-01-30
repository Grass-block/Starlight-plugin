package org.atcraftmc.starlight.velocity;

import me.gb2022.modular.pack.ApplicationPackageProvider;
import me.gb2022.modular.pack.ContentBuilder;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.shared.service.RemoteMessageService;
import org.atcraftmc.starlight.velocity.basic.*;
import org.atcraftmc.starlight.velocity.core.ProxyPlayerDiscoveryService;
import org.atcraftmc.starlight.velocity.core.VelocityCommandManager;
import org.atcraftmc.starlight.velocity.core.VelocityPlaceHolderService;

public interface SLVInternalPackage {
    @ApplicationPackageProvider(id = "starlight-velocity")
    static void core(ContentBuilder b) {
        b.service(RemoteMessageService.class);
        b.service(ProxyPlayerDiscoveryService.class);
        b.service(VelocityCommandManager.class);
        b.service(VelocityPlaceHolderService.class);

        b.module(LegacyForwardingProtect.class);
        b.module(HUBCommand.class);
        b.module(ProxyMotd.class);
        b.module(TabSync.class);
        b.module(ServerStatementObserver.class);

        var attachment = b.getAttachment(PluginPackageAttachment.class);

        attachment.config("starlight-velocity");
        attachment.language("starlight-velocity", "zh_cn");
    }
}
