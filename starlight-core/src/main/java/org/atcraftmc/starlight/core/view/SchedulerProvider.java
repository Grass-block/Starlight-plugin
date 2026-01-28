package org.atcraftmc.starlight.core.view;

import org.atcraftmc.qlib.bukkit.task.TaskScheduler;
import org.atcraftmc.starlight.core.TaskService;
import org.bukkit.entity.Player;

import java.util.function.Function;

public interface SchedulerProvider extends Function<Player, TaskScheduler> {
    SchedulerProvider ENTITY = TaskService::entity;
    SchedulerProvider GLOBAL = (p) -> TaskService.global();
    SchedulerProvider ASYNC = (p) -> TaskService.async();
}
