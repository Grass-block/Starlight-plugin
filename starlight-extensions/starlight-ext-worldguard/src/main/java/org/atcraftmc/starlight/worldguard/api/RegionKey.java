package org.atcraftmc.starlight.worldguard.api;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.atcraftmc.starlight.worldguard.data.RegionKey_L;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class RegionKey {
    private final String worldId;
    private final String regionId;

    public RegionKey(String worldId, String regionId) {
        this.worldId = worldId;
        this.regionId = regionId;
    }

    public static RegionKey fromRegion(Player player, ProtectedRegion target) {
        return fromRegion(player.getWorld(), target);
    }

    public static RegionKey fromDatabaseId(String in) {
        if (!in.contains("::")) {
            throw new IllegalArgumentException("Invalid region ID: " + in);
        }

        var parts = in.split("::");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid region ID: " + in);
        }

        return new RegionKey(parts[0], parts[1]);
    }

    public static RegionKey fromSearchId(String in) {
        if (!in.contains("@")) {
            throw new IllegalArgumentException("Invalid region ID: " + in);
        }

        var parts = in.split("@");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid region ID: " + in);
        }

        return new RegionKey(parts[1], parts[0]);
    }

    public static RegionKey fromRegion(World world, ProtectedRegion region) {
        var wid = world.getName();
        var rid = region.getId();

        return new RegionKey(wid, rid);
    }

    public String getWorldId() {
        return worldId;
    }

    public String getRegionId() {
        return regionId;
    }

    public String toDatabaseId() {
        return this.worldId + "::" + this.regionId;
    }

    public String toSearchId() {
        return this.regionId + "@" + this.worldId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.worldId, this.regionId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RegionKey rk)) {
            return false;
        }

        return this.worldId.equals(rk.worldId) && this.regionId.equals(rk.regionId);
    }

    @Override
    public String toString() {
        return toDatabaseId();
    }

    @Override
    protected RegionKey clone() {
        return this;
    }

    public RegionKey_L legacy() {
        return new RegionKey_L(Bukkit.getWorld(this.worldId), this.regionId);
    }
}
