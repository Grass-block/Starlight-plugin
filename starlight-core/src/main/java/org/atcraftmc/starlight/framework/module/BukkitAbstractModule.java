package org.atcraftmc.starlight.framework.module;

import me.gb2022.modular.module.AbstractModule;
import org.atcraftmc.qlib.config.ConfigEntry;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.framework.PluginModuleAttachment;

public abstract class BukkitAbstractModule extends AbstractModule implements BukkitModule {
    private LanguageEntry language;
    private ConfigEntry config;

    @Override
    public void initialize() {
        var a = handle().getAttachment(PluginModuleAttachment.class);

        this.language = a.getLanguage();
        this.config = a.getConfig();
    }

    @Override
    public final LanguageEntry language() {
        return this.language;
    }

    @Override
    public final ConfigEntry config() {
        return this.config;
    }
}
