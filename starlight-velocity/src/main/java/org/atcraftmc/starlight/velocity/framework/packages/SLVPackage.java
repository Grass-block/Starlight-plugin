package org.atcraftmc.starlight.velocity.framework.packages;

import me.gb2022.modular.pack.IPackage;
import org.atcraftmc.starlight.framework.SLService;
import org.atcraftmc.starlight.framework.module.SLModule;
import org.atcraftmc.starlight.framework.module.SLModuleHandle;
import org.atcraftmc.starlight.velocity.framework.SLVService;
import org.atcraftmc.starlight.velocity.framework.module.SLVModule;
import org.atcraftmc.starlight.velocity.framework.module.SLVModuleHandle;
import org.bukkit.plugin.Plugin;

public interface SLVPackage extends IPackage<SLVModule, SLVModuleHandle, SLVService> {
}
