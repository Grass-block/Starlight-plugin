package org.atcraftmc.starlight.framework;

import me.gb2022.gluon.module.AppModule;
import org.atcraftmc.qlib.config.ConfigEntry;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.SLPluginEnvironment;

import java.io.InputStream;

public interface PluginModule extends AppModule {
    default LanguageEntry language() {
        var k = handle().getMetadata().key();
        return SLPluginEnvironment.getPlugin().language().entry(k.namespace(), k.id());
    }

    default ConfigEntry config() {
        var k = handle().getMetadata().key();
        return SLPluginEnvironment.getPlugin().config().entry(k.namespace(), k.id());
    }

    default InputStream getResource(String path) {
        if (!path.startsWith("/")) {
            path = path + "/";
        }
        return this.getClass().getResourceAsStream("/assets" + path);
    }
}
