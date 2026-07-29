package org.atcraftmc.starlight.tweaks;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import net.kyori.adventure.text.Component;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@ApplicationModule(id = "quick-deposit", description = "Left-click a chest to deposit held item")
@AutoRegister(Registrations.SERVER_EVENT)
public final class QuickDeposit extends BukkitAbstractModule {

    private static int countItem(Inventory inv, Material type) {
        return Arrays.stream(inv.getContents())
                .filter(i -> i != null && i.getType() == type)
                .mapToInt(ItemStack::getAmount)
                .sum();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        var block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            return;
        }

        var type = block.getType();
        Inventory target;

        if (type == Material.CHEST || type == Material.TRAPPED_CHEST) {
            var state = block.getState();
            if (!(state instanceof Chest chest)) {
                return;
            }
            target = chest.getInventory();
        } else if (type == Material.ENDER_CHEST) {
            target = player.getEnderChest();
        } else {
            return;
        }

        event.setCancelled(true);
        deposit(player, item, target);
    }

    private void deposit(Player player, ItemStack hand, Inventory target) {
        var original = hand.getAmount();
        var type = hand.getType();
        var key = type.getKey();
        var name = (type.isBlock()?"block.":"item.") + key.getNamespace() + "." + key.getKey();
        var remaining = target.addItem(hand);

        int deposited;
        if (remaining.isEmpty()) {
            deposited = original;
            hand.setAmount(0);
        } else {
            var left = remaining.get(0).getAmount();
            deposited = original - left;
            hand.setAmount(left);
        }

        var total = countItem(target, type);

        language().item("deposit-info").send(QLib.audience(player), deposited, name, total);
    }
}
