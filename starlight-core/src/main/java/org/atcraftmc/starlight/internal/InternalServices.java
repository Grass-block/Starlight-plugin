package org.atcraftmc.starlight.internal;

import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.api.event.CommandEvent;
import org.atcraftmc.starlight.api.event.CommandTabEvent;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.atcraftmc.starlight.framework.BukkitService;
import org.atcraftmc.starlight.internal.command.InternalCommands;
import org.atcraftmc.starlight.shared.JDBCService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.TabCompleteEvent;

public interface InternalServices {
    @ApplicationService(id = "internal#bungee-channel-supplier")
    interface BungeeChannelSupplier extends BukkitService {
        String BUNGEE_CHANNEL_ID = "BungeeCord";

        @ServiceInject
        static void start() {
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Starlight.instance(), BUNGEE_CHANNEL_ID);
        }

        @ServiceInject
        static void stop() {
            Bukkit.getServer().getMessenger().unregisterOutgoingPluginChannel(Starlight.instance(), BUNGEE_CHANNEL_ID);
        }
    }

    @ApplicationService(id = "internal#jdbc-service-ticker")
    interface JDBCServiceTicker extends BukkitService {

        @ServiceInject
        static void start() {
            QLib.task().async().timer("starlight:jdbc-tick", 0, 20, () -> {
                var i = JDBCService.getInstance();

                if (i == null) {
                    return;
                }
                i.tick();
            });
        }

        @ServiceInject
        static void stop() {
            QLib.task().global().cancel("starlight:jdbc-tick");
        }
    }

    @ApplicationService(id = "internal#command-provider")
    interface InternalCommandsProvider extends BukkitService {
        @ServiceInject
        static void start() {
            InternalCommands.register();
        }

        @ServiceInject
        static void stop() {
            InternalCommands.unregister();
        }
    }

    @ApplicationService(id = "internal#command-event", impl = CommandEventService.CommandEventAdapter.class)
    interface CommandEventService extends BukkitService {

        @ServiceInject
        ServiceHolder<CommandEventAdapter> INSTANCE = new ServiceHolder<>();

        @ServiceInject
        static void start() {
            BukkitUtil.registerEventListener(INSTANCE.get());
        }

        @ServiceInject
        static void stop() {
            BukkitUtil.unregisterEventListener(INSTANCE.get());
        }

        //well I use this damn method to avoid covering vanilla commands. :D
        final class CommandEventAdapter implements Listener, CommandEventService {
            @EventHandler
            public void onCommand(PlayerCommandPreprocessEvent event) {
                if (exec(event.getPlayer(), event.getMessage().replaceFirst("/", ""))) {
                    event.setCancelled(true);
                }
            }

            @EventHandler
            public void onCommand(ServerCommandEvent event) {
                if (exec(event.getSender(), event.getCommand())) {
                    event.setCancelled(true);
                }
            }


            @EventHandler
            public void onTabComplete(TabCompleteEvent event) {
                CommandTabEvent evt = new CommandTabEvent(
                        event.getSender(),
                        event.getBuffer(),
                        event.getBuffer().split(" "),
                        event.getCompletions()
                );

                QLib.task().async().run(() -> Bukkit.getPluginManager().callEvent(evt));
                if (evt.isCancelled()) {
                    event.setCancelled(true);
                }
            }

            private boolean exec(CommandSender sender, String commandLine) {
                String[] raw = commandLine.split(" ");
                String[] args = new String[raw.length - 1];
                System.arraycopy(raw, 1, args, 0, raw.length - 1);


                CommandEvent evt = new CommandEvent(sender, raw[0], args);

                QLib.task().async().run(() -> Bukkit.getPluginManager().callEvent(evt));

                return evt.isCancelled();
            }
        }
    }
}
