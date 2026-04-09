package org.atcraftmc.starlight.worldguard.data;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class RegionKey {
    private final World world;
    private final String id;

    public RegionKey(World world, String id) {
        this.world = world;
        this.id = id;
    }

    public static RegionKey of(World world, String id) {
        return new RegionKey(world, id);
    }

    public static RegionKey of(Player player, String id) {
        return new RegionKey(player.getWorld(), id);
    }

    public static RegionKey of(World world, ProtectedRegion region) {
        return new RegionKey(world, region.getId());
    }

    public World world() {
        return world;
    }

    public String id() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RegionKey k)) {
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
