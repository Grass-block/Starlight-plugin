package org.atcraftmc.starlight.core.command;

import org.atcraftmc.qlib.command.AbstractCommand;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.atcraftmc.starlight.StarlightBukkitCore;

public abstract class CoreCommand extends AbstractCommand {
    private final LanguageEntry entry;

    protected CoreCommand() {
        this.init(StarlightBukkitCore.instance().getCommandManager());
        this.entry = StarlightBukkitCore.instance().language().entry("starlight-core:" + this.getLanguageNamespace());
    }

    public LanguageEntry getLanguage() {
        return this.entry;
    }

    public String getLanguageNamespace() {
        return this.getName();
    }
}
