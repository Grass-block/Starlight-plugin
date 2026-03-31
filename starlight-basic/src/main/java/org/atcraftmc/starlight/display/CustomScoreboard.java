package org.atcraftmc.starlight.display;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.language.Language;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.qlib.texts.TextBuilder;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.view.PlayerUIService;
import org.atcraftmc.starlight.core.VisualScoreboardService;
import org.atcraftmc.starlight.core.placeholder.PlaceHolderService;
import org.atcraftmc.starlight.core.view.SchedulerProvider;
import org.atcraftmc.starlight.foundation.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AutoRegister(Registrations.SERVER_EVENT)
@ApplicationModule(id = "custom-scoreboard", version = "0.2")
public final class CustomScoreboard extends BukkitAbstractModule {

    @Inject
    private LanguageEntry language;

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requireClass(() -> Class.forName("org.bukkit.scoreboard.Scoreboard"));
    }


    public void startRender(Player player) {
        PlayerUIService.getInstance(player).getScoreboard().registerIntervalProcess(
                this.getFullId(),
                -10,
                20,
                SchedulerProvider.ASYNC,
                (p, t) -> renderScoreboard(p)
        );
    }


    @Override
    public void enable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            startRender(p);
        }
    }

    @Override
    public void disable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerUIService.getInstance(p).getScoreboard().removeProcess(this.getFullId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        startRender(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerUIService.getInstance(event.getPlayer()).getScoreboard().removeProcess(this.getFullId());
    }

    private void renderScoreboard(Player player) {
        var board = VisualScoreboardService.instance().visualScoreboard(player);
        var locale = LocaleService.locale(player);
        var title = this.language.item("title").component(locale);
        var template = Language.generateTemplate(this.config(), "ui");
        var uiRaw = MessageAccessor.buildTemplate(this.language, locale, template).replace("{player}", player.getName());
        var uiBlock = TextBuilder.buildStringBlocks(PlaceHolderService.formatPlayer(player, uiRaw));

        board.renderSidebar(title.asComponent(), uiBlock);
    }
}
