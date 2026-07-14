package org.atcraftmc.starlight.tweaks;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationModule(id = "portable-shulker-box", version = "1.2", description = "Opens shulker boxes directly from the inventory")
@AutoRegister(Registrations.SERVER_EVENT)
public final class PortableShulkerBox extends BukkitAbstractModule {
    private final Map<UUID,Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void disable() {
        for (var p : Bukkit.getOnlinePlayers()) {
            this.close(p);
        }
    }

    public void open(Player player) {
        var inv = player.getInventory();

        if (!inv.getItemInMainHand().getType().getKey().getKey().endsWith("shulker_box")) {
            return;
        }

        var session = new Session(inv.getItemInMainHand(), player);
        this.sessions.put(player.getUniqueId(), session);
    }

    public void close(Player player){
        this.sync(player);
        this.sessions.remove(player.getUniqueId());
    }

    public void sync(Player player) {
        var uuid = player.getUniqueId();

        if (!this.sessions.containsKey(player.getUniqueId())) {
            return;
        }

        this.sessions.get(uuid).sync();
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        this.open(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        this.sync(player);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player player)) {
            return;
        }

        this.close(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.close(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        this.close(event.getEntity());
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.sessions.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }


    static final class Session {
        private final ItemStack item;
        private final BlockStateMeta meta;
        private final ShulkerBox state;

        public Session(ItemStack item, Player target) {
            this.item = item;
            this.meta = ((BlockStateMeta) item.getItemMeta());
            this.state = (ShulkerBox) meta.getBlockState();
            Inventory inventory = this.state.getInventory();
            target.openInventory(inventory);
        }

        public void sync() {
            this.meta.setBlockState(state);
            this.item.setItemMeta(this.meta);
        }
    }
}
