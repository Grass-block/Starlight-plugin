package org.atcraftmc.starlight.console;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.List;

@ApplicationModule(id = "stop-confirm", defaultEnable = false)
@AutoRegister(Registrations.SERVER_EVENT)
public final class StopConfirm extends BukkitAbstractModule {
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!List.of(event.getMessage().split("")).contains("/stop")) {
            return;
        }
        if (event.getMessage().contains("confirm")) {
            event.setMessage(event.getMessage().replace("confirm", ""));
            return;
        }
        event.setCancelled(true);

        MessageAccessor.send(this.language(), event.getPlayer(), "hint");
    }

    @EventHandler
    public void onCommand(ServerCommandEvent event) {
        if (!event.getCommand().startsWith("stop")) {
            return;
        }
        if (event.getCommand().contains("confirm")) {
            event.setCommand(event.getCommand().replace("confirm", ""));
            return;
        }
        event.setCancelled(true);

        MessageAccessor.send(this.language(), event.getSender(), "hint");
    }
}
