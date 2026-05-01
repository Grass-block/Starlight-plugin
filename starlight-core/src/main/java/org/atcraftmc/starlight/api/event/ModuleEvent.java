package org.atcraftmc.starlight.api.event;

import me.gb2022.gluon.ObjectOperationResult;
import me.gb2022.gluon.module.ModuleContainer;
import org.atcraftmc.starlight.core.event.CustomEvent;
import org.atcraftmc.starlight.core.event.BukkitEvent;
import org.bukkit.event.HandlerList;

public abstract class ModuleEvent extends CustomEvent {
    private final ModuleContainer meta;

    protected ModuleEvent(ModuleContainer meta) {
        this.meta = meta;
    }

    public ModuleContainer getMeta() {
        return meta;
    }

    @BukkitEvent
    public static final class PreEnable extends ModuleEvent {
        public PreEnable(ModuleContainer meta) {
            super(meta);
        }

        public static HandlerList getHandlerList() {
            return getHandlerList(PreEnable.class);
        }
    }

    @BukkitEvent
    public static final class Enable extends ModuleEvent {
        private final ObjectOperationResult result;

        public Enable(ModuleContainer meta, ObjectOperationResult result) {
            super(meta);
            this.result = result;
        }

        public static HandlerList getHandlerList() {
            return getHandlerList(Enable.class);
        }

        public ObjectOperationResult getResult() {
            return result;
        }
    }

    @BukkitEvent
    public static final class PreDisable extends ModuleEvent {
        public PreDisable(ModuleContainer meta) {
            super(meta);
        }

        public static HandlerList getHandlerList() {
            return getHandlerList(PreDisable.class);
        }
    }

    @BukkitEvent
    public static final class Disable extends ModuleEvent {
        private final ObjectOperationResult result;

        public Disable(ModuleContainer meta, ObjectOperationResult result) {
            super(meta);
            this.result = result;
        }

        public static HandlerList getHandlerList() {
            return getHandlerList(Disable.class);
        }

        public ObjectOperationResult getResult() {
            return result;
        }
    }
}
