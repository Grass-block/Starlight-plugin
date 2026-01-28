package org.atcraftmc.starlight.core.view.process;

import org.atcraftmc.qlib.bukkit.task.Task;
import org.atcraftmc.qlib.bukkit.task.TaskScheduler;
import org.atcraftmc.starlight.core.view.ViewRendererCallback;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Function;

public final class TaskScheduleProcess extends ViewRenderProcess {
    private final int interval;
    private final Function<Player, TaskScheduler> target;
    private final ViewRendererCallback renderer;
    private Task task;

    public TaskScheduleProcess(UUID player, String id, int priority, int interval, Function<Player, TaskScheduler> target, ViewRendererCallback renderer) {
        super(player, id, priority);
        this.interval = interval;
        this.target = target;
        this.renderer = renderer;
    }

    @Override
    public void active(Player player) {
        if (this.task != null) {
            this.task.cancel();
        }
        this.task = this.target.apply(player).timer(
                this.id + "-" + player.getUniqueId(),
                0,
                this.interval,
                (t) -> this.renderer.render(player, t)
        );
    }

    @Override
    public void inactive(Player player) {
        if (this.task != null) {
            this.task.cancel();
        }
    }
}
