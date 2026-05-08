package org.atcraftmc.starlight.core.data.region;

import me.gb2022.commons.math.AABB;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.joml.Vector3d;

public class WorldAABB {
    protected final String world;
    protected final Vector3d point0;
    protected final Vector3d point1;
    protected double x0;
    protected double y0;
    protected double z0;
    protected double x1;
    protected double y1;
    protected double z1;

    public WorldAABB(String world, Vector3d p0, Vector3d p1) {
        this.world = world;
        this.point0 = p0;
        this.point1 = p1;
        this.renderXYZ(p0, p1);
    }

    public void renderXYZ(Location p0, Location p1) {
        var xx0 = p0.getX();
        var yy0 = p0.getY();
        var zz0 = p0.getZ();
        var xx1 = p1.getX();
        var yy1 = p1.getY();
        var zz1 = p1.getZ();

        resize(xx0, yy0, zz0, xx1, yy1, zz1);
    }

    public void renderXYZ(Vector3d p0, Vector3d p1) {
        var xx0 = p0.x();
        var yy0 = p0.y();
        var zz0 = p0.z();
        var xx1 = p1.x();
        var yy1 = p1.y();
        var zz1 = p1.z();

        resize(xx0, yy0, zz0, xx1, yy1, zz1);
    }

    private void resize(double xx0, double yy0, double zz0, double xx1, double yy1, double zz1) {
        this.x0 = Math.min(xx0, xx1);
        this.y0 = Math.min(yy0, yy1);
        this.z0 = Math.min(zz0, zz1);
        this.x1 = Math.max(xx0, xx1);
        this.y1 = Math.max(yy0, yy1);
        this.z1 = Math.max(zz0, zz1);
    }

    public World getWorld() {
        return Bukkit.getWorld(this.world);
    }

    public Location getPoint0() {
        return new Location(getWorld(), this.point0.x(), this.point0.y(), this.point0.z());
    }

    public Location getPoint1() {
        return new Location(getWorld(), this.point1.x(), this.point1.y(), this.point1.z());
    }

    public void setPoint0(Location point0) {
        this.point0.set(point0.getX(), point0.getY(), point0.getZ());
        this.renderXYZ(this.point0, this.point1);
    }

    public void setPoint1(Location point1) {
        this.point1.set(point1.getX(), point1.getY(), point1.getZ());
        this.renderXYZ(this.point0, this.point1);
    }

    public AABB asAABB() {
        this.renderXYZ(this.point0, this.point1);
        return new AABB(this.x0, this.y0, this.z0, this.x1, this.y1, this.z1);
    }

    public Location getMinPoint() {
        return new Location(getWorld(), this.x0, this.y0, this.z0);
    }

    public Location getMaxPoint() {
        return new Location(getWorld(), this.x1, this.y1, this.z1);
    }

    public String getWorldId() {
        return this.world;
    }
}
