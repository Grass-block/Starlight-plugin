package org.atcraftmc.starlight.velocity.basic.peer;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import me.gb2022.commons.math.SHA;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.modular.Registrations;
import me.gb2022.modular.module.AbstractModule;
import org.atcraftmc.starlight.shared.service.RemoteMessageService;
import org.atcraftmc.starlight.velocity.framework.VelocityModule;

@AutoRegister(Registrations.SERVER_EVENT)
public final class LegacyForwardingProtect extends AbstractModule<SLVModuleHandle, SLVPackage> implements VelocityModule {

    @Subscribe
    public void onServerConnect(ServerPreConnectEvent event) {
        var server = event.getOriginalServer().getServerInfo().getName();
        var sign = SHA.getSHA256(event.getPlayer().getUsername(), true);

        RemoteMessageService.instance().message(server, "forwarding:verification", sign);
    }
}
