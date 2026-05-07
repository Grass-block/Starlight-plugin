package org.atcraftmc.starlight;

import me.gb2022.gluon.pack.ApplicationPackageProvider;
import me.gb2022.gluon.pack.ContentBuilder;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.framework.pack.MultiPackageProvider;
import org.atcraftmc.starlight.music.MusicPlayer;
import org.atcraftmc.starlight.music.MusicService;

public final class SLMusicPackage extends MultiPackageProvider {
    @ApplicationPackageProvider(id = "starlight-music")
    static void music(ContentBuilder b) {
        var i = b.getAttachment(PluginPackageAttachment.class);

        b.service(MusicService.class);
        b.module(MusicPlayer.class);
        //b.module(MusicGame.class);

        i.config("starlight-music");
        i.language("starlight-music", "zh_cn");
        i.language("starlight-music", "en_us");
        i.language("starlight-music", "fr_fr");
        i.language("starlight-music", "ja_jp");
        i.language("starlight-music", "ru_ru");
        i.language("starlight-music", "zh_tw");
    }
}
