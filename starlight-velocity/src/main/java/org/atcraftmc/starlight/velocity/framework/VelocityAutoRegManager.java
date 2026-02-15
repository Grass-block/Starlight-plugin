package org.atcraftmc.starlight.velocity.framework;

import me.gb2022.gluon.Registrations;
import org.atcraftmc.starlight.util.PluginAutoRegManager;
import org.atcraftmc.starlight.velocity.util.VelocityUtil;

public final class VelocityAutoRegManager extends PluginAutoRegManager {
    @Override
    public void initCustom() {
        Builder.build(this, (i) -> {
            i.attach(Registrations.SERVER_EVENT, VelocityUtil::registerListener);
            i.detach(Registrations.SERVER_EVENT, VelocityUtil::unregisterListener);
        });
    }
}
