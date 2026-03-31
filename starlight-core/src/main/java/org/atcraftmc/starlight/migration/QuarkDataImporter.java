package org.atcraftmc.starlight.migration;

import me.gb2022.commons.container.Pair;
import me.gb2022.commons.math.SHA;
import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.util.Identifiers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface QuarkDataImporter {
    Logger LOGGER = SLPluginEnvironment.createLogger("DataImporter");

    UUID IMPORT_DATA_IDENTITY = UUID.fromString("33550336-0000-0000-0000-000000000000");

    String WAYPOINT_DATA = "69b28eed70bbeb9fb913f2858a90782526416bde";
    String DEFAULT_INVENTORY = "75989ff307a019e933943b0f04f9923f7559aa41";
    String CUSTOM_LOG_FORMAT = "35cab68c71e61b6a802c12d8d559ade929c2c028";

    String PROTECTION_AREA = "173d7900baec575e2a557825d667ceba7f00d793";
    String EXPLOSION_WHITELIST = "81dcc2e7eea9fbd8ca2fc861decf117a077ce255";
    String POTATO_WAR = "8f81d78722151a0001a6d1cd9b2b3615b5c98f26";

    Map<String, BiConsumer<UUID, NBTTagCompound>> PLAYER_DATA_HANDLERS = new HashMap<>();
    Map<String, Pair<String, Consumer<NBTTagCompound>>> MODULE_DATA_HANDLERS = new HashMap<>();
    Map<String, Consumer<String>> CUSTOM_ACTIONS = new HashMap<>();

    static Map<String, Set<NBTTagCompound>> iteratePlayerData() {
        var result = new HashMap<String, Set<NBTTagCompound>>();
        var folder1 = new File(System.getProperty("user.dir") + "/plugins/quark/data/player");
        var folder2 = new File(System.getProperty("user.dir") + "/plugins/Quark/data/player");//this is because a fucking update

        LOGGER.info("importing data: {},{}", folder1, folder2);

        for (var player : Bukkit.getOfflinePlayers()) {
            LOGGER.info("importing player: {}", player.getName());
            appendPlayerData(player, folder1, folder2, result);
        }

        for (var player : Bukkit.getOnlinePlayers()) {
            LOGGER.info("importing player: {}", player.getName());
            appendPlayerData(player, folder1, folder2, result);
        }

        return result;
    }

    static void appendPlayerData(OfflinePlayer player, File folder1, File folder2, HashMap<String, Set<NBTTagCompound>> result) {
        var uuid = player.getUniqueId().toString();

        var uid = SHA.getSHA1(Identifiers.internal(uuid), false);
        var nid = SHA.getSHA1(Objects.requireNonNull(player.getName()), false);

        var list = result.computeIfAbsent(uuid, k -> new HashSet<>());

        var f11 = new File(folder1.getAbsolutePath() + "/" + uid);
        var f12 = new File(folder1.getAbsolutePath() + "/" + nid);
        var f21 = new File(folder2.getAbsolutePath() + "/" + uid);
        var f22 = new File(folder2.getAbsolutePath() + "/" + nid);

        if (f11.exists() && f11.length() > 0) {
            LOGGER.info("found UID playerData of {}", uuid);
            list.add(readAsNBT(f11));
        }
        if (f12.exists() && f12.length() > 0) {
            LOGGER.info("found NID playerData of {}", uuid);
            list.add(readAsNBT(f12));
        }
        if (f21.exists() && f21.length() > 0) {
            LOGGER.info("found UID playerData of {}", uuid);
            list.add(readAsNBT(f21));
        }
        if (f22.exists() && f22.length() > 0) {
            LOGGER.info("found NID playerData of {}", uuid);
            list.add(readAsNBT(f22));
        }
    }

    private static NBTTagCompound readAsNBT(File file) {
        try (var in = new FileInputStream(file)) {
            return (NBTTagCompound) NBT.readZipped(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void registerPlayerDataHandler(String id, BiConsumer<UUID, NBTTagCompound> handler) {
        PLAYER_DATA_HANDLERS.put(id, handler);
    }

    static void registerModuleDataHandler(String id, String mapped, Consumer<NBTTagCompound> handler) {
        MODULE_DATA_HANDLERS.put(id, new Pair<>(mapped, handler));
    }

    static void registerCustomDataHandler(String s, Consumer<String> handler) {
        CUSTOM_ACTIONS.put(s, handler);
    }

    static void runDataUpdater(String id) {
        if (MODULE_DATA_HANDLERS.containsKey(id)) {
            LOGGER.info("running module data importer: {}", id);
            runModuleDataUpdater(id);
        }

        if (PLAYER_DATA_HANDLERS.containsKey(id)) {
            LOGGER.info("running player data importer: {}", id);
            runPlayerDataUpdater(id);
        }

        if (CUSTOM_ACTIONS.containsKey(id)) {
            LOGGER.info("running custom action: {}", id);

            CUSTOM_ACTIONS.get(id).accept(System.getProperty("user.dir") + "/plugins/quark");
            CUSTOM_ACTIONS.get(id).accept(System.getProperty("user.dir") + "/plugins/Quark");
        }
    }

    private static void runPlayerDataUpdater(String id) {
        var data = iteratePlayerData();
        var handler = PLAYER_DATA_HANDLERS.get(id);

        for (var uuid : data.keySet()) {
            for (var d : data.get(uuid)) {
                handler.accept(UUID.fromString(uuid), d);
            }
        }
    }

    private static void runModuleDataUpdater(String id) {
        var folder1 = new File(System.getProperty("user.dir") + "/plugins/quark/data/module");
        var folder2 = new File(System.getProperty("user.dir") + "/plugins/Quark/data/module");//this is because a fucking update

        var handler = MODULE_DATA_HANDLERS.get(id);

        var f1 = new File(folder1.getAbsolutePath() + "/" + handler.getLeft());
        var f2 = new File(folder2.getAbsolutePath() + "/" + handler.getLeft());


        if (f1.exists()) {
            handler.getRight().accept(readAsNBT(f1));
        }
        if (f2.exists()) {
            handler.getRight().accept(readAsNBT(f2));
        }
    }

    static boolean has(String id) {
        if (PLAYER_DATA_HANDLERS.containsKey(id)) {
            return true;
        }

        if (MODULE_DATA_HANDLERS.containsKey(id)) {
            return true;
        }

        return CUSTOM_ACTIONS.containsKey(id);
    }

    static Set<String> handlers() {
        var result = new HashSet<String>();

        result.addAll(PLAYER_DATA_HANDLERS.keySet());
        result.addAll(MODULE_DATA_HANDLERS.keySet());
        result.addAll(CUSTOM_ACTIONS.keySet());

        return result;
    }


}
