package org.atcgroup.starlight.bundle.security;

import me.gb2022.apm.local.PluginMessenger;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.config.Configurations;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.migration.ConfigAccessor;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import java.util.*;

/**
 * <strong>**First module**</strong> completed by AI coding, Yay!
 *
 * @author GB2022, ChatGPT
 */
@AutoRegister(Registrations.SERVER_EVENT)
@ApplicationModule(id = "item-blocker", version = "1.2.2")
public final class ItemBlocker extends BukkitAbstractModule {
    private Map<String, ItemBlockerRule> rules = Map.of();

    @Inject("!starlight.security.item.bypass")
    private Permission permission;

    @Inject
    private LanguageEntry language;

    @Override
    public void enable() {
        var loaded = new LinkedHashMap<String, ItemBlockerRule>();

        for (var entry : deserializeRules(Configurations.standalone("item-blocker-rules")).entrySet()) {
            if (loaded.putIfAbsent(entry.getKey(), entry.getValue()) != null) {
                throw new IllegalArgumentException("Duplicate item blocker rule: " + entry.getKey());
            }
        }

        this.rules = Collections.unmodifiableMap(loaded);
    }

    @Override
    public void disable() {
        this.rules = Map.of();
    }

    public static Map<String, ItemBlockerRule> deserializeRules(ConfigurationSection section) {
        var rules = new LinkedHashMap<String, ItemBlockerRule>();
        for (var id : section.getKeys(false)) {
            var rule = Objects.requireNonNull(section.getConfigurationSection(id), "Invalid rule: " + id);
            rules.put(id, ItemBlockerRule.deserialize(id, rule));
        }
        return Collections.unmodifiableMap(rules);
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        this.checkEvent(event.getPlayer(), event.getItem(), Behavior.CONSUME, event);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        this.checkEvent(event.getPlayer(), event.getItem(), Behavior.INTERACT, event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        this.checkEvent(event.getPlayer(), event.getItemInHand(), Behavior.PLACE, event);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            this.checkEvent(player, player.getInventory().getItemInMainHand(), Behavior.ATTACK, event);
        }
    }

    private <E extends Event & Cancellable> void checkEvent(Player player, ItemStack stack, Behavior behavior, E event) {
        if (stack == null || stack.getType().isAir() || player.hasPermission(this.permission)) {
            return;
        }

        var itemId = stack.getType().getKey().toString();
        for (var entry : this.rules.entrySet()) {
            if (entry.getValue().filterId().contains(itemId)) {
                this.handleAction(new ActionCapture<>(entry.getKey(), entry.getValue(), player, stack, behavior, event));
            }
        }
    }

    private void handleAction(ActionCapture<?> capture) {
        if (capture.item == null) {
            return;
        }

        if (capture.player.hasPermission(this.permission)) {
            return;
        }

        var m = capture.item.getType();

        if (capture.rule.cancelAction) {
            capture.event.setCancelled(true);
        }

        if (capture.rule().removeItem) {
            capture.player.getInventory().removeItem(capture.item);
        }

        if (Objects.equals(capture.rule.level, "WARNING")) {
            MessageAccessor.send(this.language, capture.player(), "warning-item", m.getKey().toString());
            if (ConfigAccessor.getBool(this.config(), "broadcast")) {
                MessageAccessor.broadcast(this.language, true, false, "warning-item-broadcast", capture.player().getName(), m.getKey().toString());
            }
        }

        if (Objects.equals(capture.rule.level, "ILLEGAL")) {
            MessageAccessor.send(this.language, capture.player(), "warning-item", m.getKey().toString());
            if (ConfigAccessor.getBool(this.config(), "broadcast")) {
                MessageAccessor.broadcast(this.language, true, false, "illegal-item-broadcast", capture.player().getName(), m.getKey().toString());
            }
        }

        PluginMessenger.broadcastMapped("item:access", (map) -> map.put("player", capture.player.getName()).put("type", capture.rule.level).put("action", capture.behavior).put("item", m.getKey().getKey()));
    }

    public enum Behavior {
        HELD, CONSUME, INTERACT, PLACE, ATTACK
    }

    public record ActionCapture<E extends Event & Cancellable>(String ruleId, ItemBlockerRule rule, Player player,
                                                               ItemStack item, Behavior behavior, E event) {
    }

    public record ItemBlockerRule(String name, String level, boolean cancelAction, boolean removeItem,
                                  Set<String> filterId) {
        public ItemBlockerRule {
            filterId = Set.copyOf(filterId);
        }

        public static ItemBlockerRule deserialize(String id, ConfigurationSection section) {
            return new ItemBlockerRule(section.getString("name", id), section.getString("level", "WARNING"), section.getBoolean("cancel-action", false), section.getBoolean("remove-item", false), Set.copyOf(section.getStringList("filter-id")));
        }
    }
}
