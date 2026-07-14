package org.atcraftmc.starlight;

import me.gb2022.gluon.pack.ApplicationPackageProvider;
import me.gb2022.gluon.pack.ContentBuilder;
import org.atcraftmc.starlight.ai.AIChatService;
import org.atcraftmc.starlight.ai.AICommandChat;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.framework.pack.MultiPackageProvider;
import org.atcraftmc.starlight.framework.pack.SLPackageProvider;

@SLPackageProvider
public final class SLAiPackage extends MultiPackageProvider {
    @ApplicationPackageProvider(id = "starlight-ai")
    static void ai(ContentBuilder b) {
        var i = b.getAttachment(PluginPackageAttachment.class);

        b.service(AIChatService.class);

        b.module(AICommandChat.class);

        i.language("starlight-ai", "zh_cn");
    }
}
