package org.atcraftmc.starlight.core.ui.callback;

import org.atcraftmc.starlight.core.ui.inventory.InventoryUI;
import org.bukkit.entity.Player;

public interface InventoryActionListener {
    void invoke(Player p, InventoryUI.InventoryUIInstance ui, int x, int y);
}
