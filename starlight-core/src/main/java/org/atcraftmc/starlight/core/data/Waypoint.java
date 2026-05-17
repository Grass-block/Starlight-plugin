package org.atcraftmc.starlight.core.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.atcraftmc.starlight.data.jdbc.pojo.LegacyStringListHandler;
import org.atcraftmc.starlight.data.jdbc.pojo.LegacyUUIDTypeHandler;

import java.util.Set;
import java.util.UUID;

@TableName(value = "_waypoint_", autoResultMap = true)
public final class Waypoint {
    @TableId(type = IdType.INPUT)
    private String uuid;

    @TableField(typeHandler = LegacyUUIDTypeHandler.class)
    private UUID owner;

    private String name;
    private String world;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    @TableField(typeHandler = LegacyStringListHandler.class)
    private Set<String> allowed;

    public Waypoint() {}

    public Waypoint(UUID uuid, String name, String world, double x, double y, double z, float yaw, float pitch, UUID owner, Set<String> allowed) {
        this.uuid = uuid.toString();
        this.name = name;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.owner = owner;
        this.allowed = allowed;
    }

    // Getters and Setters
    public UUID getUuid() {
        return UUID.fromString(this.uuid);
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
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

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public UUID getOwner() {
        return owner;
    }

    public Set<String> getAllowed() {
        return allowed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setAllowed(Set<String> allowed) {
        this.allowed = allowed;
    }

    @Override
    public String toString() {
        return "Waypoint{" + "uuid=" + uuid + ", name='" + name + '\'' + ", world='" + world + '\'' + ", x=" + x + ", y=" + y + ", z=" + z + ", yaw=" + yaw + ", pitch=" + pitch + ", owner=" + owner + ", allowed=" + allowed + '}';
    }
}
