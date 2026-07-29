package org.atcraftmc.starlight.oddities;

import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.core.GameTestService;
import org.atcraftmc.starlight.data.assets.AssetGroup;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@ApplicationModule(id = "custom-vehicle")
public class CustomVehicle extends BukkitAbstractModule {

    @Inject("display-models;false")
    public AssetGroup asset;

    static Transformation get(Vector3f position, Vector3f scale, Quaternionf rotation, Vector3f pivot) {
        return new Transformation(position, rotation, scale, new Quaternionf());
    }

    @Override
    public void enable() {
        QLib.task().global().timer("starlight:vehicle-sync", 1, 1, () -> {
            for (var world : Bukkit.getWorlds()) {
                for (var entity : world.getEntitiesByClasses(Boat.class)) {
                    var rx = entity.getLocation().getYaw();
                    var ry = entity.getLocation().getPitch();

                    for (var p : entity.getPassengers()) {
                        for (var pp : p.getPassengers()) {
                            if (pp instanceof Display d) {
                                pp.setRotation(rx, ry);
                                d.setInterpolationDelay(1);
                                d.setInterpolationDuration(1);
                            }
                        }
                    }
                }
            }
        });


        GameTestService.register("vehicle-test", () -> {
            var player = Bukkit.getOnlinePlayers().stream().findFirst().get();
            var world = Bukkit.getWorld("world");
            var location = player.getLocation().setDirection(new Vector(90, 0, 0));

            var root = world.spawnEntity(location, EntityType.BOAT);
            var mount = world.spawn(location, BlockDisplay.class);

            var models = CustomDisplayModel.create(location, this.asset.getFile("example-car.json"),new Vector3f(0,-0.15f,0));

            for (var model : models) {
                mount.addPassenger(model);
            }

            root.addPassenger(mount);
        });
    }

    @Override
    public void disable() throws Exception {
        QLib.task().global().cancel("starlight:vehicle-sync");
    }
}
