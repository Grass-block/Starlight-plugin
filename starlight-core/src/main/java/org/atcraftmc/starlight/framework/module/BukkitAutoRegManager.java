package org.atcraftmc.starlight.framework.module;

import me.gb2022.modular.Registrations;
import org.atcraftmc.starlight.foundation.platform.BukkitUtil;
import org.atcraftmc.starlight.util.PluginAutoRegManager;
import org.bukkit.event.Listener;

public final class BukkitAutoRegManager extends PluginAutoRegManager {

    @Override
    public void initCustom() {
        Builder.build(this, (i) -> {
            i.attach(Registrations.SERVER_EVENT, (o) -> BukkitUtil.registerEventListener(((Listener) o)));
            i.detach(Registrations.SERVER_EVENT, (o) -> BukkitUtil.unregisterEventListener(((Listener) o)));
        });
    }
}
