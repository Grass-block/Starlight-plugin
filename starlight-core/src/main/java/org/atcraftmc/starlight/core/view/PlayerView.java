package org.atcraftmc.starlight.core.view;

import net.kyori.adventure.text.Component;
import org.atcraftmc.qlib.bukkit.task.Task;
import org.atcraftmc.qlib.bukkit.task.TaskScheduler;
import org.atcraftmc.starlight.api.event.PlayerViewInitEvent;
import org.atcraftmc.starlight.core.TaskService;
import org.atcraftmc.starlight.core.VisualScoreboardService;
import org.atcraftmc.starlight.foundation.TextSender;
import org.atcraftmc.starlight.foundation.platform.BukkitUtil;
import org.atcraftmc.starlight.util.InvalidPlayerHandleException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

public final class PlayerView {
    public static final Set<String> CHANNELS = new HashSet<>();//temp impl
    private final ChannelRenderer actionbar = new ChannelRenderer(this);
    private final UUID pointer;
    private final PlayerViewChannelRenderer scoreboard;
    private final PlayerViewChannelRenderer actionbar_v2;
    private PlayerUISetting setting = new PlayerUISetting();

    public PlayerView(Player pointer) {
        this.pointer = pointer.getUniqueId();

        this.scoreboard = new PlayerViewChannelRenderer("starlight:scoreboard", this);
        this.actionbar_v2 = new PlayerViewChannelRenderer("starlight:action-bar", this);

        BukkitUtil.callEvent(new PlayerViewInitEvent(pointer, this), (e) -> {
            var setting = e.getSetting();

            if (setting != null) {
                sync(setting);
            }
        });

        this.scoreboard.setCleanupAction((p) -> VisualScoreboardService.instance().visualScoreboard(pointer).stopSidebarRendering());
    }

    public void destroy() {
        this.scoreboard.destroy();
        this.actionbar_v2.destroy();
    }

    public void update() {
        this.scoreboard.update();
        this.actionbar_v2.update();
    }

    public PlayerViewChannelRenderer getScoreboard() {
        return scoreboard;
    }

    public PlayerViewChannelRenderer getActionbar_v2() {
        return actionbar_v2;
    }

    public Player pointer() {
        var p = Bukkit.getPlayer(pointer);

        if (p == null) {
            throw new InvalidPlayerHandleException(pointer);
        }

        return p;
    }

    public boolean isChannelRejected(String source) {
        return this.setting.isChannelRejected(source);
    }

    public void sendMessage(String channel, Component message) {
        if (isChannelRejected(channel)) {
            return;
        }

        TextSender.sendMessage(this.pointer(), message);
    }

    public ChannelRenderer getActionbar() {
        return actionbar;
    }

    public boolean isRendererRejected(String id) {
        return this.setting.isRendererRejected(id) || this.setting.isRejectAllChannels();
    }

    public void sync(PlayerUISetting setting) {
        this.setting = new PlayerUISetting(setting);
        this.update();
    }


    public interface ViewRenderer {
        void render(Player player, Task context);
    }

    public record GeneratedRendererRecord(String id, int priority, int interval, ViewRenderer renderer,
                                          Function<Player, TaskScheduler> scheduler) {
    }

    public static final class ChannelRenderer {
        private final Map<String, GeneratedRendererRecord> renderers = new HashMap<>();
        private final PlayerView holder;
        private boolean rejectAll = false;
        private Task currentTask;

        public ChannelRenderer(PlayerView holder) {
            this.holder = holder;
        }

        public void addChannel(String id, int priority, int interval, TaskScheduler target, ViewRenderer action) {
            if (this.rejectAll) {
                return;
            }

            this.renderers.put(id, new GeneratedRendererRecord(id, priority, interval, action, (p) -> target));
            this.select();
        }

        public void addChannel(String id, int priority, int interval, Function<Player, TaskScheduler> target, ViewRenderer action) {
            if (this.rejectAll) {
                return;
            }

            this.renderers.put(id, new GeneratedRendererRecord(id, priority, interval, action, target));
            this.select();
        }

        public void addChannel(String id, int priority, int interval, ViewRenderer action) {
            if (this.rejectAll) {
                return;
            }

            addChannel(id, priority, interval, TaskService.global(), action);
        }

        public void removeChannel(String id) {
            this.renderers.remove(id);
            this.select();
        }

        public GeneratedRendererRecord getChannel(String id) {
            return this.renderers.get(id);
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

                int pri = -Comparator.<GeneratedRendererRecord>comparingInt((o) -> o.priority).compare(o1, o2);

                if (pri != 0) {
                    return pri;
                }

                return Comparator.comparingInt(Object::hashCode).compare(o1, o2);
            });

            var selected = list.get(0);

            this.currentTask = selected.scheduler().apply(this.holder.pointer()).timer(1, selected.interval(), (t) -> {
                if (this.holder.isChannelRejected(selected.id())) {
                    return;
                }

                try {
                    selected.renderer().render(this.holder.pointer(), t);
                } catch (NullPointerException e) {
                    t.cancel();
                }
            });
        }

        public void rejectAll(boolean enable) {
            this.rejectAll = enable;
        }
    }
}
