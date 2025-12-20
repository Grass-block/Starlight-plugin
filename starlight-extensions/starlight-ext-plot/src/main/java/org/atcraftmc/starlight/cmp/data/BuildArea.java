package org.atcraftmc.starlight.cmp.data;

import org.atcraftmc.starlight.core.objects.Region;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.joml.Vector3d;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class BuildArea extends Region {
    private Set<UUID> invited;
    private String type;
    private Vector3d spawn;

    public BuildArea(UUID owner, String name, World world, Location p0, Location p1) {
        super(owner, name, world, p0, p1);
    }

    public BuildArea(UUID owner, String name, String world, Vector3d p0, Vector3d p1) {
        super(owner, name, world, p0, p1);
    }

    public BuildArea(UUID uuid, UUID owner, String name, String world, Vector3d p0, Vector3d p1, BsonDocument extraMetadata) {
        super(uuid, owner, name, world, p0, p1, extraMetadata);
    }

    @Override
    public void serializeMetadata(BsonDocument metadata) {
        var inv = this.invited.stream().map((v) -> new BsonString(v.toString())).collect(Collectors.toList());

        metadata.append("invited", new BsonArray(inv));
        metadata.append("type", new BsonString(this.type));
        metadata.append("spawn_x", new BsonDouble(this.spawn.x));
        metadata.append("spawn_y", new BsonDouble(this.spawn.y));
        metadata.append("spawn_z", new BsonDouble(this.spawn.z));
    }

    @Override
    public void deserializeMetadata(BsonDocument metadata) {
        this.invited = metadata.getArray("invited").stream().map((b) -> UUID.fromString(b.toString())).collect(Collectors.toSet());
        this.type = metadata.getString("type").toString();
        this.spawn = new Vector3d(
                metadata.getNumber("spawn_x").doubleValue(),
                metadata.getNumber("spawn_y").doubleValue(),
                metadata.getNumber("spawn_z").doubleValue()
        );
    }

    public Location getSpawn() {
        return new Location(Bukkit.getWorld(this.world), this.spawn.x(), this.spawn.y(), this.spawn.z());
    }

    public String getType() {
        return type;
    }

    public Set<UUID> getInvited() {
        return invited;
    }
}
