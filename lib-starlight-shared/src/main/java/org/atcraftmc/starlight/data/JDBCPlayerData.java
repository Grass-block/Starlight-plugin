package org.atcraftmc.starlight.data;

import org.atcraftmc.starlight.shared.data.flex.FlexibleMapService;

public interface JDBCPlayerData {
    FlexibleMapService PLAYER_LOCAL = new FlexibleMapService("sl_playerdata_local");
    FlexibleMapService PLAYER_SHARED = new FlexibleMapService("sl_playerdata_shared");
}
