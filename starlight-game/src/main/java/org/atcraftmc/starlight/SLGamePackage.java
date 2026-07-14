package org.atcraftmc.starlight;

import me.gb2022.gluon.pack.ApplicationPackageProvider;
import me.gb2022.gluon.pack.ContentBuilder;
import org.atcraftmc.starlight.framework.PluginPackageAttachment;
import org.atcraftmc.starlight.framework.pack.MultiPackageProvider;
import org.atcraftmc.starlight.framework.pack.SLPackageProvider;
import org.atcraftmc.starlight.oddities.Elevator;
import org.atcraftmc.starlight.tweaks.*;

@SLPackageProvider
public final class SLGamePackage extends MultiPackageProvider {

    @ApplicationPackageProvider(id = "starlight-tweaks")
    static void tweak(ContentBuilder b) {
        var i = b.getAttachment(PluginPackageAttachment.class);

        i.config("starlight-tweaks");
        i.language("starlight-tweaks", "zh_cn");
        i.language("starlight-tweaks", "en_us");
        i.language("starlight-tweaks", "fr_fr");
        i.language("starlight-tweaks", "ja_jp");
        i.language("starlight-tweaks", "ru_ru");
        i.language("starlight-tweaks", "zh_tw");

        b.module(CropClickHarvest.class);
        b.module(DispenserInteraction.class);
        b.module(DoubleDoorSync.class);
        b.module(RealisticSleep.class);
        b.module(VeinMiner.class);
        b.module(PortableFunctionalBlocks.class);
        b.module(PortableShulkerBox.class);
        b.module(ItemDropSecure.class);
        b.module(StairSeat.class);
        b.module(RealisticMinecart.class);
        b.module(SitOnPlayer.class);
        b.module(QuickDeposit.class);
    }

    @ApplicationPackageProvider(id = "starlight-oddities")
    static void oddities(ContentBuilder b) {
        var i = b.getAttachment(PluginPackageAttachment.class);

        i.config("starlight-oddities");
        i.language("starlight-oddities", "zh_cn");
        i.language("starlight-oddities", "en_us");
        i.language("starlight-oddities", "fr_fr");
        i.language("starlight-oddities", "ja_jp");
        i.language("starlight-oddities", "ru_ru");
        i.language("starlight-oddities","zh_tw");

        b.module(Elevator.class);
    }
}
