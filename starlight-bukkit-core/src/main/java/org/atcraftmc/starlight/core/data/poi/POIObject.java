package org.atcraftmc.starlight.core.data.poi;

import com.google.gson.JsonObject;
import org.atcraftmc.starlight.util.UUIDMapped;
import org.bukkit.Location;

import java.util.UUID;

public abstract class POIObject implements UUIDMapped {
    protected UUID uuid;
    protected String name;
    protected String world;
    protected double x;
    protected double y;
    protected double z;

    public POIObject(UUID uuid, String name, String world, double x, double y, double z) {
        this.uuid = uuid;
        this.name = name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public abstract void deserializeData(JsonObject data);

    public abstract JsonObject serializeData();

    public abstract void create();

    public abstract void destroy();

    public void teleport(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.world = location.getWorld().getName();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    @Override
    public final UUID getUuid() {
        return this.uuid;
    }

    public final String getName() {
        return name;
    }
}
