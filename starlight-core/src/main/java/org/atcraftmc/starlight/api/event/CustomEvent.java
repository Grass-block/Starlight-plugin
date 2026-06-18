package org.atcraftmc.starlight.api.event;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class CustomEvent extends Event {
    public static final Map<Class<? extends CustomEvent>, HandlerList> HANDLER_LISTS = new HashMap<>();

    protected CustomEvent() {
        super(true);

        getHandlers();

        var annotation = this.getClass().getAnnotation(BukkitEvent.class);
        var async = annotation.async();

        if (async) {
            return;
        }

        try {
            Field f = Event.class.getDeclaredField("async");
            f.setAccessible(true);
            f.set(this, false);
        } catch (Exception e) {
            try {
                Field f = Event.class.getDeclaredField("isAsync");
                f.setAccessible(true);
                f.set(this, false);
            } catch (Exception ignored) {
            }
        }
    }

    public static HandlerList getHandlerList(Class<? extends CustomEvent> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return null;
        }
        if (!HANDLER_LISTS.containsKey(clazz)) {
            HANDLER_LISTS.put(clazz, new HandlerList());
        }
        return HANDLER_LISTS.get(clazz);
    }

    @Override
    public final @NotNull HandlerList getHandlers() {
        return Objects.requireNonNull(getHandlerList(this.getClass()));
    }
}
