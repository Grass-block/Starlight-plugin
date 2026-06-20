package org.atcraftmc.starlight.worldguard.api;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.atcraftmc.starlight.shared.jdbc.document.DocumentField;
import org.atcraftmc.starlight.shared.jdbc.document.DocumentFieldCodec;
import org.atcraftmc.starlight.worldguard.WGExtraInfoServiceV2;
import org.bukkit.World;
import org.joml.Vector3d;

import java.util.Optional;

public interface WGSpawnAPI {
    double UNKNOWN_V = Double.NaN;
    Vector3d UNKNOWN_POS = new Vector3d(Double.NaN, Double.NaN, Double.NaN);
    DocumentField<Vector3d> SPAWN_LOCATION = DocumentField.custom(
            "custom-spawn",
            UNKNOWN_POS,
            DocumentFieldCodec.VECTOR_3D
    );
    DocumentField<Boolean> ALLOW_PUB_TELEPORT = DocumentField.bool("allow-tp", true);


    static boolean toggleAllowTP(RegionKey key) {
        var data = WGExtraInfoServiceV2.instance().getData(key);

        var previous = ALLOW_PUB_TELEPORT.get(data);
        var operated = !previous;

        ALLOW_PUB_TELEPORT.set(data, operated);

        return operated;
    }

    static boolean allowTP(RegionKey key) {
        var data = WGExtraInfoServiceV2.instance().getData(key);

        if (!ALLOW_PUB_TELEPORT.exist(data)) {
            return true;
        }

        return ALLOW_PUB_TELEPORT.get(data);
    }

    static boolean setRegionSpawnLocation(World world, ProtectedRegion region, Vector3d pos) {
        if (region.contains(BlockVector3.at(pos.x, pos.y, pos.z))) {
            return false;
        }

        var data = WGExtraInfoServiceV2.instance().getData(RegionKey.fromRegion(world, region));

        SPAWN_LOCATION.set(data, pos);

        return true;
    }

    static Optional<Vector3d> getSpawnLocation(RegionKey key) {
        var data = WGExtraInfoServiceV2.instance().getData(key);

        if (!ALLOW_PUB_TELEPORT.exist(data)) {
            return Optional.empty();
        }

        var d = SPAWN_LOCATION.get(data);

        if (Double.isNaN(d.x())) {
            return Optional.empty();
        }

        return Optional.of(d);
    }
}
