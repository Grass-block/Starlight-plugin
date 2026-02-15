package org.atcraftmc.starlight.core.view;

import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.Service;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationService(id = "player-ui")
public interface PlayerUIService extends Service {
    Map<UUID, PlayerView> INSTANCES = new HashMap<>();

    static PlayerView getInstance(final Player player) {
        return INSTANCES.computeIfAbsent(player.getUniqueId(), (k) -> new PlayerView(player));
    }
}
