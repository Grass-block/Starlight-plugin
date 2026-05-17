package org.atcraftmc.starlight.display;

import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.language.Language;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.VisualScoreboardService;
import org.atcraftmc.starlight.core.placeholder.PlaceHolderService;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.core.view.PlayerUIService;
import org.atcraftmc.starlight.core.view.PlayerView;
import org.atcraftmc.starlight.core.view.SchedulerProvider;
import org.atcraftmc.starlight.core.view.UITrackingStateCallback;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.bukkit.entity.Player;

@AutoRegister({Registrations.SERVER_EVENT, PlayerUIService.TRACKING})
@ApplicationModule(id = "custom-scoreboard", version = "0.2")
public final class CustomScoreboard extends BukkitAbstractModule implements UITrackingStateCallback {

    @Inject
    private LanguageEntry language;

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requireClass(() -> Class.forName("org.bukkit.scoreboard.Scoreboard"));
    }

    @Override
    public void startRender(Player player, PlayerView ui) {
        ui.getScoreboard().registerIntervalProcess(
                this.getFullId(),
                -10,
                20,
                SchedulerProvider.ASYNC,
                (p, t) -> renderScoreboard(p)
        );
    }

    @Override
    public void stopRender(Player player, PlayerView ui) {
        ui.getScoreboard().removeProcess(this.getFullId());
    }

    private void renderScoreboard(Player player) {
        var board = VisualScoreboardService.instance().visualScoreboard(player);
        var locale = LocaleService.locale(player);
        var title = QLib.textBuilder().buildComponent(this.language.item("title").message(locale).getRawMessage());
        var template = Language.generateTemplate(this.config(), "ui");
        var uiRaw = MessageAccessor.buildTemplate(this.language, locale, template).replace("{player}", player.getName());
        var uiBlock = QLib.textBuilder().buildStringBlocks(PlaceHolderService.formatPlayer(player, uiRaw));

        board.renderSidebar(title.asComponent(), uiBlock);
    }
}
