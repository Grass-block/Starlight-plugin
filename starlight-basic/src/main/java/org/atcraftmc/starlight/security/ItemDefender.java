package org.atcraftmc.starlight.security;

import me.gb2022.apm.local.PluginMessenger;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.SharedObjects;
import org.atcraftmc.starlight.data.record.RecordEntry;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.migration.ConfigAccessor;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import java.util.Date;
import java.util.List;

@AutoRegister(Registrations.SERVER_EVENT)
@ApplicationModule(id = "item-defender", version = "1.2.2")
public class ItemDefender extends BukkitAbstractModule {

    @Inject
    private LanguageEntry language;

    @Inject("item-defender;Time,Level,Player,World,X,Y,Z,Type,Action")
    private RecordEntry record;

    @Inject("!starlight.security.item.bypass")
    private Permission permission;

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event) {
        this.checkEvent(event.getPlayer(), event.getPlayer().getInventory().getItem(event.getNewSlot()), true, "Held");
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        this.checkEvent(event.getPlayer(), event.getItem(), true, "Consume");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        this.checkEvent(event.getPlayer(), event.getItem(), false, "Interact");
    }

    private void checkEvent(Player p, ItemStack stack, boolean say, String action) {
        if (stack == null) {
            return;
        }

        if (p.hasPermission(this.permission)) {
            return;
        }

        var m = stack.getType();
        var itemIllegal = this.isItemIllegal(m);
        var b2 = this.isItemWarning(m);

        if (!(itemIllegal || b2)) {
            return;
        }

        if (itemIllegal) {
            p.getInventory().remove(m);
        }

        if (itemIllegal && b2) {
            b2 = false;
        }

        if (say) {
            if (itemIllegal) {
                MessageAccessor.send(this.language, p, "illegal-item", m.getKey().toString());
            } else {
                MessageAccessor.send(this.language, p, "warning-item", m.getKey().toString());
            }
        }

        PluginMessenger.broadcastMapped("item:access", (map) -> map
                .put("player", p.getName())
                .put("type", itemIllegal ? "illegal" : "warning")
                .put("item", m.getKey().getKey()));

        if (ConfigAccessor.getBool(this.config(), "broadcast")) {
            if (itemIllegal) {
                MessageAccessor.broadcast(this.language, true, false, "illegal-item-broadcast", p.getName(), m.getKey().toString());
            } else {
                MessageAccessor.broadcast(this.language, true, false, "warning-item-broadcast", p.getName(), m.getKey().toString());
            }
        }
    }

    private boolean isItemIllegal(Material material) {
        List<String> list = ConfigAccessor.configList(config(), "illegal-list", String.class);
        return list.contains(material.getKey().getKey());
    }

    private boolean isItemWarning(Material material) {
        List<String> list = ConfigAccessor.configList(config(), "warning-list", String.class);
        return list.contains(material.getKey().getKey());
    }
}