package org.atcraftmc.starlight.display;

import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import net.kyori.adventure.text.ComponentLike;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.language.Language;
import org.atcraftmc.starlight.api.PlayerFirstJoinEvent;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.TaskService;
import org.atcraftmc.starlight.core.TextSender;
import org.atcraftmc.starlight.core.command.CommandProvider;
import org.atcraftmc.starlight.core.command.ModuleCommand;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

@AutoRegister(Registrations.SERVER_EVENT)
@CommandProvider({WelcomeMessage.WelcomeMessageCommand.class})
@ApplicationModule(id = "welcome-message", version = "0.1.0", description = "Present a welcome message when player join a server at first time.")
public final class WelcomeMessage extends BukkitAbstractModule {
    @EventHandler
    public void onPlayerFirstJoin(PlayerFirstJoinEvent event) {
        TaskService.global().delay(5, () -> this.sendWelcomeMessage(event.getPlayer()));
    }

    private void sendWelcomeMessage(Player player) {
        try {
            var msg = this.language().inline(Language.generateTemplate(this.config(), "ui"), LocaleService.locale(player));
            msg = msg.replace("{player}", player.getName());
            ComponentLike component = QLib.textBuilder().buildComponent(msg);
            TextSender.sendMessage(player, component);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BukkitCommand(name = "welcome-message")
    public static final class WelcomeMessageCommand extends ModuleCommand<WelcomeMessage> {

        @Override
        public void onCommand(CommandSender sender, String[] args) {
            this.getModule().sendWelcomeMessage(((Player) sender));
        }
    }
}
