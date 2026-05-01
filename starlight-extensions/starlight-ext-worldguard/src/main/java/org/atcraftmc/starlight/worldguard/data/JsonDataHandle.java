package org.atcraftmc.starlight.worldguard.data;

import com.google.gson.JsonObject;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.atcraftmc.starlight.util.AsyncLock;
import org.atcraftmc.starlight.worldguard.WorldGuardExtraInfoService;
import org.atcraftmc.starlight.worldguard.WorldGuardRegionService;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class JsonDataHandle {
    private final AsyncLock lock = new AsyncLock();
    private final AtomicInteger writeActivities = new AtomicInteger(0);
    private final WorldGuardExtraInfoService service;
    private final RegionKey id;
    private final JsonObject handle;
    private boolean dirty = false;
    private boolean valid = true;

    public JsonDataHandle(WorldGuardExtraInfoService service, RegionKey id, JsonObject handle) {
        this.service = service;
        this.id = id;
        this.handle = handle;
    }

    public void invalidate() {
        this.valid = false;
    }

    private void checkValid() {
        if(!this.valid) {
            throw new IllegalStateException("DataHandle is no longer valid");
        }
    }

    public Optional<ProtectedRegion> getTargetRegion() {
        return WorldGuardRegionService.getRegion(this.id);
    }

    public void flush() {
        this.service.suggestFlush(this.id);
    }

    public JsonObject getHandle() {
        return handle;
    }

    //string
    public void setString(String entry, String value) {
        checkValid();
        this.dirty = true;
        this.handle.addProperty(entry, value);
    }

    public void setStringAndFlush(String entry, String value) {
        checkValid();
        this.setString(entry, value);
        this.flush();
    }

    public String getString(String entry, String defaultValue) {
        checkValid();
        if (!this.handle.has(entry)) {
            setString(entry, defaultValue);
        }

        return this.handle.get(entry).getAsString();
    }


    //number
    public void setNumber(String entry, Number value) {
        this.dirty = true;
        this.handle.addProperty(entry, value);
    }

    public void setNumberAndFlush(String entry, Number value) {
        this.setNumber(entry, value);
        this.flush();
    }

    public Number getNumber(String entry, Number defaultValue) {
        if (!this.handle.has(entry)) {
            this.setNumber(entry, defaultValue);
        }

        return this.handle.get(entry).getAsNumber();
    }


    //component
    public void setComponent(String entry, Component value) {
        setString(entry, JSONComponentSerializer.json().serialize(value));
    }

    public void setComponentAndFlush(String entry, Component value) {
        setString(entry, JSONComponentSerializer.json().serialize(value));
        this.flush();
    }

    public Component getComponent(String entry, Component defaultValue) {
        if (!this.handle.has(entry)) {
            setComponent(entry, defaultValue);
        }

        return JSONComponentSerializer.json().deserializeOr(getString(entry, "{}"), defaultValue);
    }

    public boolean isFree() {
        return this.writeActivities.get() == 0;
    }

    public void waitUntilFree() {
        this.lock.monitor();
    }

    public void editSafe(Consumer<JsonDataHandle> action) {
        this.lock.pause();
        this.writeActivities.incrementAndGet();
        try {
            action.accept(this);
        } finally {
            this.writeActivities.decrementAndGet();
            if (this.isFree()) {
                this.lock.resume();
            }
        }
    }

    public boolean has(String s) {
        return this.handle.has(s);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
