package org.atcraftmc.starlight.velocity.framework;

import com.velocitypowered.api.proxy.ProxyServer;
import org.atcraftmc.starlight.framework.PluginModule;
import org.atcraftmc.starlight.velocity.StarlightVelocity;

public interface VelocityModule extends PluginModule {
    default StarlightVelocity getProxy() {
        return StarlightVelocity.INSTANCE.get();
    }

    default ProxyServer getProxyServer() {
        return this.getProxy().getServer();
    }
}
