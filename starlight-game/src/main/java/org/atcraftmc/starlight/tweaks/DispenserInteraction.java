package org.atcraftmc.starlight.tweaks;

import io.papermc.paper.event.block.BlockPreDispenseEvent;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.starlight.api.PluginMessages;
import org.atcraftmc.starlight.api.PluginStorage;
import org.atcraftmc.starlight.core.platform.BukkitDataAccess;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;

@ApplicationModule(id = "dispenser-interaction", version = "1.0.0", description = "Enhances dispenser interaction with custom behavior")
@AutoRegister(Registrations.SERVER_EVENT)
public final class DispenserInteraction extends BukkitAbstractModule {

    @Inject("tip")
    private LanguageItem tip;

    @Override
    public void checkCompatibility() {
        Compatibility.requireClass(() -> Class.forName("io.papermc.paper.event.block.BlockPreDispenseEvent"));
        Compatibility.requirePDC();
    }

    @Override
    public void enable() {
        PluginStorage.set(PluginMessages.CHAT_ANNOUNCE_TIP_PICK, (s) -> s.add(this.tip));
    }

    @Override
    public void disable() {
        PluginStorage.set(PluginMessages.CHAT_ANNOUNCE_TIP_PICK, (s) -> s.remove(this.tip));
    }

    @EventHandler
    public void dispenserPlaceBlock(BlockPreDispenseEvent event) {
        var block = event.getBlock();
        var type = event.getItemStack().getType();
        var data = BukkitDataAccess.blockData(block, Directional.class);
        var face = block.getRelative(data.getFacing());

        if (event.getItemStack().isEmpty() || event.getItemStack().getType().isAir()) {
            return;
        }

        if (type.isBlock() && face.getType().isAir()) {
            if (type == Material.TNT) {
                return;
            }

            var dispensed = BukkitDataAccess.blockState(block, Dispenser.class).getInventory().getItem(event.getSlot());

            if (dispensed != null) {
                dispensed.setAmount(dispensed.getAmount() - 1);
            }

            face.setBlockData(event.getItemStack().getType().createBlockData());
            event.setCancelled(true);

            return;
        }

        var is = event.getItemStack();
        var isItem = !(is.isEmpty() && is.getType().isAir() || is.getType().isBlock());

        if (face.getType().isBlock() && face.isValidTool(event.getItemStack()) && isItem) {
            event.setCancelled(true);
            face.breakNaturally(event.getItemStack(), true);
        }
    }
}
