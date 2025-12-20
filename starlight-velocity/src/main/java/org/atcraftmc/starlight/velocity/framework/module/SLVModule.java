package org.atcraftmc.starlight.velocity.framework.module;

import com.velocitypowered.api.proxy.ProxyServer;
import me.gb2022.modular.module.IModule;
import org.atcraftmc.qlib.config.ConfigEntry;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.velocity.StarlightVelocity;
import org.atcraftmc.starlight.velocity.framework.packages.SLVPackage;

import java.io.InputStream;

public interface SLVModule extends IModule<SLVPackage, SLVModuleHandle> {
    default LanguageEntry language() {
        return this.handle().getLanguage();
    }

    default ConfigEntry config() {
        return this.handle().getConfig();
    }

    default InputStream getResource(String path) {
        if (!path.startsWith("/")) {
            path = path + "/";
        }
        return this.getClass().getResourceAsStream("/assets" + path);
    }

    default StarlightVelocity getProxy() {
        return StarlightVelocity.INSTANCE.get();
    }

    default ProxyServer getProxyServer() {
        return this.getProxy().getServer();
    }
}
