package org.atcraftmc.starlight.core.ui;

import org.atcraftmc.starlight.core.ui.view.InventoryUIView;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public interface IconRenderer extends Function<InventoryUIView, ItemStack> {
    static IconRenderer fixed(ItemStack stack) {
        return (v) -> stack;
    }
}
