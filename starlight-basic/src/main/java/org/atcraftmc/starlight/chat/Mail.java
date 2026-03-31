package org.atcraftmc.starlight.chat;

import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.starlight.chat.mail.JDBCMailDataService;
import org.atcraftmc.starlight.foundation.command.CommandProvider;
import org.atcraftmc.starlight.foundation.command.ModuleCommand;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.shared.service.JDBCService;

@CommandProvider(Mail.MailCommand.class)
public class Mail extends BukkitAbstractModule {
    private final JDBCMailDataService dataService = new JDBCMailDataService();


    @Override
    public void enable() throws Exception {
        this.dataService.init(JDBCService.getDB(JDBCService.SL_SHARED).orElseThrow());
    }

    @BukkitCommand(name = "mail", permission = "+starlight.mail", subCommands = {})
    public static final class MailCommand extends ModuleCommand<Mail> {

    }
}
