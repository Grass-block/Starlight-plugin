package org.atcraftmc.starlight;

import me.gb2022.modular.pack.ApplicationPackageProvider;
import me.gb2022.modular.pack.ContentBuilder;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.framework.pack.MultiPackageProvider;
import org.atcraftmc.starlight.music.MusicGame;
import org.atcraftmc.starlight.music.MusicPlayer;
import org.atcraftmc.starlight.music.MusicService;

public final class SLMusicPackage extends MultiPackageProvider {
    @ApplicationPackageProvider(id = "starlight-music")
    static void music(ContentBuilder b) {
        var i = b.getAttachment(PluginPackageAttachment.class);

        b.service(MusicService.class);
        b.module(MusicPlayer.class);
        b.module(MusicGame.class);

        i.config("starlight-music");
        i.language("starlight-music", "zh_cn");
    }
}
