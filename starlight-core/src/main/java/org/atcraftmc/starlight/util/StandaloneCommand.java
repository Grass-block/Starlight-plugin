package org.atcraftmc.starlight.util;

import org.atcraftmc.qlib.command.AbstractCommand;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.Starlight;

public abstract class StandaloneCommand extends AbstractCommand {
    public StandaloneCommand() {
        this.init(Starlight.instance().getCommandManager());
    }

    public LanguageEntry language(String id) {
        return Starlight.instance().language().entry(id);
    }
}
