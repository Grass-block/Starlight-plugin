package org.atcraftmc.starlight.framework;

import me.gb2022.modular.ModularApplicationContext;
import org.atcraftmc.qlib.PluginConcept;
import org.atcraftmc.qlib.config.ConfigContainer;
import org.atcraftmc.qlib.language.LanguageContainer;

public interface PluginApplication extends PluginConcept, SLPluginConcept {
    static ModularApplicationContext.Builder createContext(PluginApplication app) {
        return ModularApplicationContext.builder(app).packageManager(PluginPackageManager::new);
    }

    LanguageContainer language();

    ConfigContainer config();
}
