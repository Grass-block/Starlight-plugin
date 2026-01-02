package org.atcraftmc.starlight.api.customization;

import org.atcraftmc.starlight.core.custom.CustomMeta;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;
import org.jetbrains.annotations.NotNull;

public abstract class CustomBlock extends CustomItem {
    protected CustomBlock(String id, String legacyId) {
        super(id, legacyId);
    }

    public void onPlaced(Player player, Block blockPlaced, ItemStack item) {
    }

    public ItemStack onBlockBreak(@NotNull Player player, Block state) {
        return createItem(1);
    }

    public boolean test(Block b) {
        if (!(b.getState() instanceof PersistentDataHolder state)) {
            return false;
        }

        if (this.legacyId.equals(CustomMeta.getPDCLegacyIdentifier(state))) {
            return true;
        }

        if (!CustomMeta.hasPDCIdentifier(state)) {
            return false;
        }

        return CustomMeta.getPDCIdentifier(state).equals(this.id);
    }
}
