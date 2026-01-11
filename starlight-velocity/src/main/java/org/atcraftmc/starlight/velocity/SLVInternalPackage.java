package org.atcraftmc.starlight.velocity;

import me.gb2022.modular.pack.ApplicationPackageProvider;
import me.gb2022.modular.pack.ContentBuilder;
import org.atcraftmc.starlight.shared.service.RemoteMessageService;
import org.atcraftmc.starlight.velocity.basic.HUBCommand;
import org.atcraftmc.starlight.velocity.basic.LegacyForwardingProtect;
import org.atcraftmc.starlight.velocity.basic.ProxyMotd;
import org.atcraftmc.starlight.velocity.core.ProxyPlayerTrackService;
import org.atcraftmc.starlight.velocity.core.VelocityCommandManager;

public interface SLVInternalPackage {
    @ApplicationPackageProvider(id = "starlight-velocity-core")
    static void core(ContentBuilder b) {
        b.service(RemoteMessageService.class);
        b.service(ProxyPlayerTrackService.class);
        b.service(VelocityCommandManager.class);
    }


    @ApplicationPackageProvider(id = "starlight-velocity-basic")
    static void basic(ContentBuilder b) {
        b.module(LegacyForwardingProtect.class);
        b.module(HUBCommand.class);
        b.module(ProxyMotd.class);
    }
}
