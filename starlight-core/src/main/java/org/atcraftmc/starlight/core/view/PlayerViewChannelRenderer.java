package org.atcraftmc.starlight.core.view;

import org.atcraftmc.qlib.bukkit.task.Task;
import org.atcraftmc.qlib.bukkit.task.TaskScheduler;
import org.atcraftmc.starlight.core.PlayerView;
import org.atcraftmc.starlight.core.TaskService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PlayerViewChannelRenderer {
    private final PlayerView holder;
    private final Map<String, PlayerViewRenderer> renderers = new HashMap<>();

    private boolean rejectAll;
    private Task currentTask;

    public PlayerViewChannelRenderer(PlayerView holder) {
        this.holder = holder;
    }

    private void select() {
        if (this.currentTask != null) {
            this.currentTask.cancel();
        }

        if (this.rejectAll) {
            return;
        }

        var list = new ArrayList<>(this.renderers.values());

        if (list.isEmpty()) {
            return;
        }

        list.sort((o1, o2) -> {
            if (o1 == o2) {
                return 0;
            }

            int pri = -Comparator.comparingInt(PlayerViewRenderer::priority).compare(o1, o2);

            if (pri != 0) {
                return pri;
            }

            return Comparator.comparingInt(Object::hashCode).compare(o1, o2);
        });

        var selected = list.get(0);

        /*

        this.currentTask = selected.scheduler().apply(this.holder.pointer()).timer(1, selected.interval(), (t) -> {
            if (this.holder.isChannelRejected(selected.id())) {
                return;
            }

            selected.renderer().render(this.holder.pointer(), t);
        });

         */
    }

    public void rejectAll(boolean enable) {
        this.rejectAll = enable;
    }
}