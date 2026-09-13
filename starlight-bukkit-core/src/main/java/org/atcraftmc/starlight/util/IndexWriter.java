package org.atcraftmc.starlight.util;

import me.gb2022.gluon.module.ModuleContainer;
import me.gb2022.gluon.pack.ApplicationPackage;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.atcraftmc.starlight.framework.SLPluginHandle;

import java.io.File;

public interface IndexWriter {
    static void index(IndexWriter writer) {
        var packages = StarlightBukkitCore.instance().getGluonContext().getPackageManager().getPackages().values();

        for (var pkg : packages) {
            writer.writePackageHeader(pkg);

            for (var mod : pkg.getModules()) {
                writer.writeModuleInfo(mod);
            }

            writer.writePackageEnd(pkg);
        }

        writer.close();
    }

    static File getOwnerFile(ApplicationPackage pkg) {
        var owner = pkg.holder(Object.class);

        if (owner instanceof StarlightBukkitCore core) {
            return ((Starlight) core.getPlugin()).getFile();
        } else {
            return ((SLPluginHandle) owner).getFile();
        }
    }

    void writePackageHeader(ApplicationPackage pkg);

    void writeModuleInfo(ModuleContainer mod);

    void writePackageEnd(ApplicationPackage pkg);

    default void close() {
    }
}
