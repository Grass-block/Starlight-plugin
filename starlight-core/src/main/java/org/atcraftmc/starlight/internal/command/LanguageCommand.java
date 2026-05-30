package org.atcraftmc.starlight.internal.command;

import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.config.PackContainer;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.StarlightBukkitCore;

@BukkitCommand(name = "language", permission = "-quark.language")
public final class LanguageCommand extends PackConfigureCommand {

    @Override
    public LanguageEntry getLanguageEntry() {
        return StarlightBukkitCore.lang().entry("starlight-core", "language");
    }

    @Override
    public PackContainer<?> getPackContainer() {
        return StarlightBukkitCore.lang();
    }
}
