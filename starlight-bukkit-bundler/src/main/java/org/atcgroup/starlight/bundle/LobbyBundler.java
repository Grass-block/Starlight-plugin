package org.atcgroup.starlight.bundle;

import me.gb2022.gluon.pack.ApplicationPackageProvider;
import me.gb2022.gluon.pack.ContentBuilder;
import org.atcgroup.starlight.bundle.lobby.BackToSpawn;
import org.atcgroup.starlight.bundle.lobby.DefaultInventory;
import org.atcgroup.starlight.bundle.lobby.MapProtect;
import org.atcgroup.starlight.bundle.lobby.PlayerProtect;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.framework.pack.SLPackageProvider;

@SLPackageProvider
public
interface LobbyBundler {
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
}
