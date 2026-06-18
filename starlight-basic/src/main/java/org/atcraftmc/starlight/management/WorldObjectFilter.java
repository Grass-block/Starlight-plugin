package org.atcraftmc.starlight.management;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.Language;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.core.command.CommandProvider;
import org.atcraftmc.starlight.core.command.ModuleCommand;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.shared.jdbc.JDBCData;
import org.atcraftmc.starlight.shared.jdbc.document.DocumentField;
import org.atcraftmc.starlight.util.SingleNotificationContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@ApplicationModule(id = "world-object-filter", description = "Hide or filter contents on world objects.", defaultEnable = false)
@CommandProvider(WorldObjectFilter.FilterCommand.class)
@AutoRegister(Registrations.SERVER_EVENT)
public final class WorldObjectFilter extends BukkitAbstractModule {
    public static final DocumentField<Boolean> FILTER_FLAGS = DocumentField.bool("world-filter:flags", true);
    public static final DocumentField<Boolean> FILTER_SIGNS = DocumentField.bool("world-filter:signs", false);

    private final TileEntityPacketHandler tileEntityPacketHandler = new TileEntityPacketHandler(this);
    private final ChunkDataPacketHandler chunkDataPacketHandler = new ChunkDataPacketHandler(this);
    private final SingleNotificationContainer notificationContainer = new SingleNotificationContainer();

    @Override
    public void enable() throws Exception {
        ProtocolLibrary.getProtocolManager().addPacketListener(this.tileEntityPacketHandler);
        ProtocolLibrary.getProtocolManager().addPacketListener(this.chunkDataPacketHandler);
    }

    @Override
    public void disable() throws Exception {
        ProtocolLibrary.getProtocolManager().removePacketListener(this.tileEntityPacketHandler);
        ProtocolLibrary.getProtocolManager().removePacketListener(this.chunkDataPacketHandler);
    }

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requirePlugin("ProtocolLib");
    }

    public void notifyPlayer(Player player) {
        var audience = QLib.audience(player);
        var b1 = FILTER_FLAGS.get(JDBCData.PLAYER_SHARED, player.getUniqueId());
        var b2 = FILTER_SIGNS.get(JDBCData.PLAYER_SHARED, player.getUniqueId());

        var loc = audience.pointed().locale();

        var temp = this.config().value("notify-gui").list(String.class);
        var template = String.join("\n", temp);
        var ui = Language.format(this.language().inline(template, loc), b1, b2);

        audience.sendMessage(QLib.textBuilder().buildComponent(ui));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        QLib.task().entity(event.getPlayer()).delay(10, () -> notifyPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        var type = event.getBlock().getType();

        if (type == Material.RED_BANNER || type == Material.WHITE_BANNER || type == Material.BLUE_BANNER) {
            if (!FILTER_FLAGS.get(JDBCData.PLAYER_SHARED, event.getPlayer().getUniqueId())) {
                return;
            }

            if (!this.notificationContainer.notify(event.getPlayer(), "flag")) {
                return;
            }

            language().item("action-flag-hint").send(QLib.audience(event.getPlayer()));
        }
    }

    @EventHandler
    public void onPlayerSignChange(SignChangeEvent event) {
        if (FILTER_SIGNS.get(JDBCData.PLAYER_SHARED, event.getPlayer().getUniqueId())) {
            return;
        }

        if (!this.notificationContainer.notify(event.getPlayer(), "sign")) {
            return;
        }

        language().item("action-sign-hint").send(QLib.audience(event.getPlayer()));
    }

    public void handleBlockEntityData(Player player, Location location, NbtCompound tag) {
        var block = location.getBlock();
        var type = block.getType();

        if ((type == Material.RED_BANNER || type == Material.WHITE_BANNER || type == Material.BLUE_BANNER) || (type == Material.RED_WALL_BANNER || type == Material.WHITE_WALL_BANNER || type == Material.BLUE_WALL_BANNER)) {
            if (!FILTER_FLAGS.get(JDBCData.PLAYER_SHARED, player.getUniqueId())) {
                return;
            }

            if (tag.containsKey("Patterns")) {
                tag.put("Patterns", NbtFactory.ofList("Patterns"));
            }
            if (tag.containsKey("patterns")) {
                tag.put("patterns", NbtFactory.ofList("patterns"));
            }
            return;
        }

        if (type.getKey().asString().contains("_sign")) {
            if (!FILTER_SIGNS.get(JDBCData.PLAYER_SHARED, player.getUniqueId())) {
                return;
            }

            if (tag.containsKey("BackText")) {
                tag.put("BackText", NbtFactory.ofCompound("BackText"));
            }
            if (tag.containsKey("back_text")) {
                tag.put("back_text", NbtFactory.ofCompound("back_text"));
            }
            if (tag.containsKey("FrontText")) {
                tag.put("FrontText", NbtFactory.ofCompound("FrontText"));
            }
            if (tag.containsKey("front_text")) {
                tag.put("front_text", NbtFactory.ofCompound("front_text"));
            }

            if (tag.containsKey("Text4")) {
                tag.put("Text4", NbtFactory.of("Text4", ""));
            }
            if (tag.containsKey("Text3")) {
                tag.put("Text3", NbtFactory.of("Text3", ""));
            }
            if (tag.containsKey("Text2")) {
                tag.put("Text2", NbtFactory.of("Text2", ""));
            }
            if (tag.containsKey("Text1")) {
                tag.put("Text1", NbtFactory.of("Text1", ""));
            }
        }
    }


    private static final class TileEntityPacketHandler extends PacketAdapter {
        private final WorldObjectFilter handle;

        public TileEntityPacketHandler(WorldObjectFilter handle) {
            super(Starlight.instance(), ListenerPriority.HIGHEST, PacketType.Play.Server.TILE_ENTITY_DATA);
            this.handle = handle;
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            if (event.isAsync()) {
                return;
            }

            var packet = event.getPacket();
            var loc = packet.getBlockPositionModifier().read(0);
            var tag = (NbtCompound) packet.getNbtModifier().read(0);
            var location = loc.toLocation(event.getPlayer().getWorld());

            if (tag == null) {
                return;
            }

            this.handle.handleBlockEntityData(event.getPlayer(), location, tag);

            packet.getNbtModifier().write(0, tag);
        }
    }

    private static final class ChunkDataPacketHandler extends PacketAdapter {
        private final WorldObjectFilter handle;

        public ChunkDataPacketHandler(WorldObjectFilter handle) {
            super(Starlight.instance(), ListenerPriority.HIGHEST, PacketType.Play.Server.MAP_CHUNK);
            this.handle = handle;
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            var packet = event.getPacket();
            var cx = packet.getIntegers().read(0);
            var cz = packet.getIntegers().read(1);
            var chunkData = packet.getLevelChunkData().read(0);

            chunkData.getBlockEntityInfo().forEach((i) -> {
                var addition = i.getAdditionalData();

                if (addition == null) {
                    return;
                }

                var wx = cx * 16 + i.getSectionX();
                var wz = cz * 16 + i.getSectionZ();
                var location = new Location(event.getPlayer().getWorld(), wx, i.getY(), wz);

                this.handle.handleBlockEntityData(event.getPlayer(), location, addition);

                i.setAdditionalData(addition);
            });

            packet.getLevelChunkData().write(0, chunkData);
        }
    }

    @BukkitCommand(name = "world-filter", permission = "+starlight.management.worldfilter")
    public static final class FilterCommand extends ModuleCommand<WorldObjectFilter> {
        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, "flags", "signs");
        }

        @Override
        public void execute(CommandExecution context) {
            var uuid = context.requireSenderAsPlayer().getUniqueId();
            var audience = QLib.audience(context.requireSenderAsPlayer());

            switch (context.requireEnum(0, "flags", "signs")) {
                case "flags" -> {
                    var prev = FILTER_FLAGS.get(JDBCData.PLAYER_SHARED, uuid);

                    FILTER_FLAGS.set(JDBCData.PLAYER_SHARED, uuid, !prev);

                    if (prev) {
                        getLanguage().item("flag-disable").send(audience);
                    } else {
                        getLanguage().item("flag-enable").send(audience);
                    }
                }
                case "signs" -> {
                    var prev = FILTER_SIGNS.get(JDBCData.PLAYER_SHARED, uuid);

                    FILTER_SIGNS.set(JDBCData.PLAYER_SHARED, uuid, !prev);

                    if (prev) {
                        getLanguage().item("sign-disable").send(audience);
                    } else {
                        getLanguage().item("sign-enable").send(audience);
                    }
                }
            }
        }
    }
}
