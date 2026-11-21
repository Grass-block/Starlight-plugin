package org.atcraftmc.starlight.core;

import me.gb2022.gluon.service.ServiceInject;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public interface GameTestService {
    Map<String, Runnable> TESTS = new HashMap<>();

    static void register(String name, Runnable runnable) {
        TESTS.put(name, runnable);
    }

    static void unregister(String name) {
        TESTS.remove(name);
    }

    static void run(String name) {
        TESTS.get(name).run();
    }


    @ServiceInject
    static void start() {
        register("recipe-scan", () -> {
            for (var a : Bukkit.getRecipesFor(new ItemStack(Material.ACACIA_PLANKS))) {
                if (a instanceof Keyed keyed) {
                    System.out.println(keyed.getKey().asString());
                }
                System.out.println(a.getResult());
            }
        });
    }

    @ServiceInject
    static void stop() {


    }
}
