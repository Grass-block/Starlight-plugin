package org.atcraftmc.starlight.utilities;

import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.starlight.core.GameTestService;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.util.NBTExaminer;
import org.bukkit.Bukkit;

@ApplicationModule(id = "inventory-profile")
public class InventoryProfile extends BukkitAbstractModule {

    @Override
    public void enable() throws Exception {
        GameTestService.register("inv-serialize", () -> {
            var p = Bukkit.getPlayer("GrassBlock2022");
            var it = p.getInventory().getItemInMainHand();
            //NBTExaminer.send(p, item(it));
        });
    }
}
