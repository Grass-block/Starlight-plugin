package org.atcraftmc.starlight.util;

import me.gb2022.commons.reflect.DependencyInjector;
import me.gb2022.modular.pack.ApplicationPackage;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.starlight.data.assets.Asset;
import org.atcraftmc.starlight.data.assets.AssetGroup;
import org.atcraftmc.starlight.framework.PluginModule;

import java.util.function.Function;

public class PluginDependencyInjector extends DependencyInjector<PluginModule> {
    public PluginDependencyInjector() {
        this.initSelf();
        this.init();
    }

    public final void initSelf() {
        Function<String[], Boolean> useCacheForAsset = (p) -> p.length == 1 || Boolean.parseBoolean(p[1]);

        registerInjector(Asset.class, (p, m) -> new Asset(m.owner(), p[0], useCacheForAsset.apply(p)));
        registerInjector(AssetGroup.class, (p, m) -> new AssetGroup(m.owner(), p[0], useCacheForAsset.apply(p)));
        registerInjector(ApplicationPackage.class, (p, m) -> m.parent());
        registerInjector(Logger.class, (p, m) -> m.handle().getLogger());
        registerInjector(LanguageEntry.class, (p, m) -> m.language());
        registerInjector(LanguageItem.class, (p, m) -> m.language().item(p[0]));
    }

    @Override
    public final <T> T createInjection(Class<T> type, PluginModule owner, String argument) {
        return super.createInjection(type, owner, argument.replace("/", ";"));
    }

    public void init() {

    }
}
