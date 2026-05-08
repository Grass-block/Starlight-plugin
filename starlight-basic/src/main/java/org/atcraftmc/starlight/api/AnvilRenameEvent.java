package org.atcraftmc.starlight.api;

import org.atcraftmc.starlight.api.event.CustomEvent;
import org.atcraftmc.starlight.api.event.BukkitEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.AnvilInventory;

@BukkitEvent(async = false)
public final class AnvilRenameEvent extends CustomEvent {
    private final AnvilInventory inventory;
    private final String content;

    private String outcome;

    public AnvilRenameEvent(AnvilInventory inventory, String content) {
        this.inventory = inventory;
        this.content = content;
        this.outcome = content;
    }

    public static HandlerList getHandlerList() {
        return getHandlerList(AnvilRenameEvent.class);
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getContent() {
        return content;
    }

    public AnvilInventory getInventory() {
        return inventory;
    }

    public String getOutcome() {
        return outcome;
    }
}
