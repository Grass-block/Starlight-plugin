package org.atcraftmc.starlight.velocity.basic;

import me.gb2022.apm.remote.RemoteMessenger;
import me.gb2022.apm.remote.event.EndpointJoinEvent;
import me.gb2022.apm.remote.event.EndpointLeftEvent;
import me.gb2022.apm.remote.event.RemoteEventListener;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.starlight.velocity.framework.VelocityAbstractModule;
import org.atcraftmc.starlight.velocity.util.ServerDisplayName;

@AutoRegister(Registrations.PLUGIN_VPN_LISTENER)
@ApplicationModule(id = "server-statement-observer")
public final class ServerStatementObserver extends VelocityAbstractModule implements RemoteEventListener {

    @Override
    public void endpointJoined(RemoteMessenger messenger, EndpointJoinEvent event) {
        var sender = event.getServer();
        var display = ServerDisplayName.getDisplayName(event.getServer());
        getProxy().getServer().getAllPlayers().stream().filter((p) -> {
            var server = p.getCurrentServer();
            return server.filter(con -> !con.getServer().getServerInfo().getName().equals(sender)).isPresent();
        }).forEach((p) -> language().item("online").send(p, display));
    }

    @Override
    public void endpointLeft(RemoteMessenger messenger, EndpointLeftEvent event) {
        var sender = event.getServer();
        var display = ServerDisplayName.getDisplayName(event.getServer());
        getProxy().getServer().getAllPlayers().stream().filter((p) -> {
            var server = p.getCurrentServer();
            return server.filter(con -> !con.getServer().getServerInfo().getName().equals(sender)).isPresent();
        }).forEach((p) -> language().item("offline").send(p, display));
    }
}
