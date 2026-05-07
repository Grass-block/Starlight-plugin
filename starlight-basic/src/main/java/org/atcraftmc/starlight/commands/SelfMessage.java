package org.atcraftmc.starlight.commands;

import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.texts.TextBuilder;
import org.atcraftmc.starlight.foundation.TextSender;
import org.atcraftmc.starlight.framework.module.SLCommandModule;
import me.gb2022.gluon.module.ApplicationModule;

@ApplicationModule(id="self-message")
@BukkitCommand(name = "self-msg")
public final class SelfMessage extends SLCommandModule {
    @Override
    public void execute(CommandExecution context) {
        TextSender.sendMessage(context.getSender(), QLib.textBuilder().build(context.requireRemainAsParagraph(0, true)));
    }

    @Override
    public void suggest(CommandSuggestion suggestion) {
        suggestion.suggest(0, "[messages...]");
    }
}
