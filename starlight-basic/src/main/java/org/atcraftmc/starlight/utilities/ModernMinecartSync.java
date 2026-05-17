package org.atcraftmc.starlight.utilities;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

@ApplicationModule(id = "modern-minecart-sync", description = "provide information to legacy clients of newer protocol.")
@AutoRegister(Registrations.SERVER_EVENT)
public final class ModernMinecartSync extends BukkitAbstractModule {
    private final EntityProtocolManager protocolManager = new EntityProtocolManager();

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requirePlugin("ViaVersion");
        try {
            ProtocolVersion.class.getDeclaredField("v1_20_5");
        } catch (NoSuchFieldException e) {
            throw new APIIncompatibleException("ViaVersion does not present V1_20_5.");
        }
    }

    private boolean isLegacy(Player player) {
        return Via.getAPI().getPlayerProtocolVersion(player).olderThan(ProtocolVersion.v1_20_5);
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        if (!(event.getVehicle() instanceof Minecart m)) {
            return;
        }

        QLib.task().entity(m).timer("starlight:minecart:legacy-sync", 1, 1, new MinecartTracker(this, m));
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Minecart m)) {
            return;
        }

        QLib.task().entity(m).timer("starlight:minecart:legacy-sync", 1, 1, new MinecartTracker(this, m));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Minecart m)) {
            return;
        }

        QLib.task().entity(m).cancel("starlight:minecart:legacy-sync");
    }

    private boolean isVisible(Player player, Entity entity) {
        if (!player.getWorld().equals(entity.getWorld())) {
            return false;
        }

        return player.getLocation().distanceSquared(entity.getLocation()) < 128 * 128;
    }

    private static final class EntityProtocolManager {
        private final ProtocolManager protocol = ProtocolLibrary.getProtocolManager();

        public void teleport(Player audience, Entity entity, Location location) {
            var packet = this.protocol.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);

            packet.getIntegers().write(0, entity.getEntityId());

            packet.getDoubles().write(0, location.getX());
            packet.getDoubles().write(1, location.getY());
            packet.getDoubles().write(2, location.getZ());

            packet.getBytes().write(0, (byte) (location.getYaw() * 256 / 360));
            packet.getBytes().write(1, (byte) (location.getPitch() * 256 / 360));

            packet.getBooleans().write(0, entity.isOnGround());

            this.protocol.sendServerPacket(audience, packet);
        }

        public void moveAndLook(Player audience, Entity entity, double dx, double dy, double dz, float yaw, float pitch) {
            var packet = this.protocol.createPacket(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);

            packet.getIntegers().write(0, entity.getEntityId());

            packet.getShorts().write(0, (short) (dx * 4096));
            packet.getShorts().write(1, (short) (dy * 4096));
            packet.getShorts().write(2, (short) (dz * 4096));

            packet.getBytes().write(0, (byte) (yaw * 256 / 360));
            packet.getBytes().write(1, (byte) (pitch * 256 / 360));

            packet.getBooleans().write(0, true);

            this.protocol.sendServerPacket(audience, packet);
        }

        public void velocity(Player audience, Entity entity, Vector speed) {
            var packet = this.protocol.createPacket(PacketType.Play.Server.ENTITY_VELOCITY);

            packet.getIntegers().write(0, entity.getEntityId());

            packet.getShorts().write(1, (short) (speed.getX() * 8000));
            packet.getShorts().write(2, (short) (speed.getY() * 8000));
            packet.getShorts().write(3, (short) (speed.getZ() * 8000));

            this.protocol.sendServerPacket(audience, packet);
        }
    }

    private static final class MinecartTracker implements Runnable {
        private final ModernMinecartSync handle;
        private final Minecart minecart;

        private Location previousLocation;

        private MinecartTracker(ModernMinecartSync handle, Minecart minecart) {
            this.handle = handle;
            this.minecart = minecart;
        }

        @Override
        public void run() {
            var cp = this.minecart.getLocation();

            if (this.previousLocation == null) {
                this.previousLocation = cp;
                return;
            }

            var lp = this.previousLocation;

            var audiences = new ArrayList<Player>();

            for (var player : Bukkit.getOnlinePlayers()) {
                if(!this.handle.isLegacy(player)){
                    continue;
                }


                if (!this.handle.isVisible(player, this.minecart)) {
                    continue;
                }

                audiences.add(player);
            }

            var dx = cp.getX() - lp.getX();
            var dy = cp.getY() - lp.getY();
            var dz = cp.getZ() - lp.getZ();

            if (dx >= 8 || dx <= -8 || dy >= 8 || dy <= -8 || dz >= 8 || dz <= -8) {
                for (var p : audiences) {
                    this.handle.protocolManager.teleport(p, this.minecart, cp);
                }
            } else {
                for (var p : audiences) {
                    this.handle.protocolManager.moveAndLook(p, this.minecart, dx, dy, dz, cp.getYaw(), cp.getPitch());
                }
            }

            for (var p : audiences) {
                //this.handle.protocolManager.velocity(p, this.minecart, this.minecart.getVelocity());
            }

            this.previousLocation = cp;
        }
    }
}
