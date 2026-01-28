package org.atcraftmc.starlight.core.view.process;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class ViewRenderProcess {
    protected final UUID target;
    protected final String id;
    protected final int priority;

    protected ViewRenderProcess(UUID target, String id, int priority) {
        this.target = target;
        this.id = id;
        this.priority = priority;
    }

    public final void start() {
        this.active(Bukkit.getPlayer(this.target));
    }

    public final void stop() {
        this.inactive(Bukkit.getPlayer(this.target));
    }


    public abstract void active(Player player);

    public abstract void inactive(Player player);


    public String id() {
        return this.id;
    }

    public int priority() {
        return this.priority;
    }
}
