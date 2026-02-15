package org.atcraftmc.starlight.utilities;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.command.QuarkCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.starlight.api.customization.CustomItem;
import org.atcraftmc.starlight.core.custom.CustomBlockService;
import org.atcraftmc.starlight.core.custom.CustomMeta;
import org.atcraftmc.starlight.foundation.command.CommandProvider;
import org.atcraftmc.starlight.foundation.command.ModuleCommand;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@ApplicationModule(id = "menu-item",description = "provides an item for accessing menu quicker")
@AutoRegister(Registrations.SERVER_EVENT)
@CommandProvider(MenuItem.ClaimMenuItemCommand.class)
public final class MenuItem extends BukkitAbstractModule {
    private final MenuCustomItem item = new MenuCustomItem();

    @Override
    public void enable() throws Exception {
        CustomBlockService.instance().registerItem(this.item);
    }

    @Override
    public void disable() throws Exception {
        CustomBlockService.instance().unregisterItem("menu_item");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (config().value("detect-join").bool()) {
            this.detectPlayerInventory(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (config().value("detect-respawn").bool()) {
            this.detectPlayerInventory(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (config().value("detect-drop").bool()) {
            this.detectPlayerInventory(event.getPlayer());
        }
    }

    public void performAction(Player player) {
        Bukkit.dispatchCommand(player, config().value("bind-command").string());
    }

    public void detectPlayerInventory(Player player) {
        var inv = player.getInventory();

        for (var stack : inv.getContents()) {
            if (stack == null) {
                continue;
            }

            if (Objects.equals(CustomMeta.getItemPDCIdentifier(stack), "menu_item")) {
                return;
            }
        }

        inv.addItem(this.item.createItem(1));
    }

    @QuarkCommand(name = "menu-item", permission = "+starlight.menu.item")
    public static final class ClaimMenuItemCommand extends ModuleCommand<MenuItem> {
        @Override
        public void execute(CommandExecution context) {


            var i = this.getModule().item.createItem(1);
            context.requireSenderAsPlayer().getInventory().addItem(i);
        }
    }

    private final class MenuCustomItem extends CustomItem {
        private MenuCustomItem() {
            super("menu_item", "menu_item");
        }

        @Override
        public LanguageItem getDisplayName(ItemStack stack) {
            return MenuItem.this.language().item("item-name");
        }

        @Override
        public LanguageItem getDescription(ItemStack stack) {
            return MenuItem.this.language().item("item-lore");
        }

        @Override
        public Material getActualBlock() {
            return Material.matchMaterial(config().value("item").string());
        }

        @Override
        public void onItemInteractAtBlock(Player player, ItemStack stack, Block target, Action action) {
            performAction(player);
        }

        @Override
        public void onItemInteractAtAir(Player player, ItemStack stack, Action action) {
            performAction(player);
        }
    }
}
