package org.atcraftmc.starlight.commands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.assertion.CommandAssertionException;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.starlight.foundation.command.CommandProvider;
import org.atcraftmc.starlight.foundation.command.ModuleCommand;
import org.atcraftmc.starlight.foundation.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.inject.Key;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;

@ApplicationModule(id = "world-edit-commands")
@CommandProvider({WorldEditCommands.MirrorCommand.class, WorldEditCommands.DrainWaterCommand.class, WorldEditCommands.FastBrushCommand.class, WorldEditCommands.BoxOutlineCommand.class})
public final class WorldEditCommands extends BukkitAbstractModule {
    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requirePlugin("WorldEdit");
    }

    @BukkitCommand(name = "/outline-box", permission = "-worldedit.region.outline")
    public static final class BoxOutlineCommand extends ModuleCommand<WorldEditCommands> {
        public static Pattern parsePattern(Player player, String input) throws Exception {
            BukkitPlayer wePlayer = BukkitAdapter.adapt(player);

            ParserContext context = new ParserContext();
            context.setActor(wePlayer);
            context.setWorld(wePlayer.getWorld());

            return WorldEdit.getInstance().getBlockFactory().parseFromInput(input, context);
        }

        public static void drawHollowBorder(Player player, Pattern pattern) throws Exception {

            var wePlayer = BukkitAdapter.adapt(player);

            var region = WorldEdit.getInstance().getSessionManager().get(wePlayer).getSelection(wePlayer.getWorld());
            var change = 0;

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(wePlayer.getWorld())) {

                var min = region.getMinimumPoint();
                var max = region.getMaximumPoint();

                var minX = min.getX();
                var maxX = max.getX();
                var minY = min.getY();
                var maxY = max.getY();
                var minZ = min.getZ();
                var maxZ = max.getZ();

                for (int x = minX; x <= maxX; x++) {
                    set(editSession, x, minY, minZ, pattern);
                    set(editSession, x, minY, maxZ, pattern);
                    set(editSession, x, maxY, minZ, pattern);
                    set(editSession, x, maxY, maxZ, pattern);
                    change += 4;
                }

                for (int y = minY; y <= maxY; y++) {
                    set(editSession, minX, y, minZ, pattern);
                    set(editSession, minX, y, maxZ, pattern);
                    set(editSession, maxX, y, minZ, pattern);
                    set(editSession, maxX, y, maxZ, pattern);
                    change += 4;
                }

                for (int z = minZ; z <= maxZ; z++) {
                    set(editSession, minX, minY, z, pattern);
                    set(editSession, minX, maxY, z, pattern);
                    set(editSession, maxX, minY, z, pattern);
                    set(editSession, maxX, maxY, z, pattern);
                    change += 4;
                }

                editSession.flushSession();

                wePlayer.printInfo(TranslatableComponent.of("worldedit.line.changed", TextComponent.of(change)));
            }
        }

        static void set(EditSession editSession, int x, int y, double z, Pattern pattern) throws MaxChangedBlocksException {
            editSession.setBlock(BlockVector3.at(x, y, z), pattern);
        }

        @Override
        public void suggest(CommandSuggestion suggestion) {
            var command = WorldEdit.getInstance().getPlatformManager().getPlatformCommandManager().getCommandManager();
            var conv = command.getConverter(Key.of(Pattern.class));
            var buf = suggestion.getBuffer();
            var iva = new SimpleIVA();

            iva.put(Actor.class, BukkitAdapter.adapt(suggestion.getSenderAsPlayer()));
            var b = buf.get(buf.size() - 1);

            conv.ifPresent((c) -> suggestion.suggest(0, c.getSuggestions(b, iva)));
        }

        @Override
        public void execute(CommandExecution context) {
            try {
                var player = context.requireSenderAsPlayer();
                var pattern = parsePattern(player, context.requireArgumentAt(0));

                drawHollowBorder(player, pattern);
            } catch (InputParseException e) {
                context.getSender().sendMessage(ChatColor.RED + e.getMessage());
            } catch (CommandAssertionException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static final class SimpleIVA extends HashMap<Class<?>, Object> implements InjectedValueAccess {

            @Override
            public <T> @NotNull Optional<T> injectedValue(@NotNull Key<T> key, @NotNull InjectedValueAccess injectedValueAccess) {
                return (Optional<T>) Optional.ofNullable(this.get(key.getTypeToken().getRawType()));
            }
        }
    }

    @BukkitCommand(name = "/mirror")
    public static final class MirrorCommand extends ModuleCommand<WorldEditCommands> {
        @Override
        public void execute(CommandExecution context) {
            var player = context.requireSenderAsPlayer();

            Bukkit.dispatchCommand(player, "/copy");
            Bukkit.dispatchCommand(player, "/flip");
            Bukkit.dispatchCommand(player, "/paste");
        }
    }

    @BukkitCommand(name = "/drain-water")
    public static final class DrainWaterCommand extends ModuleCommand<WorldEditCommands> {
        @Override
        public void execute(CommandExecution context) {
            var player = context.requireSenderAsPlayer();

            Bukkit.dispatchCommand(player, "/set ^[waterlogged=false]");
            Bukkit.dispatchCommand(player, "/replace water air");
        }
    }

    @BukkitCommand(name = "/fast-brash")
    public static final class FastBrushCommand extends ModuleCommand<WorldEditCommands> {
        public static final String[] TREES = {"acacia", "birch", "brownmushroom", "cherry", "chorusplant", "crimsonfungus", "darkoak", "jungle", "junglebush", "largeoak", "largespruce", "mangrove", "oak", "pine", "rand", "randbirch", "randjungle", "randmushroom", "randspruce", "redmushroom", "randspruce",  // Duplicate
                "shortjungle", "smalljungle", "spruce", "swamp", "tall_mangrove", "tall_birch", "tall_spruce", "warped_fungus"};

        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, TREES);
            suggestion.suggest(0, "grasses");
        }

        @Override
        public void execute(CommandExecution context) {
            var player = context.requireSenderAsPlayer();

            var command = switch (context.requireArgumentAt(0)) {
                case "acacia" -> "/br forest sphere 0.1 acacia";
                case "birch" -> "/br forest sphere 0.1 birch";
                case "brownmushroom" -> "/br forest sphere 0.1 brownmushroom";
                case "cherry" -> "/br forest sphere 0.1 cherry";
                case "chorusplant" -> "/br forest sphere 0.1 chorusplant";
                case "crimsonfungus" -> "/br forest sphere 0.1 crimsonfungus";
                case "darkoak" -> "/br forest sphere 0.1 darkoak";
                case "jungle" -> "/br forest sphere 0.1 jungle";
                case "junglebush" -> "/br forest sphere 0.1 junglebush";
                case "largeoak" -> "/br forest sphere 0.1 largeoak";
                case "largespruce" -> "/br forest sphere 0.1 largespruce";
                case "mangrove" -> "/br forest sphere 0.1 mangrove";
                case "oak" -> "/br forest sphere 0.1 oak";
                case "pine" -> "/br forest sphere 0.1 pine";
                case "rand" -> "/br forest sphere 0.1 rand";
                case "randbirch" -> "/br forest sphere 0.1 randbirch";
                case "randjungle" -> "/br forest sphere 0.1 randjungle";
                case "randmushroom" -> "/br forest sphere 0.1 randmushroom";
                case "randspruce" -> "/br forest sphere 0.1 randspruce";
                case "redmushroom" -> "/br forest sphere 0.1 redmushroom";
                case "shortjungle" -> "/br forest sphere 0.1 shortjungle";
                case "smalljungle" -> "/br forest sphere 0.1 smalljungle";
                case "spruce" -> "/br forest sphere 0.1 spruce";
                case "swamp" -> "/br forest sphere 0.1 swamp";
                case "tall_mangrove" -> "/br forest sphere 0.1 tall mangrove";
                case "tallbirch" -> "/br forest sphere 0.1 tallbirch";
                case "tallspruce" -> "/br forest sphere 0.1 tallspruce";
                case "warpedfungus" -> "/br forest sphere 0.1 warpedfungus";
                case "grasses" -> "/br apply sphere 0.1 item bone_meal";
                default -> throw new IllegalStateException("Unexpected value: " + context.requireArgumentAt(0));
            };

            Bukkit.dispatchCommand(player, command);
        }
    }
}
