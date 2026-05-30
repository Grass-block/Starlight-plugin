package org.atcraftmc.starlight.utilities;

import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.starlight.core.GameTestService;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.util.NBTExaminer;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@ApplicationModule(id = "inventory-profile", description = "Loads and examines player inventory NBT data")
public class InventoryProfile extends BukkitAbstractModule {

    public static File getPlayerDataFolder() {
        World overworld = Bukkit.getWorlds()
                .stream()
                .filter(w -> w.getEnvironment() == World.Environment.NORMAL)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No overworld found"));

        return new File(overworld.getWorldFolder(), "playerdata");
    }

    public static NBTTagCompound loadPlayerNBT(UUID uuid) {
        try {
            File playerDataFolder = getPlayerDataFolder();
            File datFile = new File(playerDataFolder, uuid.toString() + ".dat");

            if (!datFile.exists()) {
                System.out.println(datFile.getAbsolutePath());
                return null;
            }

            try (FileInputStream fis = new FileInputStream(datFile)) {
                return (NBTTagCompound) NBT.readZipped(fis);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void enable() throws Exception {
        GameTestService.register("inv-serialize", () -> {
            var p = Bukkit.getPlayer("GrassBlock2022");

            p.saveData();
            NBTExaminer.send(Bukkit.getConsoleSender(), loadPlayerNBT(p.getUniqueId()));
        });
    }
}
