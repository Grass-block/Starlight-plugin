package org.atcraftmc.starlight.framework.module;

import me.gb2022.modular.module.component.SubComponent;
import org.atcraftmc.starlight.framework.BukkitModule;
import org.bukkit.event.Listener;

public abstract class SLModuleComponent<E extends BukkitModule> extends SubComponent<E> implements Listener {
}
