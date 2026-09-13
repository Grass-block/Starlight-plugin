package org.atcraftmc.starlight.config;

import org.atcraftmc.qlib.PluginConcept;
import org.atcraftmc.qlib.language.LanguagePack;

public final class NestedLanguagePack extends LanguagePack {
    public NestedLanguagePack(String id, String locale, PluginConcept provider) {
        super(id, locale, provider);
    }

    public String getTemplateResource() {
        return "/templates/lang/%s/%s.%s.yml".formatted(this.id, this.id, this.getLocale());
    }
}
