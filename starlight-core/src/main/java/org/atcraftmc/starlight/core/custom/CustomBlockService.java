package org.atcraftmc.starlight.core.custom;

import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.qlib.command.AbstractCommand;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.api.customization.CustomBlock;
import org.atcraftmc.starlight.api.customization.CustomItem;
import org.atcraftmc.starlight.api.event.ClientLocaleChangeEvent;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.TaskService;
import org.atcraftmc.starlight.foundation.platform.BukkitUtil;
import org.atcraftmc.starlight.framework.BukkitService;
import org.bukkit.Bukkit;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.persistence.PersistentDataHolder;

import java.util.HashMap;
import java.util.Map;

@ApplicationService(id = "custom-block", impl = CustomBlockService.class, export = true)
public final class CustomBlockService implements BukkitService {

    @ServiceInject
    public static final ServiceHolder<CustomBlockService> HOLDER = new ServiceHolder<>();
    private final Map<String, CustomBlock> blocks = new HashMap<>();
    private final Map<String, CustomItem> items = new HashMap<>();
    private final CustomBlockCommand command = new CustomBlockCommand();

    public static CustomBlockService instance() {
        return HOLDER.get();
    }

    public void registerBlock(CustomBlock block) {
        this.blocks.put(block.getId(), block);
        this.items.put(block.getId(), block);
    }

    public void unregisterBlock(String k) {
        this.blocks.remove(k);
        this.items.remove(k);
    }

    public void registerItem(CustomItem item) {
        this.items.put(item.getId(), item);
    }

    public void unregisterItem(String id) {
        this.items.remove(id);
    }

    @Override
    public void enable() {
        //todo: possible offline-mount name issue
        BukkitUtil.registerEventListener(this);
        Starlight.instance().getCommandManager().register(this.command);

        for (var player : Bukkit.getOnlinePlayers()) {
            refreshInventory(player, 1);
        }
    }

    @Override
    public void disable() {
        BukkitUtil.unregisterEventListener(this);
        Starlight.instance().getCommandManager().unregister(this.command);
    }

    @EventHandler
    public void onClientLocaleChange(ClientLocaleChangeEvent event) {
        refreshInventory(event.getPlayer(), 1);
    }

    @EventHandler
    public void onPlayerPickItem(PlayerHarvestBlockEvent event) {
        for (var stack : event.getItemsHarvested()) {
            if (!CustomMeta.hasItemPDCIdentifier(stack)) {
                continue;
            }

            var id = CustomMeta.getItemPDCIdentifier(stack);

            if (!this.items.containsKey(id)) {
                continue;
            }

            var block = this.items.get(id);
            block.render(stack, LocaleService.locale(event.getPlayer()));
            block.onItemPick(event.getPlayer(), stack);
        }
    }

    @EventHandler
    public void onPlayerPickItem(PlayerPickupItemEvent event) {
        var stack = event.getItem().getItemStack();
        if (!CustomMeta.hasItemPDCIdentifier(stack)) {
            return;
        }

        var id = CustomMeta.getItemPDCIdentifier(stack);

        if (!this.items.containsKey(id)) {
            return;
        }

        var block = this.items.get(id);
        block.render(stack, LocaleService.locale(event.getPlayer()));
        block.onItemPick(event.getPlayer(), stack);

        event.getItem().setItemStack(stack);
    }

    public void refreshInventory(Player player, int delay) {
        TaskService.entity(player).delay(delay, () -> {
            for (var i : player.getInventory().getContents()) {
                if (i == null) {
                    continue;
                }

                if (!CustomMeta.hasItemPDCIdentifier(i)) {
                    continue;
                }

                var id2 = CustomMeta.getItemPDCIdentifier(i);

                if (!this.items.containsKey(id2)) {
                    continue;
                }

                var block2 = this.items.get(id2);
                block2.render(i, LocaleService.locale(i));
            }
        });
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }

        if (!CustomMeta.hasItemPDCIdentifier(event.getItem())) {
            return;
        }

        var id = CustomMeta.getItemPDCIdentifier(event.getItem());

        if (!this.items.containsKey(id)) {
            return;
        }

        var block = this.items.get(id);
        block.render(event.getItem(), LocaleService.locale(event.getPlayer()));

        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            block.onItemInteractAtBlock(event.getPlayer(), event.getItem(), event.getClickedBlock(), event.getAction());
            return;
        }

        block.onItemInteractAtAir(event.getPlayer(), event.getItem(), event.getAction());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var block = event.getBlock();

        if (!(block.getState() instanceof PersistentDataHolder state)) {
            return;
        }

        String id = null;

        if (CustomMeta.hasPDCIdentifier(state)) {
            id = CustomMeta.getPDCIdentifier(state);
        }

        if (CustomMeta.getPDCLegacyIdentifier(state) != null) {
            id = CustomMeta.getPDCLegacyIdentifier(state);
        }

        if (!this.blocks.containsKey(id)) {
            return;
        }

        event.setDropItems(false);
        var b = this.blocks.get(id);
        var result = b.onBlockBreak(event.getPlayer(), block);

        if (result == null) {
            return;
        }

        BukkitUtil.createDrop(block.getLocation(), result);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        var item = event.getItemInHand();

        if (!CustomMeta.hasItemPDCIdentifier(item)) {
            return;
        }

        var id = CustomMeta.getItemPDCIdentifier(item);

        if (!this.blocks.containsKey(id)) {
            return;
        }

        var block = this.blocks.get(id);

        var b = event.getBlockPlaced().getState();
        CustomMeta.setPDCIdentifier((TileState) b, block.getId());
        b.update();

        block.onPlaced(event.getPlayer(), event.getBlockPlaced(), item);
    }


    @BukkitCommand(name = "custom-item", op = true, playerOnly = true)
    public final class CustomBlockCommand extends AbstractCommand {

        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, items.keySet());
            suggestion.suggest(1, "1", "16", "64");
        }

        @Override
        public void execute(CommandExecution context) {
            var lang = Starlight.lang().entry("starlight-core:custom-block");

            var id = context.requireArgumentAt(0);
            var amount = context.requireArgumentInteger(1);

            var block = items.get(id);

            if (block == null) {
                lang.item("give-failed").send(context.getSender(), id);
                return;
            }

            context.requireSenderAsPlayer().getInventory().addItem(block.createItem(amount));
            lang.item("give").send(context.getSender(), id);
        }
    }
}
