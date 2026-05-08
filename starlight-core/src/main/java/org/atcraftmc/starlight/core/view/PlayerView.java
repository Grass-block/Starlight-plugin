package org.atcraftmc.starlight.core.view;

import net.kyori.adventure.text.Component;
import org.atcraftmc.qlib.bukkit.task.Task;
import org.atcraftmc.starlight.api.event.PlayerViewInitEvent;
import org.atcraftmc.starlight.core.VisualScoreboardService;
import org.atcraftmc.starlight.core.TextSender;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.atcraftmc.starlight.util.InvalidPlayerHandleException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public final class PlayerView {
    public static final Set<String> CHANNELS = new HashSet<>();//temp impl
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

}
