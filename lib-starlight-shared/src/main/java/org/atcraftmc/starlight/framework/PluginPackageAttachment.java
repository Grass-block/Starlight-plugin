package org.atcraftmc.starlight.framework;

import me.gb2022.commons.container.Pair;
import me.gb2022.gluon.ModularApplicationContext;
import me.gb2022.gluon.pack.ApplicationPackage;
import me.gb2022.gluon.pack.PackageAttachment;
import org.atcraftmc.qlib.PluginConcept;
import org.atcraftmc.qlib.config.Configuration;
import org.atcraftmc.qlib.language.LanguagePack;
import org.atcraftmc.starlight.shared.Configurations;

import java.util.HashSet;
import java.util.Set;

public final class PluginPackageAttachment implements PackageAttachment {
    private final Set<String> configs = new HashSet<>();
    private final Set<Pair<String, String>> languages = new HashSet<>();
    private final Set<LanguagePack> languagePacks = new HashSet<>();
    private final Set<Configuration> configurations = new HashSet<>();

    private PluginApplication application;

    public void config(String id) {
        this.configs.add(id);
    }

    public void language(String id, String language) {
        this.languages.add(new Pair<>(id, language));
    }

    @Override
    public void initContext(ModularApplicationContext ctx, ApplicationPackage pkg) {
        this.application = ctx.holder(PluginApplication.class);
        var concept = pkg.holder(Object.class);
        var core = ctx.holder(PluginConcept.class);
        var wrap = PluginConcept.wrapSubPack(concept, core);

        for (var id : this.configs) {
            this.configurations.add(Configurations.values(wrap, id));
        }

        for (Pair<String, String> pack : this.languages) {
            this.languagePacks.add(new LanguagePack(pack.getLeft(), pack.getRight(), wrap));
        }
    }

    @Override
    public void enable() throws Exception {
        for (var pack : this.languagePacks) {
            pack.load();
            this.application.language().register(pack);
        }
        for (var cfg : this.configurations) {
            cfg.load();
            this.application.config().register(cfg);
        }
    }

    @Override
    public void disable() throws Exception {
        for (var pack : this.languagePacks) {
            this.application.language().unregister(pack);
        }
        for (var cfg : this.configurations) {
            this.application.config().unregister(cfg);
        }
    }
}
