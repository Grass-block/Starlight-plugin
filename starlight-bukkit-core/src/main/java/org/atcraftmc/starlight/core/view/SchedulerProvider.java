package org.atcraftmc.starlight.core.view;

import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.bukkit.task.TaskScheduler;
import org.bukkit.entity.Player;

import java.util.function.Function;

public interface SchedulerProvider extends Function<Player, TaskScheduler> {
    SchedulerProvider ENTITY = entity -> QLib.task().entity(entity);
    SchedulerProvider GLOBAL = (p) -> QLib.task().global();
    SchedulerProvider ASYNC = (p) -> QLib.task().async();
}
