package org.atcgroup.starlight.bundle.utilities;

import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.api.event.PlayerReadyEvent;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

@ApplicationModule(id = "ysm-sync-fix", defaultEnable = false)
@AutoRegister(Registrations.SERVER_EVENT)
public final class YSMSyncFix extends BukkitAbstractModule {
    public static final String COMMAND = "ysm model set %s \"%s\" - true";

    @Inject
    private Logger logger;

    public File getPlayerDataFile(UUID uuid) {
        var world = Bukkit.getWorlds().get(0);
        var worldFolder = world.getWorldFolder();

        return new File(
                new File(worldFolder, "playerdata"),
                uuid + ".dat"
        );
    }

    @EventHandler
    public void onPlayerReady(PlayerReadyEvent event) {
        var f = getPlayerDataFile(event.getPlayer().getUniqueId());

        if (!f.exists() || f.length() == 0) {
            return;
        }

        try (var in = new FileInputStream(f)) {
            var nbt = (NBTTagCompound) NBT.readZipped(in);

            if (nbt.hasKey("neoforge:attachments")) {
                var a = nbt.getCompoundTag("neoforge:attachments");
                if (a.hasKey("yes_steve_model:model_id")) {
                    var y = a.getCompoundTag("yes_steve_model:model_id");
                    var id = y.getString("model_id");

                    if (id != null) {
                        this.logger.info("Force-sync player {}'s model -> {}", event.getPlayer().getName(), id);

                        QLib.task().entity(event.getPlayer()).run(() -> {
                            var command = String.format(COMMAND, event.getPlayer().getName(), id);
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        });
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
