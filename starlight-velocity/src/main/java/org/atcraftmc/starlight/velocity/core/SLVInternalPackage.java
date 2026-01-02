package org.atcraftmc.starlight.velocity.core;

import me.gb2022.modular.pack.ApplicationPackageProvider;
import me.gb2022.modular.pack.ContentBuilder;
import org.atcraftmc.starlight.shared.service.RemoteMessageService;

public interface SLVInternalPackage {
    @ApplicationPackageProvider(id = "starlight-velocity")
    static void core(ContentBuilder b) {
        b.service(RemoteMessageService.class);
    }
}
