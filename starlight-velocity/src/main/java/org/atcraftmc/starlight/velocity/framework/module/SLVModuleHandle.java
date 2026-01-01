package org.atcraftmc.starlight.velocity.framework.module;

import me.gb2022.modular.ComponentMetadata;
import me.gb2022.modular.module.ModuleHandle;
import org.atcraftmc.qlib.config.ConfigContainer;
import org.atcraftmc.qlib.config.ConfigEntry;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.starlight.velocity.StarlightVelocity;
import org.atcraftmc.starlight.velocity.framework.packages.SLVPackage;

public final class SLVModuleHandle extends ModuleHandle<SLVModule, SLVModuleHandle, SLVPackage> {
    private final LanguageItem displayName;
    private LanguageEntry language;
    private ConfigEntry config;

    public SLVModuleHandle(SLVPackage pack, Class<? extends SLVModule> reference) {
        super(pack, reference, ComponentMetadata.fromModule(pack.getId(), reference));
        this.displayName = StarlightVelocity.lang().access(pack.getId()).item("-module-name:" + getMetadata().key().id());
    }

    @Override
    public void preEnable(SLVModule module) {
        var ns = this.getParent().getId();
        var id = this.getMetadata().key().id();

        this.language = StarlightVelocity.lang().entry(ns, id);
        this.config = ConfigContainer.getInstance().entry(ns, id);

        ModuleServices.onEnable(this);
    }

    @Override
    public void postDisable(SLVModule module) {
        ModuleServices.onDisable(this);
    }

    @Override
    public String getLoggerName() {
        return "Starlight";
    }

    public String displayName(MinecraftLocale locale) {
        var ns = this.getMetadata().key().namespace();
        var id = this.getMetadata().key().id();

        if (this.displayName != null && this.displayName.handle().hasAny(ns, "-module-name", id)) {
            return "%s&7(&f%s&7)".formatted(id, this.displayName.message(locale));
        } else {
            return id;
        }
    }

    public ConfigEntry getConfig() {
        return config;
    }

    public LanguageEntry getLanguage() {
        return language;
    }
}
