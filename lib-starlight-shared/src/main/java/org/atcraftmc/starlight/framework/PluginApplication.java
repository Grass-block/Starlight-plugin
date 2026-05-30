package org.atcraftmc.starlight.framework;

import me.gb2022.gluon.ModularApplicationContext;
import org.atcraftmc.qlib.PluginConcept;
import org.atcraftmc.qlib.config.ConfigContainer;
import org.atcraftmc.qlib.language.LanguageContainer;
import org.atcraftmc.starlight.util.dependency.LibraryManager;

public interface PluginApplication extends PluginConcept {
    static ModularApplicationContext.Builder createContext(PluginApplication app) {
        return ModularApplicationContext.builder(app).packageManager(PluginPackageManager::new);
    }

    LanguageContainer language();

    ConfigContainer config();
}
