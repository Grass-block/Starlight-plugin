package org.atcraftmc.starlight.internal.command;

import org.atcraftmc.qlib.command.LegacyCommandManager;
import org.atcraftmc.qlib.command.QuarkCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.starlight.ProductInfo;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.TaskService;
import org.atcraftmc.starlight.foundation.TextSender;
import org.atcraftmc.starlight.foundation.command.CoreCommand;
import org.atcraftmc.starlight.foundation.platform.APIProfileTest;
import org.atcraftmc.starlight.migration.QuarkDataImporter;

@QuarkCommand(name = "player-ui", permission = "+starlight.command.ui")
public final class PlayerUICommand extends CoreCommand {

    /*
    @Override
    public void execute(CommandExecution context) {
        var l = Starlight.instance().coreLanguage();



        var slot =




        switch (context.requireEnum(0, "info", "stats", "sync-commands", "update-data")) {
            case "info" -> ProductInfo.sendInfoDisplay(context.getSender());
            case "stats" -> ProductInfo.sendStatsDisplay(context.getSender());
            case "update-data" -> {
                var id = context.requireArgumentAt(1);


                if (!QuarkDataImporter.has(id)) {
                    l.item("data-update:none").send(context.getSender(), id);
                    return;
                }

                l.item("data-update:start").send(context.getSender(), id);

                TaskService.async().run(() -> {
                    QuarkDataImporter.runDataUpdater(id);
                    l.item("data-update:done").send(context.getSender());
                });
            }
            case "sync-commands" -> {
                LegacyCommandManager.sync();
                l.item("command:sync-commands").send(context.getSender());
            }
        };
    }

     */

    @Override
    public void suggest(CommandSuggestion suggestion) {
        super.suggest(suggestion);
        suggestion.suggest(0, "info", "stats", "sync-commands", "update-data");
        suggestion.matchArgument(0, "update-data", (ctx) -> ctx.suggest(1, QuarkDataImporter.handlers()));
    }
}
