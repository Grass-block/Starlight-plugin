package org.atcraftmc.starlight.core;

import me.gb2022.commons.reflect.method.MethodHandle;
import me.gb2022.commons.reflect.method.MethodHandleO0;
import me.gb2022.commons.reflect.method.MethodHandleO2;
import me.gb2022.commons.reflect.method.MethodHandleO3;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import me.gb2022.gluon.service.ServiceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.foundation.ComponentSerializer;
import org.atcraftmc.starlight.foundation.platform.APIProfile;
import org.atcraftmc.starlight.foundation.platform.APIProfileTest;
import org.atcraftmc.starlight.foundation.platform.BukkitUtil;
import org.atcraftmc.starlight.foundation.platform.Compatibility;
import org.atcraftmc.starlight.framework.BukkitService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;

import java.util.*;

@ApplicationService(id = "visual-scoreboard")
public interface VisualScoreboardService extends BukkitService {
    @ServiceInject
    ServiceHolder<BukkitScoreboardService> INSTANCE = new ServiceHolder<>();

    @ServiceProvider
    static VisualScoreboardService create() {
        return new BukkitScoreboardService();
    }

    static VisualScoreboardService instance() {
        return INSTANCE.get();
    }

    VisualScoreboard visualScoreboard(Player player);

    interface VisualScoreboard {
        void mount();

        void renderSidebar(Component title, List<String> columns);

        void stopSidebarRendering();

        void setNameTag(Player target, Component prefix, Component postfix);

        void setTabColumn(Player target, int value, Component title);

        void clearTabColumn();

        default void destroy() {
        }
    }


    abstract class AbstractScoreboardService implements VisualScoreboardService {
        private final Map<UUID, VisualScoreboard> handles = new HashMap<>();

        public abstract VisualScoreboard create(UUID uuid);

        @Override
        public void enable() {
            for (var player : Bukkit.getOnlinePlayers()) {
                this.loadScoreboard(player);
            }

            BukkitUtil.registerEventListener(this);
        }

        @Override
        public void disable() {
            BukkitUtil.unregisterEventListener(this);

            for (var player : Bukkit.getOnlinePlayers()) {
                this.unloadScoreboard(player);
            }
        }

        @EventHandler
        public final void onPlayerJoin(PlayerJoinEvent event) {
            this.loadScoreboard(event.getPlayer());
        }

        @EventHandler
        public final void onPlayerQuit(PlayerQuitEvent event) {
            this.unloadScoreboard(event.getPlayer());
        }

        public final void loadScoreboard(Player player) {
            this.unloadScoreboard(player);

            var instance = create(player.getUniqueId());

            this.handles.put(player.getUniqueId(), instance);
            instance.mount();
        }

        public final void unloadScoreboard(Player player) {
            var instance = this.handles.get(player.getUniqueId());

            if (instance == null) {
                return;
            }

            instance.destroy();
        }

        @Override
        public VisualScoreboard visualScoreboard(Player player) {
            if (!this.handles.containsKey(player.getUniqueId())) {
                this.loadScoreboard(player);
            }

            return this.handles.get(player.getUniqueId());
        }
    }

    final class BukkitVisualScoreboard implements VisualScoreboard {
        public static final String CRITERIA = "sl-display-visual";

        public static final String BUFFER_1 = "sidebar-buffer1";
        public static final String BUFFER_2 = "sidebar-buffer2";
        public static final String PLAYER_LIST = "tab-buffer";
        private final Logger logger;
        private final UUID uuid;
        private Scoreboard scoreboard;

        public BukkitVisualScoreboard(UUID uuid) {
            this.logger = SLPluginEnvironment.createLogger("Scoreboard/" + uuid);
            this.uuid = uuid;
        }

        public Player handle() {
            return Bukkit.getPlayer(this.uuid);
        }

        @Override
        public void mount() {
            TaskService.entity(handle()).run(() -> {
                this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

                var player = Bukkit.getPlayer(this.uuid);
                if (player == null || !player.isOnline()) {
                    return;
                }

                var mgr = Bukkit.getScoreboardManager();
                var tempBoard = mgr.getNewScoreboard();

                bindScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                player.setScoreboard(tempBoard);
                player.setScoreboard(this.scoreboard);
            });
        }

        @Override
        public void destroy() {
            this.stopSidebarRendering();
        }


        private Objective getObjective(String name) {
            var builder = this.scoreboard.getObjective(name);

            if (builder == null) {
                builder = this.scoreboard.registerNewObjective(name, CRITERIA);
            }

            return builder;
        }

        private void bindScoreboard(Scoreboard scoreboard) {
            Optional.ofNullable(Bukkit.getPlayer(this.uuid)).ifPresent((p) -> p.setScoreboard(scoreboard));
        }

        private void setDisplayName(Objective objective, Component title) {
            if (APIProfileTest.isPaperCompat()) {
                objective.displayName(title.asComponent());
            } else {
                objective.setDisplayName(LegacyComponentSerializer.legacySection().serialize(title.asComponent()));
            }
        }

        private void build(Objective builder, Component title, List<String> columns) {
            setDisplayName(builder, title);
            var existing = new HashMap<String, Integer>();
            for (int i = 0; i < columns.size(); i++) {
                String column = columns.get(i);
                if (existing.containsKey(column)) {
                    int fix = existing.get(column);
                    existing.put(column, fix + 1);
                    column = column + " ".repeat(fix + 1);
                } else {
                    existing.put(column, 0);
                }
                builder.getScore(column).setScore(columns.size() - i);
            }
        }

        public Scoreboard getScoreboard() {
            return scoreboard;
        }

        public UUID getUuid() {
            return uuid;
        }

        @Override
        public void renderSidebar(Component title, List<String> columns) {
            TaskService.entity(handle()).run(() -> {
                var buffer1 = getObjective(BUFFER_1);
                var buffer2 = getObjective(BUFFER_2);

                Optional.ofNullable(Bukkit.getPlayer(this.uuid)).ifPresent((p) -> p.setScoreboard(this.scoreboard));

                if (buffer1.getDisplaySlot() == null) {
                    try {
                        buffer1.unregister();
                        buffer1 = getObjective(BUFFER_1);
                        this.build(buffer1, title, columns);
                        buffer1.setDisplaySlot(DisplaySlot.SIDEBAR);
                        buffer2.setDisplaySlot(null);
                    } catch (NullPointerException | IllegalStateException e) {
                        stopSidebarRendering();
                    }
                } else {
                    try {
                        buffer2.unregister();
                        buffer2 = getObjective(BUFFER_2);
                        this.build(buffer2, title, columns);
                        buffer2.setDisplaySlot(DisplaySlot.SIDEBAR);
                        buffer1.setDisplaySlot(null);
                    } catch (NullPointerException | IllegalStateException e) {
                        stopSidebarRendering();
                    }
                }
            });
        }

        @Override
        public void stopSidebarRendering() {
            TaskService.entity(handle()).run(() -> {
                this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
                this.scoreboard.getObjectives().forEach((o) -> {
                    try {
                        o.unregister();
                    } catch (Exception e) {
                        this.logger.warn("Failed to unregister {}: {}", o.getName(), e.getMessage());
                    }
                });

                bindScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                bindScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            });
        }

        @Override
        public synchronized void setNameTag(Player target, Component prefix, Component postfix) {
            TaskService.entity(handle()).run(() -> TeamAPI.SET_TEAM.invoke(this.scoreboard, target, prefix, postfix));
        }

        @Override
        public synchronized void setTabColumn(Player target, int value, Component title) {
            TaskService.entity(handle()).run(() -> {
                var tab = getObjective(PLAYER_LIST);
                tab.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                tab.getScore(target).setScore(value);
                setDisplayName(tab, title);
            });
        }

        @Override
        public synchronized void clearTabColumn() {
            TaskService.entity(handle()).run(() -> getObjective(PLAYER_LIST).unregister());
        }

        interface TeamAPI {
            MethodHandleO2<Team, Component, Component> TEAM_PREFIX = MethodHandle.select((ctx) -> {
                ctx.attempt(() -> Team.class.getMethod("prefix", Component.class), (t, c1, c2) -> {
                    t.prefix(c1);
                    t.suffix(c2);
                });
                ctx.dummy((t, c1, c2) -> {
                    t.setPrefix(ComponentSerializer.legacy(c1));
                    t.setSuffix(ComponentSerializer.legacy(c2));
                });
            });
            MethodHandleO0<Team> SET_NAME_TAG_VISIBILITY = MethodHandle.select((ctx) -> {
                ctx.attempt(() -> {
                    Class.forName("org.bukkit.scoreboard.Team.Option");
                    return null;
                }, (t) -> t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS));
                ctx.attempt(
                        () -> Team.class.getMethod("setNameTagVisibility", NameTagVisibility.class),
                        (t) -> t.setNameTagVisibility(NameTagVisibility.ALWAYS)
                );
                ctx.dummy((t) -> {});
            });
            @SuppressWarnings("Convert2MethodRef")
            MethodHandleO3<Scoreboard, Player, Component, Component> SET_TEAM = MethodHandle.select((ctx) -> {
                ctx.attempt(() -> {
                    Compatibility.blackListPlatform(APIProfile.FOLIA);
                    return Class.forName("org.bukkit.scoreboard.Team").getEnclosingMethod();
                }, (s, p, pr, po) -> set(s, p, pr, po));
                ctx.dummy((s, p, pr, po) -> {
                });
            });

            static void set(Scoreboard scoreboard, Player target, Component prefix, Component postfix) {
                var team = "sl@" + target.getName();
                var t = scoreboard.getTeam(team);

                if (t == null) {
                    t = scoreboard.registerNewTeam(team);
                }

                SET_NAME_TAG_VISIBILITY.invoke(t);
                TEAM_PREFIX.invoke(t, prefix, postfix);
                t.addPlayer(target);
            }
        }
    }

    final class BukkitScoreboardService extends AbstractScoreboardService {
        @Override
        public VisualScoreboard create(UUID uuid) {
            return new BukkitVisualScoreboard(uuid);
        }
    }
}
