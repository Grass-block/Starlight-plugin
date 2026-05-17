package org.atcraftmc.starlight.display;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import net.kyori.adventure.text.ComponentLike;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.placeholder.PlaceHolderService;
import org.atcraftmc.starlight.core.view.PlayerUIService;
import org.atcraftmc.starlight.core.view.PlayerView;
import org.atcraftmc.starlight.core.view.UITrackingStateCallback;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.bukkit.entity.Player;

@ApplicationModule(id = "action-bar-hud", description = "Create a HUD display on actionbar title.")
@AutoRegister({Registrations.SERVER_EVENT,PlayerUIService.TRACKING})
public final class ActionBarHUD extends BukkitAbstractModule implements UITrackingStateCallback {
    @Inject
    private LanguageEntry language;

    private String render(Player player) {
        var loc = player.getLocation();
        var block = loc.getBlock();

        var locale = LocaleService.locale(player);
        var biome_n = block.getBiome().getKey().getNamespace();
        var biome_k = block.getBiome().getKey().getKey();

        var p = MessageAccessor.getMessage(this.language, locale, "position", loc.getX(), loc.getY(), loc.getZ());
        var b = MessageAccessor.getMessage(this.language, locale, "biome", biome_n, biome_k);
        var t = MessageAccessor.getMessage(this.language, locale, "time");
        var f = MessageAccessor.getMessage(this.language, locale, "face", loc.getYaw(), loc.getPitch());

        var template = config().value("template").string().replace("{position}", p).replace("{biome}", b).replace("{time}", t).replace(
                "{face}",
                f
        );

        return PlaceHolderService.formatPlayer(player, template);
    }

    @Override
    public void startRender(Player player, PlayerView ui) {
        PlayerUIService.getInstance(player).getActionbar_v2().registerIntervalProcess(
                this.getFullId(),
                -10,
                3,
                entity -> QLib.task().entity(entity),
                (p, c) -> {
                    var comp = QLib.textBuilder().buildComponent(render(p));
                    QLib.audience(p).sendActionBar((ComponentLike) comp);
                }
        );
    }

    @Override
    public void stopRender(Player player, PlayerView ui) {
        PlayerUIService.getInstance(player).getActionbar_v2().removeProcess(this.getFullId());
    }
}
