package org.atcraftmc.starlight.core.data.region;

import org.bson.BsonDocument;
import org.bukkit.Location;
import org.bukkit.World;
import org.joml.Vector3d;

import java.util.UUID;

/**
 * separated xyz coordinate is ONLY for acceleration.
 */
public class Region extends WorldAABB {
    private final UUID uuid;
    private final UUID owner;
    private final BsonDocument extraMetadata;

    private String name;

    /**
     * We don't care the position since we sort them correctly
     *
     * @param world world
     * @param p0    point1
     * @param p1    point2
     */
    public Region(UUID owner, String name, World world, Location p0, Location p1) {
        super(world.getName(), new Vector3d(p0.getX(), p0.getY(), p0.getZ()), new Vector3d(p1.getX(), p1.getY(), p1.getZ()));
        this.owner = owner;
        this.uuid = UUID.randomUUID();
        this.extraMetadata = new BsonDocument();
        this.deserializeMetadata(this.extraMetadata);
        this.name = name;
    }

    public Region(UUID owner, String name, String world, Vector3d p0, Vector3d p1) {
        super(world, p0, p1);

        this.owner = owner;
        this.uuid = UUID.randomUUID();
        this.extraMetadata = new BsonDocument();
        this.deserializeMetadata(this.extraMetadata);
        this.name = name;
    }

    public Region(String world, Vector3d p0, Vector3d p1) {
        super(world, p0, p1);

        this.owner = UUID.randomUUID();
        this.uuid = UUID.randomUUID();
        this.extraMetadata = new BsonDocument();
        this.deserializeMetadata(this.extraMetadata);
        this.name = "unnamed#" + this.uuid;
    }

    public Region(UUID uuid, UUID owner, String name, String world, Vector3d p0, Vector3d p1, BsonDocument extraMetadata) {
        super(world, p0, p1);

        this.owner = owner;
        this.extraMetadata = extraMetadata;
        this.deserializeMetadata(extraMetadata);
        this.uuid = uuid;
        this.name = name;
    }

    public void serializeMetadata(BsonDocument metadata) {
    }

    public void deserializeMetadata(BsonDocument metadata) {
    }

    public BsonDocument getExtraMetadata() {
        return extraMetadata;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder("Region{");
        sb.append("world=").append(this.world);
        sb.append(", x0=").append(this.x0);
        sb.append(", y0=").append(this.y0);
        sb.append(", z0=").append(this.z0);
        sb.append(", x1=").append(this.x1);
        sb.append(", y1=").append(this.y1);
        sb.append(", z1=").append(this.z1);
        sb.append('}');
        return sb.toString();
    }

    public UUID getOwner() {
        return this.owner;
    }
}
