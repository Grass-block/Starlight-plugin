package org.atcraftmc.starlight.shared.jdbc;

import org.atcraftmc.starlight.shared.jdbc.document.DocumentDataService;
import org.atcraftmc.starlight.shared.jdbc.flex.FlexibleMapService;

public interface JDBCData  {
    String SL_SHARED = "starlight:shared";
    String SL_LOCAL = "starlight:default";

    DocumentDataService PLAYER_LOCAL = new DocumentDataService("sl_playerdata_local_v2");
    DocumentDataService PLAYER_SHARED = new DocumentDataService("sl_playerdata_shared_v2");
    FlexibleMapService PLAYER_LOCAL_L = new FlexibleMapService("sl_playerdata_local");
    FlexibleMapService PLAYER_SHARED_L = new FlexibleMapService("sl_playerdata_shared");
}
