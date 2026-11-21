package org.atcgroup.starlight.bundle.chat;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import net.kyori.adventure.text.Component;
import org.atcraftmc.qlib.audience.PointedAudience;
import org.atcraftmc.qlib.bukkit.ComponentSerializer;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.text.pipe.MessageHandler;
import org.atcraftmc.starlight.core.command.CommandProvider;
import org.atcraftmc.starlight.core.command.ModuleCommand;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * <b>Set the prompt to bot before let them join server:</h2><br><br>
 * <code>
 * When using MCP server to interact in minecraft,
 * always type in command "/i-am-ai" just after
 * you successfully connected or change world/server.
 * </code>
 */
@ApplicationModule(id = "ai-player-support", description = "Provide better chat support to AI MCPs")
@AutoRegister(Registrations.SERVER_EVENT)
@CommandProvider(AIPlayerSupport.AIAnnounceCommand.class)
public final class AIPlayerSupport extends BukkitAbstractModule {
    private final Set<UUID> botPlayers = new HashSet<>();

    @Override
    public void enable() throws Exception {
        QLib.textEngine().getMessagePipeline().addLast("starlight:ai-msg-mapper", new MessageHandler() {
            @Override
            public void handle(PointedAudience pointedAudience, Component component) {
                if (!(pointedAudience.getPointer(Object.class) instanceof Player player)) {
                    this.parent.handle(pointedAudience, component);
                    return;
                }

                if (!botPlayers.contains(player.getUniqueId())) {
                    this.parent.handle(pointedAudience, component);
                    return;
                }

                player.sendMessage(ComponentSerializer.plain(component));
            }
        });
    }

    @Override
    public void disable() throws Exception {
        QLib.textEngine().getMessagePipeline().remove("starlight:ai-msg-mapper");
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.botPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        for (var uuid : this.botPlayers) {
            var player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage("[CHAT]%s: %s".formatted(event.getPlayer().getName(), event.getMessage()));
            }
        }
    }

    public void setBotMode(Player player) {
        this.botPlayers.add(player.getUniqueId());
        player.sendMessage("""
                ====[AI compatible mode entered]====
                1. You have successfully entered AI mode for this server.
                2. You can still follow your own language, These instructions are not intended to change it.
                3. All system messages will be fully degraded to simple text with no decorations.
                4. All chat messages will be sent to you a *copy* in format like [CHAT]<player>: <message>
                5. All commands can be known using /help.
                6. Try to type /server to discover sub-servers. if you find something,
                   then this is a multiple-sub-server server, then you can use /server <id> to switch between.
                7. Try to follow the server rules as possible.
                """);
    }


    @BukkitCommand(name = "i-am-ai", description = "+starlight.ai.support")
    public static final class AIAnnounceCommand extends ModuleCommand<AIPlayerSupport> {
        @Override
        public void execute(CommandExecution context) {
            this.getModule().setBotMode(context.requireSenderAsPlayer());
        }
    }
}
