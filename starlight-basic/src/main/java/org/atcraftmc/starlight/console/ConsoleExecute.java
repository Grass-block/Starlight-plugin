package org.atcraftmc.starlight.console;

import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.LegacyCommandManager;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.framework.module.SLCommandModule;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@BukkitCommand(name = "console", permission = "-starlight.console.execute")
@ApplicationModule(id="console-execute",version = "1.0.0", description = "Executes commands from the server console")
public final class ConsoleExecute extends SLCommandModule {
    @Inject
    private LanguageEntry language;

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s).append(" ");
        }
        MessageAccessor.send(this.language, sender, "execute", sb.toString());
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), sb.toString());
    }

    @Override
    public void suggest(CommandSuggestion suggestion) {
        var buffer = suggestion.getBuffer();
        if (buffer.isEmpty()) {
            return;
        }

        var id = buffer.get(0);
        var cmd = LegacyCommandManager.getCommandMap().getCommand(id);

        if (cmd == null) {
            return;
        }

        var origin = buffer.toArray(new String[0]);

        if (origin.length < 1) {
            return;
        }

        var subArgs = new String[origin.length - 1];

        System.arraycopy(origin, 1, subArgs, 0, origin.length - 1);

        suggestion.suggest(buffer.size() - 1, cmd.tabComplete(suggestion.getSender(), id, subArgs));
    }
}
