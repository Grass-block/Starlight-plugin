package org.atcraftmc.starlight.core.view;

import org.atcraftmc.starlight.core.PlayerView;
import org.atcraftmc.starlight.core.view.process.TaskScheduleProcess;
import org.atcraftmc.starlight.core.view.process.ViewRenderProcess;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class PlayerViewChannelRenderer {
    private final Map<String, ViewRenderProcess> renderers = new HashMap<>();
    private final AtomicReference<ViewRenderProcess> current = new AtomicReference<>();
    private final PlayerView holder;
    private final Set<String> rejects = new HashSet<>();
    private final UUID player;
    private boolean rejectAll = false;
    private Consumer<Player> cleanupAction = (p) -> {};


    public PlayerViewChannelRenderer(PlayerView holder) {
        this.holder = holder;
        this.player = holder.pointer().getUniqueId();//todo
    }

    public void setCleanupAction(Consumer<Player> cleanupAction) {
        this.cleanupAction = cleanupAction;
    }

    public Optional<ViewRenderProcess> getCurrent() {
        return Optional.ofNullable(this.current.get());
    }

    public ViewRenderProcess registerProcess(String id, ViewRenderProcess process) {
        this.renderers.put(id, process);
        this.update();

        return process;
    }

    public ViewRenderProcess registerIntervalProcess(String id, int priority, int interval, SchedulerProvider provider, ViewRendererCallback func) {
        return this.registerProcess(id, new TaskScheduleProcess(this.player, id, priority, interval, provider, func));
    }

    public void addReject(String id) {
        this.rejects.add(id);
    }

    public void removeReject(String id) {
        this.rejects.remove(id);
    }


    private void update() {
        var previous = this.getCurrent();
        var player = Bukkit.getPlayer(this.player);

        if (previous.isPresent()) {
            previous.get().inactive(player);
            this.current.set(null);
        }


        if (this.rejectAll) {
            this.cleanupAction.accept(player);
            return;
        }

        var list = new ArrayList<>(this.renderers.values());

        list.removeIf((p) -> this.rejects.contains(p.id()));

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

    public void rejectAll(boolean enable) {
        this.rejectAll = enable;
    }
}