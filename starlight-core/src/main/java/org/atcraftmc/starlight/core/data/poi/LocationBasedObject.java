package org.atcraftmc.starlight.core.data.poi;

import org.bukkit.Location;

import java.util.UUID;

public abstract class LocationBasedObject {
    protected UUID uuid;
    protected String name;
    protected String world;
    protected double x;
    protected double y;
    protected double z;
    protected String data;

    public LocationBasedObject(UUID uuid, String name, String world, double x, double y, double z, String data) {
        this.uuid = uuid;
        this.name = name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.data = data;
        this.deserializeData(data);
    }

    public abstract void deserializeData(String data);

    public abstract String serializeData();

    public void onTeleported(Location location) {
    }

    public void teleport(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.world = location.getWorld().getName();
        this.onTeleported(location);
    }

    public void setData(String data) {
        this.data = data;
        this.deserializeData(data);
    }

    public String getData() {
        this.data = this.serializeData();
        return data;
    }
}
