package org.atcraftmc.starlight.display;

import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.starlight.api.PlayerFirstJoinEvent;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.atcraftmc.starlight.data.JDBCPlayerData;
import org.atcraftmc.starlight.data.jdbc.document.DocumentField;
import org.atcraftmc.starlight.framework.BukkitService;
import org.atcraftmc.starlight.shared.data.flex.TableColumn;
import org.atcraftmc.starlight.shared.service.JDBCData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@ApplicationService(id = "player-welcome")
public interface PlayerWelcomeService extends BukkitService {
    EventListener EVENT_LISTENER = new EventListener();

    @ServiceInject
    static void start() {
        BukkitUtil.registerEventListener(EVENT_LISTENER);
    }

    @ServiceInject
    static void stop() {
        BukkitUtil.unregisterEventListener(EVENT_LISTENER);
    }

    final class EventListener implements Listener {
        public static final TableColumn<Boolean> JOINED_L = TableColumn.bool("joined", false);
        public static final DocumentField<Number> JOINED = DocumentField.number("joined", -1);

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            var data = JDBCData.PLAYER_LOCAL.get(event.getPlayer().getUniqueId());

            if (JOINED.get(data).longValue() == -1L) {
                if (JOINED_L.exist(JDBCPlayerData.PLAYER_LOCAL) && JOINED_L.get(
                        JDBCPlayerData.PLAYER_LOCAL,
                        event.getPlayer().getUniqueId()
                )) {
                    JOINED.set(data, System.currentTimeMillis());
                } else {
                    BukkitUtil.callEvent(new PlayerFirstJoinEvent(event.getPlayer()));
                    JOINED.set(data, System.currentTimeMillis());
                }
            }
        }
    }
}
