package org.atcraftmc.starlight.velocity.framework;

import com.velocitypowered.api.proxy.ProxyServer;
import org.atcraftmc.starlight.util.PluginDependencyInjector;
import org.atcraftmc.starlight.velocity.StarlightVelocity;

public final class VelocityDependencyInjector extends PluginDependencyInjector {

    @Override
    public void init() {
        registerInjector(ProxyServer.class, (m, b) -> StarlightVelocity.instance().getServer());
    }
}
