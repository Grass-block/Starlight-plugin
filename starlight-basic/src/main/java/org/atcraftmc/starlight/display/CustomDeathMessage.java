package org.atcraftmc.starlight.display;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import org.atcraftmc.starlight.core.platform.Compatibility;
import me.gb2022.gluon.module.ApplicationModule;

import java.util.Objects;

@ApplicationModule(id="custom-death-message",description = "Re-format your death message.")
@AutoRegister(Registrations.SERVER_EVENT)
public final class CustomDeathMessage extends BukkitAbstractModule {
    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requireMethod(()->PlayerDeathEvent.class.getMethod("deathMessage"));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        var prefix = QLib.textBuilder().buildComponent(config().value("prefix").string());
        var suffix = QLib.textBuilder().buildComponent(config().value("suffix").string());

        if (e.deathMessage() == null) {
            return;
        }

        e.deathMessage(prefix.append(Objects.requireNonNull(e.deathMessage())).append(suffix));
    }
}
