package org.atcraftmc.starlight.worldguard;

import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;

public class WGRegionManagement extends BukkitAbstractModule {

    static LanguageItem lang(String id) {
        return SLPluginEnvironment.getApplication().language().item("starlight-worldguard:wg-custom-name:" + id);
    }
}
