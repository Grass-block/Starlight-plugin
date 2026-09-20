package org.atcraftmc.starlight.core.view;

import net.kyori.adventure.text.Component;
import org.atcraftmc.qlib.bukkit.task.Task;
import org.atcraftmc.starlight.core.TextSender;
import org.atcraftmc.starlight.core.VisualScoreboardService;
import org.atcraftmc.starlight.util.InvalidPlayerHandleException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public final class PlayerView {
    public static final Set<String> CHANNELS = new HashSet<>();//temp impl
    private final UUID pointer;
    private PlayerUISetting setting = new PlayerUISetting();

    private final Map<String, PlayerViewChannelRenderer> renderers = new HashMap<>();


    public Map<String, PlayerViewChannelRenderer> getRenderChannels() {
        return renderers;
    }

    public PlayerView(Player pointer) {
        this.pointer = pointer.getUniqueId();
    }

    public void destroy() {
        for (var r:this.renderers.values()) {
            r.destroy();
        }
    }

    public void update() {
        for (var r:this.renderers.values()) {
            r.update();
        }
    }

    public PlayerViewChannelRenderer getScoreboard() {
        return this.renderers.get("starlight:scoreboard-sidebar");
    }

    public PlayerViewChannelRenderer getActionbar() {
        return this.renderers.get("starlight:action-bar");
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
