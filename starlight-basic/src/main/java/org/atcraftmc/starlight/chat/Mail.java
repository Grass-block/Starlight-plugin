package org.atcraftmc.starlight.chat;

import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.starlight.chat.mail.JDBCMailDataService;
import org.atcraftmc.starlight.core.command.CommandProvider;
import org.atcraftmc.starlight.core.command.ModuleCommand;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.shared.jdbc.JDBCData;
import org.atcraftmc.starlight.shared.JDBCService;

@CommandProvider(Mail.MailCommand.class)
public class Mail extends BukkitAbstractModule {
    private final JDBCMailDataService dataService = new JDBCMailDataService();


    @Override
    public void enable() throws Exception {
        this.dataService.initService(JDBCService.dataSource(JDBCData.SL_SHARED));
    }

    @BukkitCommand(name = "mail", permission = "+starlight.mail", subCommands = {})
    public static final class MailCommand extends ModuleCommand<Mail> {

    }
}
