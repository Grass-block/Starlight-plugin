package org.atcraftmc.starlight.util;

import org.atcraftmc.qlib.command.AbstractCommand;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.StarlightBukkitCore;

public abstract class StandaloneCommand extends AbstractCommand {
    public StandaloneCommand() {
        this.init(StarlightBukkitCore.instance().getCommandManager());
    }

    public LanguageEntry language(String id) {
        return StarlightBukkitCore.instance().language().entry(id);
    }
}
