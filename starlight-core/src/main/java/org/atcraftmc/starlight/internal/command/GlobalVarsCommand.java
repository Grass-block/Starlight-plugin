package org.atcraftmc.starlight.internal.command;

import org.atcraftmc.qlib.command.QuarkCommand;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.core.placeholder.PlaceHolderService;
import org.atcraftmc.starlight.foundation.command.CoreCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

@QuarkCommand(name = "globalvars", permission = "-quark.globalvars")
public final class GlobalVarsCommand extends CoreCommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        switch (args[0]) {
            case "reload" -> {
                PlaceHolderService.reloadExternal();
                Starlight.instance().coreLanguage().item("global-var:reload").send(sender);
            }
            case "restore" -> {
                PlaceHolderService.EXTERNAL_VARS.restore();
                Starlight.instance().coreLanguage().item("global-var:restore").send(sender);
            }
            case "sync" -> {
                PlaceHolderService.EXTERNAL_VARS.sync();
                Starlight.instance().coreLanguage().item("global-var:sync").send(sender);
            }
        }
    }

    @Override
    public void onCommandTab(CommandSender sender, String[] buffer, List<String> tabList) {
        if (buffer.length == 1) {
            tabList.add("reload");
            tabList.add("restore");
            tabList.add("sync");
        }
    }
}
