package org.atcraftmc.starlight.internal;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.gb2022.commons.TriState;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.Language;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.qlib.texts.TextBuilder;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.api.event.ModuleEvent;
import org.atcraftmc.starlight.api.event.PlayerViewInitEvent;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.TaskService;
import org.atcraftmc.starlight.core.ui.InventoryUI;
import org.atcraftmc.starlight.core.ui.TextRenderer;
import org.atcraftmc.starlight.core.ui.UI;
import org.atcraftmc.starlight.core.ui.providing.GUIProvider;
import org.atcraftmc.starlight.core.ui.view.InventoryUIView;
import org.atcraftmc.starlight.core.view.PlayerUIService;
import org.atcraftmc.starlight.core.view.PlayerUISetting;
import org.atcraftmc.starlight.core.view.PlayerView;
import org.atcraftmc.starlight.data.jdbc.JDBCUtil;
import org.atcraftmc.starlight.foundation.command.CommandProvider;
import org.atcraftmc.starlight.foundation.command.ModuleCommand;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.shared.data.JDBCBasedDataService;
import org.atcraftmc.starlight.shared.service.JDBCService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@ApplicationModule(id = "player-view-customization")
@AutoRegister(Registrations.SERVER_EVENT)
@CommandProvider(PlayerViewCustomization.PlayerUICommand.class)
public final class PlayerViewCustomization extends BukkitAbstractModule {
    private final SettingStorage storage = new SettingStorage("SL_UI_SETTINGS");
    private final Map<String, ViewSettingInfo> settings = new HashMap<>();
    private final SettingsUI settingsUI = new SettingsUI(this);

    @Inject
    private Logger logger;


    @EventHandler
    public void onModuleEnable(ModuleEvent.Enable event) {
        register(event.getMeta().getMetadata().key().fullId());
    }

    @EventHandler
    public void onModuleDisable(ModuleEvent.Disable event) {
        var id = event.getMeta().getMetadata().key().fullId();

        this.settings.remove(id);
    }

    private void register(String id) {
        var container = this.language().handle();
        var name_id = id + ":--view-setting-title";
        var desc_id = id + ":--view-setting-desc";

        if (!container.hasAny(name_id)) {
            return;
        }

        //System.out.println(name_id);

        var name = container.item(name_id);
        var desc = container.item(desc_id);

        if (!container.hasAny(desc_id)) {
            desc = this.language().item("default-desc");
        }

        this.settings.put(id, new ViewSettingInfo(id, name, desc, id));
    }

    @Override
    public void enable() throws Exception {
        var keys = SLPluginEnvironment.getContext().getModuleManager().getIdsByStatus(TriState.TRUE);

        for (var key : keys) {
            register(key);
        }
        this.logger.info("Auto-loaded {} view-settings.", keys.size());

        try {
            this.storage.init(JDBCService.getDB(JDBCService.SL_SHARED).orElseThrow());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PlayerUISetting getSetting(UUID uuid) {
        return this.storage.get(uuid);
    }

    public void saveSetting(UUID uuid, PlayerUISetting setting) {
        this.storage.save(uuid, setting);
    }

    @EventHandler
    public void onPlayerViewInit(PlayerViewInitEvent event) {
        event.setSetting(getSetting(event.getPlayer().getUniqueId()));
    }

    record ViewSettingInfo(String id, LanguageItem name, LanguageItem desc, String provider) {
    }

    @BukkitCommand(name = "player-ui", permission = "+starlight.command.ui")
    public static final class PlayerUICommand extends ModuleCommand<PlayerViewCustomization> {

        @Override
        public void execute(CommandExecution context) {
            var instance = PlayerUIService.getInstance(context.requireSenderAsPlayer());
            var uuid = context.requireSenderAsPlayer().getUniqueId();
            var setting = this.getModule().getSetting(uuid);

            switch (context.requireEnum(0, "renderer", "channel", "reject-all", "gui")) {
                case "gui" -> {
                    var page = context.hasArgumentAt(1) ? context.requireArgumentInteger(1) : 0;
                    getModule().settingsUI.open(context.requireSenderAsPlayer(), page);
                }
                case "channel" -> {
                    var channel = context.requireArgumentAt(1);
                    var b = setting.isChannelRejected(channel);

                    if (b) {
                        setting.unrejectChannel(channel);
                    } else {
                        setting.rejectChannel(channel);
                    }

                    //todo:msg
                }
                case "renderer" -> {
                    var renderer = context.requireArgumentAt(1);
                    var b = setting.isRendererRejected(renderer);
                    if (b) {
                        setting.unrejectRenderer(renderer);
                    } else {
                        setting.rejectRenderer(renderer);
                    }
                }
                case "reject-all" -> {
                    var b = setting.isRejectAllChannels();
                    setting.rejectAllChannels(!b);
                }
            }

            instance.sync(setting);
            instance.update();
            this.getModule().saveSetting(uuid, setting);
        }

        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, "renderer", "channel", "reject-all", "gui");
            suggestion.suggest(1, PlayerView.CHANNELS);
        }
    }

    private static final class SettingStorage extends JDBCBasedDataService<PlayerUISetting> {
        private final Cache<UUID, PlayerUISetting> settingCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.SECONDS).build();

        public SettingStorage(String table) {
            super(table);
        }

        @Override
        public String getTableNamePlaceholder() {
            return "_ui_";
        }

        @Override
        public PreparedStatement attemptCreateTable(Connection conn) throws SQLException {
            var sql = """
                    CREATE TABLE IF NOT EXISTS _ui_(
                        uuid char(36) PRIMARY KEY,
                        channels_rejected varchar(512) NOT NULL,
                        renderers_rejected varchar(512) NOT NULL,
                        reject_all_channel bool
                    );
                    """;

            return conn.prepareStatement(sql);
        }

        public boolean set(UUID uuid, PlayerUISetting setting) {
            this.settingCache.put(uuid, setting);
            try {
                return _add(uuid, setting);
            } catch (SQLException e) {
                if (!JDBCUtil.isUniqueViolation(e)) {
                    throw new RuntimeException(e);
                }

                try {
                    return _update(uuid, setting);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        private boolean _add(UUID uuid, PlayerUISetting data) throws SQLException {
            var ps = this.connection.prepareStatement(
                    "INSERT INTO _ui_ (uuid, channels_rejected,renderers_rejected,reject_all_channel) VALUES (?,?, ?, ?)");

            ps.setString(1, uuid.toString());
            ps.setString(2, String.join("::", data.getRejectChannels()));
            ps.setString(3, String.join("::", data.getRejectRenderers()));
            ps.setBoolean(4, data.isRejectAllChannels());
            this.settingCache.invalidate(uuid);

            return ps.executeUpdate() > 0;
        }

        private boolean _update(UUID uuid, PlayerUISetting data) throws SQLException {
            var ps = this.connection.prepareStatement(
                    "UPDATE _ui_ SET channels_rejected=?, renderers_rejected=?, reject_all_channel=?where uuid = ?");
            ps.setString(1, String.join("::", data.getRejectChannels()));
            ps.setString(2, String.join("::", data.getRejectRenderers()));
            ps.setBoolean(3, data.isRejectAllChannels());
            ps.setString(4, uuid.toString());
            this.settingCache.invalidate(uuid);

            return ps.executeUpdate() > 0;
        }

        public PlayerUISetting load(UUID uuid) {
            var sql = "SELECT * FROM _ui_ WHERE uuid = ?";

            try (var ps = this.connection.prepareStatement(sql)) {
                ps.setString(1, uuid.toString());

                var result = new PlayerUISetting();

                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return result;
                    }

                    for (var s : rs.getString("channels_rejected").split("::")) {
                        result.rejectChannel(s);
                    }
                    for (var s : rs.getString("renderers_rejected").split("::")) {
                        result.rejectRenderer(s);
                    }
                    result.rejectAllChannels(rs.getBoolean("reject_all_channel"));

                    return result;
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        public PlayerUISetting get(UUID player) {
            try {
                return settingCache.get(player, () -> load(player));
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        public void save(UUID uuid, PlayerUISetting setting) {
            this.settingCache.put(uuid, setting);
            set(uuid, setting);
        }
    }

    public final class SettingsUI implements GUIProvider<InventoryUI> {
        private final PlayerViewCustomization reference;

        public SettingsUI(PlayerViewCustomization reference) {
            this.reference = reference;
        }

        @Override
        public InventoryUI create() {
            return new InventoryUI(54, TextRenderer.literal(Component.text("__title")));
        }

        @Override
        public InventoryUIView initializeView(InventoryUI builder, Player viewer, Object... args) {
            var list = new ArrayList<>(this.reference.settings.keySet());
            list.sort(Comparator.naturalOrder());
            builder.title(TextRenderer.data(language().item("ui-title"), list.size() / 45));
            var view = builder.createInventoryUI(viewer);
            view.setCustomData("list", list);
            return view;
        }

        public void buildChannelButton(Player user, String id, UI.ElementBuilder builder, int page) {
            if (!this.reference.settings.containsKey(id)) {
                return;
            }

            var state = !getSetting(user.getUniqueId()).getRejectChannels().contains(id);
            var info = this.reference.settings.get(id);
            var locale = LocaleService.locale(user);

            builder.icon(UI.icon(Material.OAK_SIGN));
            builder.name(TextRenderer.data(info.name));

            var template = Language.generateTemplate(this.reference.config(), "ui-template", (s) -> s.replace(
                            "{state}",
                            state ? "open" : "close"
                    )
                    .replace("{id}", info.id())
                    .replace("{provider}", info.provider())
                    .replace("{desc}", info.desc.message(locale).render())
                    .replace("\n", "\n{#white}"));


            builder.lore(TextRenderer.literal(TextBuilder.buildComponent(this.reference.language().inline(template, locale))));
            builder.operation(UI.SOUND_CLICK);
            builder.operation(UI.command((p) -> "player-ui channel " + info.id()));
            builder.operation((v, player, action) -> TaskService.entity(player).delay(1, () -> v.setData(renderData(v, page))));
        }

        @Override
        public void render(InventoryUI builder, InventoryUIView view, Object... args) {
            var page = Integer.parseInt(args[0].toString());
            var list = ((List<String>) view.getCustomData("list", List.class));
            var pages = list.size() / 45;
            var base = page * 45;

            if (page > pages) {
                throw new RuntimeException("Page code out of range!");
            }

            for (var i = base; i < base + 45; i++) {
                if (i >= list.size()) {
                    break;
                }

                int finalI = i;
                UI.buildComponent(builder, i - base, (b) -> this.buildChannelButton(view.getViewer(), list.get(finalI), b, page));
            }

            //close button
            UI.buildComponent(builder, 53, (b) -> {
                b.icon(UI.icon(Material.REDSTONE));
                b.name(TextRenderer.data(Starlight.lang().item("common", "ui", "close")));
                b.operation(UI.SOUND_CLICK);
                b.operation(UI.close());
            });

            //page indicator
            UI.buildComponent(builder, 49, (b) -> {
                b.icon(UI.icon(Material.CLOCK, page + 1));
                b.name(TextRenderer.data(Starlight.lang().item("common", "ui", "page"), page + 1, pages + 1));
            });

            if (page != 0) {
                UI.builder()
                        .icon(UI.icon(Material.YELLOW_STAINED_GLASS_PANE))
                        .name(TextRenderer.data(Starlight.lang()
                                                        .item(
                                                                "common",
                                                                "ui",
                                                                "prev"
                                                        )))
                        .operation((v, player, action) -> v.setData(renderData(v, page - 1)))
                        .operation(UI.SOUND_CLICK)
                        .build(builder, 48);
            } else {
                UI.builder()
                        .icon(UI.icon(Material.GRAY_STAINED_GLASS_PANE))
                        .name(TextRenderer.data(Starlight.lang()
                                                        .item("common", "ui", "prev")))
                        .operation(UI.SOUND_DISABLE)
                        .build(builder, 48);
            }

            if (page != pages) {
                UI.builder()
                        .icon(UI.icon(Material.BLUE_STAINED_GLASS_PANE))
                        .name(TextRenderer.data(Starlight.lang()
                                                        .item("common", "ui", "next")))
                        .operation((v, player, action) -> v.setData(renderData(v, page + 1)))
                        .operation(UI.SOUND_CLICK)
                        .build(builder, 50);
            } else {
                UI.builder()
                        .icon(UI.icon(Material.GRAY_STAINED_GLASS_PANE))
                        .name(TextRenderer.data(Starlight.lang()
                                                        .item("common", "ui", "next")))
                        .operation(UI.SOUND_DISABLE)
                        .build(builder, 50);
            }
        }
    }
}
