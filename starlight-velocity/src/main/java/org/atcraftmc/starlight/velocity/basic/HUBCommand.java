package org.atcraftmc.starlight.velocity.basic;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.gb2022.gluon.module.ApplicationModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.atcraftmc.starlight.velocity.core.VelocityCommandManager;
import org.atcraftmc.starlight.velocity.framework.VelocityAbstractModule;
import org.atcraftmc.starlight.velocity.util.VelocityCommand;

@VelocityCommand(name = "hub", aliases = {"lobby", "quit"})
@ApplicationModule(id = "hub-command", description = "Teleports players to the hub or lobby server")
public final class HUBCommand extends VelocityAbstractModule implements SimpleCommand {

    @Override
    public void enable() {
        VelocityCommandManager.registerCommand(this);
    }

    @Override
    public void disable() {
        VelocityCommandManager.unregisterCommand(this);
    }

    @Override
    public void execute(Invocation invocation) {
        var source = invocation.source();
        var server = this.config().value("server").string();

        if (!(source instanceof Player player)) {
            source.sendMessage(Component.text("[Quark-Velocity] You must be a player to use this command!", NamedTextColor.RED));
            return;
        }

        this.getProxyServer().getServer(server).ifPresentOrElse(
                (lobby) -> player.createConnectionRequest(lobby).connect(),
                () -> player.sendMessage(Component.text("[Quark-Velocity] No lobby server found!", NamedTextColor.RED))
        );
    }
}
