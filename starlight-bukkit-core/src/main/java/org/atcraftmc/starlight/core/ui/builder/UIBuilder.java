package org.atcraftmc.starlight.core.ui.builder;

import org.atcraftmc.starlight.core.ui.AbstractUI;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public abstract class UIBuilder {
    Consumer<Player> initializer;

    static InventoryUIBuilder inventory(int slots) {
        return new InventoryUIBuilder(slots);
    }

    public abstract AbstractUI build();

    public UIBuilder initializer(Consumer<Player> initializer) {
        this.initializer = initializer;
        return this;
    }
}
