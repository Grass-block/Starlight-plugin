package org.atcgroup.starlight.bundle.mission;

import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.util.PlayerMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;

//todo
@ApplicationModule(id = "online-time-reward")
public class OnlineTimeReward extends BukkitAbstractModule {
    private final PlayerMap<Long> loginRecords = new PlayerMap<>();

    @Override
    public void enable() throws Exception {

    }

    @Override
    public void disable() throws Exception {
        super.disable();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        var period = config().value("tick-period").intValue();
        QLib.task().async().timer(0, period, () -> {

        });


        this.loginRecords.put(event.getPlayer(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.loginRecords.remove(event.getPlayer());
    }


}
