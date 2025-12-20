package org.atcraftmc.starlight.velocity.peer;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import me.gb2022.commons.math.SHA;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.modular.Registrations;
import org.atcraftmc.quark_velocity.ProxyModule;
import org.atcraftmc.quark_velocity.Registers;
import org.atcraftmc.starlight.velocity.framework.module.SLVPackageModule;

@AutoRegister(Registrations.SERVER_EVENT)
public final class LegacyForwardingProtect extends SLVPackageModule {

    @Subscribe
    public void onServerConnect(ServerPreConnectEvent event) {
        var server = event.getOriginalServer().getServerInfo().getName();
        var sign = SHA.getSHA256(event.getPlayer().getUsername(), true);

        this.getMessenger().message(server, "forwarding:verification", sign);
    }
}
