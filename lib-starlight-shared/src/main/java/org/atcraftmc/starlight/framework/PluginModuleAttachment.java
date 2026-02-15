package org.atcraftmc.starlight.framework;

import me.gb2022.gluon.ModularApplicationContext;
import me.gb2022.gluon.module.ModuleContainer;
import me.gb2022.gluon.module.ModuleMetadata;
import me.gb2022.gluon.module.attachment.ModuleAttachment;
import org.atcraftmc.qlib.config.ConfigEntry;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.qlib.language.MinecraftLocale;

public final class PluginModuleAttachment implements ModuleAttachment {
    private LanguageEntry language;
    private ConfigEntry config;
    private ModuleMetadata metadata;
    private LanguageItem displayName;

    @Override
    public void initContext(ModularApplicationContext ctx, ModuleContainer container) {
        var k = container.getMetadata().key();
        var h = ctx.holder(PluginApplication.class);

        this.language = h.language().entry(k.namespace(), k.id());
        this.config = h.config().entry(k.namespace(), k.id());
        this.metadata = container.getMetadata();

        this.displayName = h.language().item(container.getMetadata().key().namespace() + ":-module-name:" + container.getMetadata()
                .key()
                .id());
    }

    public LanguageEntry getLanguage() {
        return language;
    }

    public ConfigEntry getConfig() {
        return config;
    }

    public String displayName(MinecraftLocale locale) {
        var ns = this.metadata.key().namespace();
        var id = this.metadata.key().id();

        if (this.displayName != null && this.displayName.handle().hasAny(ns, "-module-name", id)) {
            return "%s&7(&f%s&7)".formatted(id, this.displayName.message(locale));
        } else {
            return id;
        }
    }
}
