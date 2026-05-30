package org.atcraftmc.starlight.commands;

import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.assertion.NumberLimitation;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.command.select.EntitySelector;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.SLCommandModule;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.function.Supplier;

@ApplicationModule(id = "entity-motion", description = "Controls entity motion with velocity commands")
@AutoRegister(Registrations.SERVER_EVENT)
@BukkitCommand(name = "motion", permission = "-starlight.command.motion")
public final class EntityMotion extends SLCommandModule {
    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requireMethod(() -> Location.class.getDeclaredMethod("getNearbyEntities", double.class, double.class, double.class));
    }

    @Override
    public void suggest(CommandSuggestion suggestion) {
        EntitySelector.tab(suggestion, 0);
        suggestion.suggest(1, "add", "set");
        suggestion.suggest(2, "[time]", "1", "5", "10", "direction", "time");

        suggestion.matchArgument(2, "time", (c) -> {
            c.suggest(3, "[time]", "1", "5", "10");
            c.suggest(4, "[x]", "0", "1.0", "-1.0");
            c.suggest(5, "[y]", "0", "1.0", "-1.0");
            c.suggest(6, "[z]", "0", "1.0", "-1.0");
        });

        suggestion.matchArgument(2, "direction", (c) -> {
            c.suggest(3, "[axis:dist]", "px:10", "pz:10", "ny:10");
            c.suggest(4, "[x]", "0", "1.0", "-1.0");
            c.suggest(5, "[y]", "0", "1.0", "-1.0");
            c.suggest(6, "[z]", "0", "1.0", "-1.0");
        });
    }

    @Override //motion <entity> 10 / reach
    public void execute(CommandExecution context) {
        var target = EntitySelector.selectEntity(context, 0);
        var mode = context.requireEnum(1, "add", "set");
        var add = mode.equals("add");

        var cond = context.requireArgumentAt(2);

        if (cond.matches("[0-9]+")) {
            Supplier<EndCondition> condition = () -> new EndCondition.Time(Integer.parseInt(cond));

            var x = context.requireArgumentDouble(3);
            var y = context.requireArgumentDouble(4);
            var z = context.requireArgumentDouble(5);

            emitCommand(context, target, mode, add, condition, x, y, z);
        } else {
            Supplier<EndCondition> condition = switch (context.requireEnum(2, "time", "direction")) {
                case "time" -> () -> new EndCondition.Time(context.requireArgumentInteger(3, NumberLimitation.moreThan(0)));
                case "direction" -> {
                    var arg = context.requireArgumentAt(3).split(":");

                    var face = switch (arg[0]) {
                        case "nx", "north", "x-" -> BlockFace.NORTH;
                        case "px", "south", "x+" -> BlockFace.SOUTH;
                        case "nz", "west", "z-" -> BlockFace.WEST;
                        case "pz", "east", "z+" -> BlockFace.EAST;
                        case "ny", "down", "y-" -> BlockFace.DOWN;
                        case "py", "up", "y+" -> BlockFace.UP;
                        default -> BlockFace.SELF;
                    };

                    yield () -> new EndCondition.ReachAxis(face, Double.parseDouble(arg[1]));
                }
                default -> null;
            };

            if (condition == null) {
                return;
            }

            var x = context.requireArgumentDouble(4);
            var y = context.requireArgumentDouble(5);
            var z = context.requireArgumentDouble(6);

            emitCommand(context, target, mode, add, condition, x, y, z);
        }
    }

    private void emitCommand(CommandExecution context, Collection<CommandSender> target, String mode, boolean add, Supplier<EndCondition> condition, double x, double y, double z) {
        var vector = new Vector(x, y, z);


        for (var p : target) {
            attempt((Entity) p, vector, add, condition);
        }

        if (!(context.getSender() instanceof Player)) {
            return;
        }
        MessageAccessor.send(this.language(), context.getSender(), "hint", target.size(), mode, x, y, z);
    }


    private void attempt(Entity e, Vector value, boolean add, Supplier<EndCondition> condition) {
        if (e.getVehicle() != null) {
            attempt(e.getVehicle(), value, add, condition);
        }

        var cond = condition.get();

        cond.init(e);

        QLib.task().entity(e).timer(1, 1, task -> {
            cond.tick();

            if (cond.reached()) {
                task.cancel();
                return;
            }

            e.setVelocity(add ? e.getVelocity().add(value) : value);
        });
    }

    interface EndCondition {
        void init(Entity entity);

        void tick();

        boolean reached();

        default void init() {
        }

        final class Time implements EndCondition {
            private final int tick;
            private int timer = 0;


            public Time(int tick) {
                this.tick = tick;
            }

            @Override
            public void init(Entity entity) {

            }

            @Override
            public void tick() {
                this.timer++;
            }

            @Override
            public boolean reached() {
                return this.timer > this.tick;
            }
        }

        final class ReachAxis implements EndCondition {
            private final BlockFace face;
            private final double distance;
            private Location start;
            private Entity entity;

            public ReachAxis(BlockFace face, double distance) {
                this.face = face;
                this.distance = distance;
            }

            @Override
            public void init(Entity entity) {
                this.entity = entity;
                this.start = entity.getLocation();
            }

            @Override
            public void tick() {

            }

            @Override
            public boolean reached() {
                switch (this.face) {
                    case UP -> {
                        return this.entity.getLocation().getY() > this.start.getY() + this.distance;
                    }
                    case DOWN -> {
                        return this.entity.getLocation().getY() < this.start.getY() - this.distance;
                    }
                    case NORTH -> {
                        return this.entity.getLocation().getZ() < this.start.getZ() - this.distance;
                    }
                    case SOUTH -> {
                        return this.entity.getLocation().getZ() > this.start.getZ() + this.distance;
                    }
                    case WEST -> {
                        return this.entity.getLocation().getX() < this.start.getX() - this.distance;
                    }
                    case EAST -> {
                        return this.entity.getLocation().getX() > this.start.getX() + this.distance;
                    }
                }

                return true;
            }
        }
    }
}
