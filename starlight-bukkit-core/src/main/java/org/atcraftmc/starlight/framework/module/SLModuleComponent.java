package org.atcraftmc.starlight.framework.module;

import me.gb2022.gluon.module.component.SubComponent;
import org.bukkit.event.Listener;

public abstract class SLModuleComponent<E extends BukkitModule> extends SubComponent<E> implements Listener {
}
