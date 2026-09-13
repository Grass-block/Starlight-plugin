package org.atcraftmc.starlight.internal;

import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.internal.platformapi.ActionBarSender;
import org.atcraftmc.starlight.internal.platformapi.ChatSender;

@ApplicationModule(id = "protocol-lib-injector", internal = true, description = "Create more compatible message sending via ProtocolLib.")
public final class ProtocolLibPlatformInjector extends BukkitAbstractModule {
    public static final String HANDLER_ID = "starlight:plib-inject";

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requirePlugin("ProtocolLib");
    }

    @Override
    public void enable() {
        QLib.textEngine().getMessagePipeline().addLast(HANDLER_ID, ChatSender.getImpl());
        QLib.textEngine().getActionBarPipeline().addLast(HANDLER_ID, ActionBarSender.getImpl());
    }

    @Override
    public void disable() {
        QLib.textEngine().getMessagePipeline().remove(HANDLER_ID);
        QLib.textEngine().getActionBarPipeline().remove(HANDLER_ID);
    }
}
