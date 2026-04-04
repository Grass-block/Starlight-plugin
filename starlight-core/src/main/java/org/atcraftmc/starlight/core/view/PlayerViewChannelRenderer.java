package org.atcraftmc.starlight.core.view;

import org.atcraftmc.starlight.core.view.process.TaskScheduleProcess;
import org.atcraftmc.starlight.core.view.process.ViewRenderProcess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class PlayerViewChannelRenderer {
    private final String id;
    private final Map<String, ViewRenderProcess> renderers = new HashMap<>();
    private final AtomicReference<ViewRenderProcess> current = new AtomicReference<>();
    private final PlayerView holder;
    private final UUID player;
    private Consumer<Player> cleanupAction = (p) -> {};

    public PlayerViewChannelRenderer(String id, PlayerView holder) {
        this.id = id;
        this.holder = holder;
        this.player = holder.pointer().getUniqueId();
    }

    public void destroy() {
        for (var id:new ArrayList<>(this.renderers.keySet())) {
            this.removeProcess(id);
        }
    }

    public void setCleanupAction(Consumer<Player> cleanupAction) {
        this.cleanupAction = cleanupAction;
    }

    public Optional<ViewRenderProcess> getCurrent() {
        return Optional.ofNullable(this.current.get());
    }

    public ViewRenderProcess registerProcess(String id, ViewRenderProcess process) {
        PlayerView.CHANNELS.add(id);
        this.renderers.put(id, process);
        this.update();

        return process;
    }

    public ViewRenderProcess registerIntervalProcess(String id, int priority, int interval, SchedulerProvider provider, ViewRendererCallback func) {
        return this.registerProcess(id, new TaskScheduleProcess(this.player, id, priority, interval, provider, func));
    }

    public void removeProcess(String s) {
        this.renderers.remove(s);
        this.update();
    }

    public PlayerView getHolder() {
        return holder;
    }

    public void update() {
        var previous = this.getCurrent();
        var player = Bukkit.getPlayer(this.player);

        if (previous.isPresent()) {
            previous.get().inactive(player);
            this.current.set(null);
        }


        if (this.getHolder().isRendererRejected(this.id)) {
            this.cleanupAction.accept(player);
            return;
        }

        var list = new ArrayList<>(this.renderers.values());

        list.removeIf((p) -> this.holder.isChannelRejected(p.id()));

        if (list.isEmpty()) {
            this.cleanupAction.accept(player);
            return;
        }

        list.sort((o1, o2) -> {
            if (o1 == o2) {
                return 0;
            }

            int pri = -Comparator.comparingInt(ViewRenderProcess::priority).compare(o1, o2);

            if (pri != 0) {
                return pri;
            }

            return Comparator.comparingInt(Object::hashCode).compare(o1, o2);
        });

        var selected = list.get(0);
        this.current.set(selected);

        selected.active(player);
    }
}