package org.atcraftmc.starlight.core.view;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.gb2022.modular.service.ApplicationService;
import me.gb2022.modular.service.Service;
import me.gb2022.modular.service.ServiceInject;
import org.atcraftmc.qlib.command.QuarkCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.starlight.foundation.command.CoreCommand;
import org.atcraftmc.starlight.shared.data.JDBCBasedDataService;
import org.atcraftmc.starlight.shared.service.JDBCService;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@ApplicationService(id="player-ui")
public interface PlayerUIService extends Service {
    SettingStorage STORAGE = new SettingStorage("SL_UI_SETTINGS");
    Map<UUID, PlayerView> INSTANCES = new HashMap<>();

    static PlayerView getInstance(final Player player) {
        return INSTANCES.computeIfAbsent(player.getUniqueId(), (k) -> new PlayerView(player));
    }

    static PlayerUISetting getSetting(UUID uuid) {
        return STORAGE.get(uuid);
    }

    static void saveSetting(UUID uuid, PlayerUISetting setting) {
        STORAGE.save(uuid, setting);
    }

    @ServiceInject
    static void start(){
        try {
            STORAGE.init(JDBCService.getDB(JDBCService.SL_SHARED).orElseThrow());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    final class SettingStorage extends JDBCBasedDataService<PlayerUISetting> {
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
                if (!JDBCService.isUniqueViolation(e)) {
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
            ps.setString(2, String.join("::", data.rejectChannels));
            ps.setString(3, String.join("::", data.rejectRenderers));
            ps.setBoolean(4, data.isRejectAllChannels());
            this.settingCache.invalidate(uuid);

            return ps.executeUpdate() > 0;
        }

        private boolean _update(UUID uuid, PlayerUISetting data) throws SQLException {
            var ps = this.connection.prepareStatement(
                    "UPDATE _ui_ SET channels_rejected=?, renderers_rejected=?, reject_all_channel=?where uuid = ?");
            ps.setString(1, String.join("::", data.rejectChannels));
            ps.setString(2, String.join("::", data.rejectRenderers));
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


    @QuarkCommand(name = "player-ui", permission = "+starlight.command.ui")
    final class PlayerUICommand extends CoreCommand {

        @Override
        public void execute(CommandExecution context) {
            var instance = getInstance(context.requireSenderAsPlayer());
            var uuid = context.requireSenderAsPlayer().getUniqueId();
            var setting = getSetting(uuid);

            switch (context.requireEnum(0, "renderer", "channel", "reject-all")) {
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
            saveSetting(uuid, setting);
        }

        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0,"renderer", "channel", "reject-all");
            suggestion.suggest(1, PlayerView.CHANNELS);
        }
    }
}
