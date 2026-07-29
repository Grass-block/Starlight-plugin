package org.atcraftmc.starlight.display;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.commons.reflect.method.MethodHandle;
import me.gb2022.commons.reflect.method.MethodHandleO1;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import me.gb2022.gluon.module.component.ComponentProvider;
import net.kyori.adventure.text.Component;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.language.Language;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.qlib.texts.TextBuilder;
import org.atcraftmc.qlib.texts.placeholder.StringObjectPlaceHolder;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.atcraftmc.starlight.core.ComponentSerializer;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.VisualScoreboardService;
import org.atcraftmc.starlight.core.placeholder.PlaceHolderService;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.core.view.ScoreboardTrackingStateCallback;
import org.atcraftmc.starlight.framework.module.SLCommandModule;
import org.atcraftmc.starlight.framework.module.SLModuleComponent;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.atcraftmc.starlight.shared.jdbc.JDBCData;
import org.atcraftmc.starlight.shared.jdbc.document.DocumentField;
import org.atcraftmc.starlight.shared.jdbc.flex.TableColumn;
import org.atcraftmc.starlight.util.CachedInfo;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.*;

@SuppressWarnings("deprecation")
@AutoRegister({Registrations.SERVER_EVENT, VisualScoreboardService.TRACKING})
@ApplicationModule(id = "player-name-header", description = "Provide a Header before player's name.")
@BukkitCommand(name = "header", permission = "-starlight.name.header")
@ComponentProvider({PlayerNameHeader.BelowNameColumns.class})
public final class PlayerNameHeader extends SLCommandModule implements ScoreboardTrackingStateCallback {
    public static final TableColumn<String> PLAYER_HEADER_L = TableColumn.string("name_header", 16, "unset");
    public static final DocumentField<String> PLAYER_HEADER = DocumentField.string("name-header", "unset");

    private final Map<UUID, String> cache = new HashMap<>();

    MethodHandleO1<Player, Component> SET_NAME_HEADER = MethodHandle.select(ctx -> {
        ctx.attempt(() -> Player.class.getMethod("playerListName", Component.class), (p, c) -> {
            p.playerListName(c);
            p.customName(c);
            p.displayName(c);
        });
        ctx.dummy((p, c) -> {
            var cc = ComponentSerializer.legacy(c);
            p.setPlayerListName(cc);
            p.setCustomName(cc);
            p.setDisplayName(cc);
        });
    });

    @Inject
    private LanguageEntry language;

    @Override
    public void enable() throws Exception {
        super.enable();

        for (var p : Bukkit.getOnlinePlayers()) {
            this.attach(p);
        }

        PlaceHolderService.PLAYER.register("rank", (StringObjectPlaceHolder<Player>) this::getHeader);
    }

    @Override
    public void disable() throws Exception {
        super.disable();
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.detach(p);
        }

        PlaceHolderService.PLAYER.unregister("rank");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        var p = Bukkit.getPlayerExact(args[1]);
        var o = Bukkit.getOfflinePlayer(args[1]);

        if (Objects.equals(args[0], "set")) {
            PLAYER_HEADER.set(JDBCData.PLAYER_SHARED, Bukkit.getOfflinePlayer(args[1]).getUniqueId(), args[2]);
            MessageAccessor.send(this.language, sender, "set-header", args[1], args[2]);
        }
        if (Objects.equals(args[0], "clear")) {
            PLAYER_HEADER.set(JDBCData.PLAYER_SHARED, o.getUniqueId(), "unset");
            MessageAccessor.send(this.language, sender, "clear-header", args[1]);
        }

        this.cache.remove(o.getUniqueId());

        if (p != null && p.isOnline()) {
            this.attach(p);
        }
    }

    @Override
    public void onCommandTab(CommandSender sender, String[] buffer, List<String> tabList) {
        if (buffer.length == 1) {
            tabList.add("set");
            tabList.add("clear");
        }
        if (buffer.length == 2) {
            tabList.addAll(CachedInfo.getAllPlayerNames());
        }
        if (buffer.length == 3 && Objects.equals(buffer[0], "set")) {
            tabList.add("<header>");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.attach(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        this.detach(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        this.attach(event.getPlayer());
    }

    @Override
    public void mount(Player player, VisualScoreboardService.VisualScoreboard scoreboard) {
        for (var target : Bukkit.getOnlinePlayers()) {
            var prefix = getPlayerPrefix(target);
            var postfix = getPlayerSuffix(target);

            scoreboard.setNameTag(target, prefix, postfix);
        }
    }

    public void attach(Player p) {
        var name = getPlayerName(p);
        SET_NAME_HEADER.invoke(p, name);
    }

    public void detach(Player p) {
        p.setDisplayName(p.getName());
        p.setPlayerListName(p.getName());
    }

    public String getHeader(Player player) {
        var uuid = player.getUniqueId();

        if (this.cache.containsKey(uuid)) {
            return this.cache.get(uuid);
        }

        var data = JDBCData.PLAYER_SHARED.get(uuid);

        if (!PLAYER_HEADER.exist(data) && PLAYER_HEADER_L.exist(JDBCData.PLAYER_SHARED_L)) {
            var old = PLAYER_HEADER_L.get(JDBCData.PLAYER_SHARED_L, uuid);
            PLAYER_HEADER.set(data, old);
        }

        var header = PLAYER_HEADER.get(data);

        if (!Objects.equals(header, "unset")) {
            this.cache.put(uuid, header);
            return header;
        } else {
            if (player.isOp()) {
                var h = config().value("op-header").string();
                this.cache.put(uuid, h);
                return h;
            } else {
                var h = config().value("player-header").string();
                this.cache.put(uuid, h);
                return h;
            }
        }
    }

    public Component getPlayerName(Player player) {
        String header = getHeader(player);
        String template = this.config().value("template").string();
        if (template == null) {
            return Component.text(player.getName());
        }
        return QLib.textBuilder().buildComponent(PlaceHolderService.format(template.replace("{player}", player.getName())
                                                                                   .replace(
                                                                                           "{header}",
                                                                                           header + TextBuilder.EMPTY_COMPONENT
                                                                                   )));
    }

    public Component getPlayerSuffix(Player player) {
        var header = getHeader(player);
        var template = Objects.requireNonNull(this.config().value("template").string()).split("\\{player}");
        if (template.length == 1) {
            return Component.text("");
        }
        return QLib.textBuilder().buildComponent(PlaceHolderService.format(template[template.length - 1].replace("{header}", header)));
    }

    public Component getPlayerPrefix(Player player) {
        var header = getHeader(player);
        var template = Objects.requireNonNull(this.config().value("template").string()).split("\\{player}");
        return QLib.textBuilder().buildComponent(PlaceHolderService.format(template[0].replace("{header}", header)));
    }


    public static final class BelowNameColumns extends SLModuleComponent<PlayerNameHeader> {
        @Override
        public void checkCompatibility() throws APIIncompatibleException {
            Compatibility.requireClass(() -> Class.forName("io.papermc.paper.scoreboard.numbers.NumberFormat"));
            Compatibility.assertion(StarlightBukkitCore.instance()
                                            .config()
                                            .value("quark-display:player-name-header:below-name-enable")
                                            .bool());
        }

        public Component build(Player player, MinecraftLocale locale) {
            String template = Language.generateTemplate(this.parent.config(), "below-name");

            String ui = MessageAccessor.buildTemplate(this.parent.language(), locale, template);
            ui = PlaceHolderService.formatPlayer(player, ui);

            return QLib.textBuilder().buildComponent(ui);
        }

        @Override
        public void enable() {
            QLib.task().global().timer("render-below-name", 0, 20, this::render);
        }

        @Override
        public void disable() {
            QLib.task().global().cancel("render-below-name");
        }

        public void render() {
            for (Player view : Bukkit.getOnlinePlayers()) {
                var scoreboard = ((VisualScoreboardService.BukkitVisualScoreboard) VisualScoreboardService.instance()
                        .visualScoreboard(view)).getScoreboard();

                var obj = scoreboard.getObjective("below-name");

                if (obj == null) {
                    obj = scoreboard.registerNewObjective("below-name", "@quark");
                }

                obj.setDisplaySlot(DisplaySlot.BELOW_NAME);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    obj.displayName(build(player, LocaleService.locale(view)));
                    obj.getScore(player).numberFormat(NumberFormat.fixed(build(player, LocaleService.locale(view))));
                }
            }
        }
    }
}
