package org.atcraftmc.starlight.core.view;

import org.atcraftmc.qlib.bukkit.task.TaskScheduler;
import org.atcraftmc.starlight.core.PlayerView;
import org.bukkit.entity.Player;

import java.util.function.Function;

public abstract class PlayerViewRenderer {
    final String id;
    private final int priority;

    public PlayerViewRenderer(String id, int priority) {
        this.id = id;
        this.priority = priority;
    }

    public static PlayerViewRenderer interval(String id, int priority, int interval, TaskScheduler target, PlayerView.ViewRenderer action) {
        return new TaskScheduledRenderer(id, priority, interval, target, action);
    }

    public static PlayerViewRenderer interval(String id, int priority, int interval, Function<Player, TaskScheduler> target, PlayerView.ViewRenderer action) {
        return new TaskScheduledRenderer(id, priority, interval, target, action);
    }


    public int priority() {
        return priority;
    }

    public abstract void active(Player player);

    public abstract void inactive(Player player);

    private static final class TaskScheduledRenderer extends PlayerViewRenderer {
        private final int interval;
        private final Function<Player, TaskScheduler> target;
        private final PlayerView.ViewRenderer renderer;

        private TaskScheduledRenderer(String id, int priority, int interval, TaskScheduler target, PlayerView.ViewRenderer action) {
            super(id, priority);
            this.interval = interval;
            this.target = (p) -> target;
            this.renderer = action;
        }

        private TaskScheduledRenderer(String id, int priority, int interval, Function<Player, TaskScheduler> target, PlayerView.ViewRenderer action) {
            super(id, priority);
            this.interval = interval;
            this.target = target;
            this.renderer = action;
        }

        @Override
        public void active(Player player) {
            this.target.apply(player).timer(this.id + "-" + player.getUniqueId(), 0, this.interval, (t) -> this.renderer.render(player, t));
        }

        @Override
        public void inactive(Player player) {
            this.target.apply(player).cancel(this.id + "-" + player.getUniqueId());
        }
    }
}
