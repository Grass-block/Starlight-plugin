package org.atcraftmc.quark;

import me.gb2022.gluon.pack.ApplicationPackageProvider;
import me.gb2022.gluon.pack.ContentBuilder;
import org.atcraftmc.quark.lobby.BackToSpawn;
import org.atcraftmc.quark.lobby.DefaultInventory;
import org.atcraftmc.quark.lobby.MapProtect;
import org.atcraftmc.quark.lobby.PlayerProtect;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.framework.pack.MultiPackageProvider;
import org.atcraftmc.starlight.framework.pack.SLPackageProvider;

@SLPackageProvider
public final class SLLobbyPackage extends MultiPackageProvider {
    @ApplicationPackageProvider(id = "starlight-lobby")
    static void lobby(ContentBuilder b) {
        var i = b.getAttachment(PluginPackageAttachment.class);

        b.module(BackToSpawn.class);
        b.module(DefaultInventory.class);
        b.module(MapProtect.class);
        b.module(PlayerProtect.class);

        i.config("starlight-lobby");
        i.language("starlight-lobby", "zh_cn");
        i.language("starlight-lobby", "en_us");
        i.language("starlight-lobby", "fr_fr");
        i.language("starlight-lobby", "ja_jp");
        i.language("starlight-lobby", "ru_ru");
        i.language("starlight-lobby", "zh_tw");
    }
}
