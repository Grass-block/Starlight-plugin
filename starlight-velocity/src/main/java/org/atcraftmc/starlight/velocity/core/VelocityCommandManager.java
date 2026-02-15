package org.atcraftmc.starlight.velocity.core;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.InvocableCommand;
import me.gb2022.commons.reflect.Annotations;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.Service;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.starlight.velocity.StarlightVelocity;
import org.atcraftmc.starlight.velocity.util.VelocityCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationService(id = "vc-command-manager", impl = VelocityCommandManager.Impl.class)
public interface VelocityCommandManager extends Service {
    @ServiceInject
    ServiceHolder<VelocityCommandManager> INSTANCE = new ServiceHolder<>();

    static void registerCommand(InvocableCommand<?> command) {
        Annotations.matchAnnotation(command, VelocityCommand.class, (c) -> INSTANCE.get().registerCommand(command, c.name(), c.aliases()));
    }

    static void unregisterCommand(InvocableCommand<?> command) {
        Annotations.matchAnnotation(command, VelocityCommand.class, (c) -> INSTANCE.get().unregisterCommand(c.name(), c.aliases()));
    }

    void registerCommand(InvocableCommand<?> command, String name, String... aliases);

    void unregisterCommand(String name, String... aliases);

    Map<String, CommandMeta> getMetas();

    Optional<CommandMeta> meta(String name);

    final class Impl implements VelocityCommandManager {
        private final Map<String, CommandMeta> metas = new HashMap<>();

        private final StarlightVelocity plugin = StarlightVelocity.instance();
        private final CommandManager handle = this.plugin.getServer().getCommandManager();


        @Override
        public void registerCommand(InvocableCommand<?> command, String name, String... aliases) {
            var meta = this.handle.metaBuilder(name).aliases(aliases).plugin(this.plugin).build();
            this.handle.register(meta, command);

            this.metas.put(name, meta);
        }

        @Override
        public void unregisterCommand(String name, String... aliases) {
            var meta = this.handle.metaBuilder(name).aliases(aliases).plugin(this.plugin).build();
            this.handle.unregister(meta);

            this.metas.remove(name);
        }

        @Override
        public Map<String, CommandMeta> getMetas() {
            return metas;
        }

        @Override
        public Optional<CommandMeta> meta(String name) {
            return Optional.ofNullable(this.metas.get(name));
        }
    }


}
