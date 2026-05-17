package org.atcraftmc.quark.lobby;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.starlight.framework.module.SLCommandModule;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.atcraftmc.starlight.config.Configurations;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@ApplicationModule(id = "lobby-default-inventory")
@BukkitCommand(name = "lobby-default-inventory", playerOnly = true)
@AutoRegister(Registrations.SERVER_EVENT)
public final class DefaultInventory extends SLCommandModule {
    public static final String TITLE = ChatColor.LIGHT_PURPLE + "Default Inventory Editor";
    Inventory inventory = Bukkit.createInventory(null, InventoryType.PLAYER, TITLE);


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        cover(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().isOp()) {
            return;
        }
        cover(event.getWhoClicked());
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemHarvest(PlayerAttemptPickupItemEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }
        event.setCancelled(true);
    }

    private void cover(HumanEntity player) {
        player.getInventory().setContents(this.inventory.getContents());
        player.getInventory().setHeldItemSlot(4);
    }

    @Override
    public void enable() throws Exception {
        super.enable();
        load();
    }

    @Override
    public void disable() throws Exception {
        super.disable();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() != this.inventory) {
            return;
        }

        this.save();

        MessageAccessor.send(this.language(), event.getPlayer(), "edit-complete");
    }

    private void load() {
        var conf = Configurations.standalone("lobby-default-inventory");

        this.inventory.clear();

        for (var k : conf.getKeys(false)) {
            var i = Integer.parseInt(k);

            this.inventory.setItem(i, conf.getItemStack(k));
        }
    }

    private void save() {
        var conf = Configurations.standalone("lobby-default-inventory");
        var index = 0;

        for (var k : conf.getKeys(false)) {
            conf.set(k, null);
        }

        for (ItemStack stack : this.inventory.getContents()) {
            if (stack == null) {
                index++;
                continue;
            }

            conf.set(String.valueOf(index), stack);
            index++;
        }

        Configurations.saveStandalone(conf);
    }

    @Override
    public void suggest(CommandSuggestion suggestion) {
        suggestion.suggest(0, "cover", "edit", "reload");
    }

    @Override
    public void execute(CommandExecution context) {
        var sender = context.getSender();

        switch (context.requireEnum(0, "cover", "edit", "reload")) {
            case "edit" -> {
                context.requireSenderAsPlayer().openInventory(this.inventory);
                MessageAccessor.send(this.language(), sender, "edit-start");
            }
            case "cover" -> {
                cover(context.requireSenderAsPlayer());
                MessageAccessor.send(this.language(), sender, "cover");
            }
            case "reload" -> {
                load();
                MessageAccessor.send(this.language(), sender, "reload");
            }
        }
    }
}
