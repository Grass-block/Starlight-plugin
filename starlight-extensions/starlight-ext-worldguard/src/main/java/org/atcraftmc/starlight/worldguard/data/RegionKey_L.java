package org.atcraftmc.starlight.worldguard.data;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class RegionKey_L {
    private final World world;
    private final String id;

    public RegionKey_L(World world, String id) {
        this.world = world;
        this.id = id;
    }

    public static RegionKey_L of(World world, String id) {
        return new RegionKey_L(world, id);
    }

    public static RegionKey_L of(Player player, String id) {
        return new RegionKey_L(player.getWorld(), id);
    }

    public static RegionKey_L of(World world, ProtectedRegion region) {
        return new RegionKey_L(world, region.getId());
    }

    public World world() {
        return world;
    }

    public String id() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RegionKey_L k)) {
            return false;
        }

        return id.equals(k.id) && world.equals(k.world);
    }

    @Override
    public String toString() {
        return world.getName() + ":" + id;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
