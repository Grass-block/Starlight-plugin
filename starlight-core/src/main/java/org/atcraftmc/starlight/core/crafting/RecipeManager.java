package org.atcraftmc.starlight.core.crafting;

import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.core.platform.APIProfileTest;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.inventory.Recipe;

public interface RecipeManager {
    static void register(Recipe... recipes) {
        QLib.task().global().run(() -> {
            for (Recipe r : recipes) {
                if (Bukkit.getRecipe(((Keyed) r).getKey()) != null) {
                    continue;
                }
                try {
                    Bukkit.addRecipe(r);
                } catch (IllegalArgumentException e) {
                    if (APIProfileTest.isMixedServer()) {
                        continue;
                    }

                    throw e;
                }
            }
        });
    }

    static void unregister(Recipe... recipes) {
        if (APIProfileTest.isMixedServer()) {
            return;
        }

        QLib.task().global().run(() -> {
            for (Recipe r : recipes) {
                Bukkit.removeRecipe(((Keyed) r).getKey());
            }
        });
    }
}
