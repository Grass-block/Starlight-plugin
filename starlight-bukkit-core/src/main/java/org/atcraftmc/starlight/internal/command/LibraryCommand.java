package org.atcraftmc.starlight.internal.command;

import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.texts.ComponentBlock;
import org.atcraftmc.starlight.SharedObjects;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.TextSender;
import org.atcraftmc.starlight.core.command.CoreCommand;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;

@BukkitCommand(name = "libraries", permission = "-starlight.core.libs")
public final class LibraryCommand extends CoreCommand {

    @Override
    public void execute(CommandExecution context) {
        list(context);
    }

    public void list(CommandExecution context) {
        var paths = Starlight.instance().getLibraryManager().getLoadedURLs();
        var sender = context.requireSenderAsPlayer();
        var locale = LocaleService.locale(sender);
        var list = new ComponentBlock();

        list.add(this.getLanguage().item("list").component(locale).asComponent());

        paths.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach((e) -> {
            var id = e.getKey();
            var lib = e.getValue();


            File file;
            try {
                file = new File(lib.toURI());
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }

            var component = getLanguage().item("list-item").component(locale, id);
            var hover = getLanguage().item("list-hover").component(
                    locale,
                    id,
                    file.getName(),
                    file.length() / 1024,
                    SharedObjects.DATE_FORMAT.format(new Date(file.lastModified())),
                    file.getAbsolutePath()
            );

            var line = component.asComponent().hoverEvent(hover.asComponent().asHoverEvent());
            list.add(line);
        });

        sender.sendMessage(QLib.textEngine().renderString("{#line}"));
        TextSender.sendMessage(sender, list);
        sender.sendMessage(QLib.textEngine().renderString("{#line}"));
    }
}
