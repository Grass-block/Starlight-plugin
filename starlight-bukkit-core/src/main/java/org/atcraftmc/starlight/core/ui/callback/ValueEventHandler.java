package org.atcraftmc.starlight.core.ui.callback;

import org.atcraftmc.starlight.core.ui.UIInstance;
import org.bukkit.entity.Player;

public interface ValueEventHandler {
    void invoke(Player player, UIInstance ui, int value);
}
