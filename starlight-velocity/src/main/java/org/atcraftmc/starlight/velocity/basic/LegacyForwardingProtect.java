package org.atcraftmc.starlight.velocity.basic;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import me.gb2022.commons.math.SHA;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.starlight.shared.RemoteMessageService;
import org.atcraftmc.starlight.velocity.framework.VelocityAbstractModule;

@ApplicationModule(id = "legacy-forwarding-protect", description = "Protects servers from unauthorized forwarding connections")
@AutoRegister(Registrations.SERVER_EVENT)
public final class LegacyForwardingProtect extends VelocityAbstractModule {

    @Subscribe
    public void onServerConnect(ServerPreConnectEvent event) {
        var server = event.getOriginalServer().getServerInfo().getName();
        var sign = SHA.getSHA256(event.getPlayer().getUsername(), true);

        RemoteMessageService.instance().message(server, "forwarding:verification", sign);
    }
}
