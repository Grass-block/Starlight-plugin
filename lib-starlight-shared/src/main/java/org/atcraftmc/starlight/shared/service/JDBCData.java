package org.atcraftmc.starlight.shared.service;

import org.atcraftmc.starlight.data.jdbc.document.DocumentDataService;

public interface JDBCData  {
    DocumentDataService PLAYER_LOCAL = new DocumentDataService("sl_playerdata_local_v2");
    DocumentDataService PLAYER_SHARED = new DocumentDataService("sl_playerdata_shared_v2");
}
