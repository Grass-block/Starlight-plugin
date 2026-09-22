package org.atcgroup.starlight.bundle.display;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.commons.nbt.NBTTagCompound;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.commons.reflect.method.MethodHandle;
import me.gb2022.commons.reflect.method.MethodHandleO1;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.core.ComponentSerializer;
import org.atcraftmc.starlight.core.command.CommandProvider;
import org.atcraftmc.starlight.core.command.ModuleCommand;
import org.atcraftmc.starlight.core.command.PluginCommandExecutor;
import org.atcraftmc.starlight.core.custom.CustomMeta;
import org.atcraftmc.starlight.core.data.poi.POIDataService;
import org.atcraftmc.starlight.core.data.poi.POIObject;
import org.atcraftmc.starlight.core.platform.BukkitCodec;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.atcraftmc.starlight.migration.QuarkDataImporter;
import org.atcraftmc.starlight.shared.JDBCService;
import org.atcraftmc.starlight.shared.jdbc.JDBCData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Nameable;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.joml.Vector3d;

import java.sql.SQLException;
import java.util.*;

@ApplicationModule(id = "hover-display", description = "Displays hover texts for items and entities")
@AutoRegister(Registrations.SERVER_EVENT)
@CommandProvider(HoverDisplay.HoverDisplayCommand.class)
public final class HoverDisplay extends BukkitAbstractModule implements PluginCommandExecutor {
    @SuppressWarnings("Convert2MethodRef")
    public static final MethodHandleO1<ArmorStand, Component> CUSTOM_NAME = MethodHandle.select((ctx) -> {
        ctx.attempt(() -> Nameable.class.getMethod("customName", Component.class), (p, c) -> p.customName(c));
        ctx.dummy((a, c) -> a.setCustomName(ComponentSerializer.legacy(c)));
    });

    private final HoverDisplayStorageService storage = new HoverDisplayStorageService();

    @Inject
    private Logger logger;

    @Inject
    private LanguageEntry language;

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requireClass(() -> Class.forName("org.bukkit.Nameable"));
        Compatibility.requireMethod(() -> Nameable.class.getDeclaredMethod("customName"));
    }

    @Override
    public void enable() {
        this.storage.initService(JDBCService.dataSource(JDBCData.SL_LOCAL));

        QLib.task().async().timer("hover-display:tick-timer", 0, 20, () -> this.storage.tick());

        QuarkDataImporter.registerModuleDataHandler("starlight:hover-text", QuarkDataImporter.HOVER_TEXT, (tag) -> {
            tag.getTagMap().forEach((k, v) -> {
                var location = BukkitCodec.location(((NBTTagCompound) v).getCompoundTag("location"));
                var texts = new ArrayList<Component>();

                if (!((NBTTagCompound) v).hasKey("texts")) {
                    texts.add(ComponentSerializer.json(((NBTTagCompound) v).getString("text")));
                } else {
                    var list = ((NBTTagCompound) v).getTagList("texts");

                    for (int i = 0; i < list.size(); i++) {
                        texts.add(ComponentSerializer.json(list.get(i).toString()));
                    }
                }

                create(k, location, texts);
            });
        });
    }

    @Override
    public void disable() {
        QLib.task().async().cancel("hover-display:tick-timer");
        QLib.task().global().run(this::clearAll);
        this.storage.cleanup();
    }

    public void clearAll() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {

                if (entity.getType() != EntityType.ARMOR_STAND) {
                    continue;
                }

                if (!CustomMeta.hasPDCIdentifier(entity)) {
                    continue;
                }

                if (!Objects.equals(CustomMeta.getPDCIdentifier(entity), "quark:hover-text")) {
                    continue;
                }

                entity.remove();
            }
        }
    }

    public void create(String id, Location loc, List<Component> text) {
        try {
            this.storage.add(new VirtualArmorStand(UUID.randomUUID(), id, loc, text));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(CommandExecution context) {
        var sender = context.requireSenderAsPlayer();

        var op = context.requireEnum(0, "create", "delete-all", "delete", "edit", "tp");

        if (Objects.equals(op, "delete-all")) {
            QLib.task().global().run(this::clearAll);
            MessageAccessor.send(this.language, sender, "delete-all");
            return;
        }

        var name = context.requireArgumentAt(1);

        try {
            if (Objects.equals(op, "create")) {
                if (this.storage.existName(name)) {
                    MessageAccessor.send(this.language, sender, "exist", name);
                    return;
                }
            } else if (!this.storage.existName(name)) {
                MessageAccessor.send(this.language, sender, "not-found", name);
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        switch (op) {
            case "create" -> {
                create(name, sender.getLocation().add(0, 1.37, 0), buildText(context));
                MessageAccessor.send(this.language, sender, "create", name);
            }
            case "delete" -> {
                try {
                    this.storage.delete(name);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                MessageAccessor.send(this.language, sender, "delete", name);
            }
            case "edit" -> {
                try {
                    var data = this.storage.byName(name).orElseThrow();
                    data.edit(buildText(context));
                    this.storage.update(data);
                    data.destroy();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                MessageAccessor.send(this.language, sender, "edit", name);
            }
            case "tp" -> {
                try {
                    this.storage.move(name, sender.getLocation().add(0, 1.37, 0));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                MessageAccessor.send(this.language, sender, "teleport", name);
            }
        }
    }

    private List<Component> buildText(CommandExecution context) {
        return Arrays.stream(context.requireRemainAsParagraph(2, true).split("\\{#return}")).map(QLib.textBuilder()::buildComponent).toList();
    }

    @Override
    public void suggest(CommandSuggestion suggestion) {
        suggestion.suggest(0, "create", "delete", "edit", "tp");
        try {
            suggestion.suggest(1, this.storage.listNames());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BukkitCommand(name = "hover-display", permission = "-starlight.hoverdisplay")
    public static final class HoverDisplayCommand extends ModuleCommand<HoverDisplay> {
        @Override
        public void init(HoverDisplay module) {
            this.setExecutor(module);
        }
    }

    public static final class VirtualArmorStand extends POIObject {
        private final List<Component> components = new ArrayList<>(16);
        private final Set<ArmorStand> handles = new HashSet<>();

        public VirtualArmorStand(UUID uuid, String name, String world, double x, double y, double z) {
            super(uuid, name, world, x, y, z);
        }

        public VirtualArmorStand(UUID uuid, String id, Location loc, List<Component> text) {
            super(uuid, id, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ());
            this.components.addAll(text);
        }

        @Override
        public void create() {
            var anchor = new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);

            for (int i = 0; i < this.components.size(); i++) {
                var text = this.components.get(i);
                var location = anchor.clone().subtract(0, 0.244 * i, 0);

                QLib.task().global().run(() -> {
                    var stand = location.getWorld().spawn(location, ArmorStand.class);
                    CustomMeta.setPDCIdentifier(stand, "quark:hover-text");

                    stand.teleport(location);

                    stand.setMarker(true);
                    stand.setSmall(true);
                    stand.setGravity(false);
                    stand.setInvulnerable(true);
                    stand.setVisible(false);
                    CUSTOM_NAME.invoke(stand, text);
                    stand.setCustomNameVisible(true);

                    this.handles.add(stand);
                });
            }
        }

        @Override
        public void destroy() {
            QLib.task().global().run(() -> {
                for (var handle : this.handles) {
                    handle.remove();
                }
                this.handles.clear();
            });
        }

        public void edit(List<Component> texts) {
            this.destroy();
            this.components.clear();
            this.components.addAll(texts);
            this.create();
        }

        @Override
        public void deserializeData(JsonObject data) {
            if (!data.has("text")) {
                return;
            }
            var arr = data.get("text").getAsJsonArray();
            for (var e : arr) {
                this.components.add(ComponentSerializer.json(e.getAsString()));
            }
        }

        @Override
        public JsonObject serializeData() {
            var json = new JsonObject();
            var comps = new JsonArray();

            json.add("text", comps);

            for (var c : this.components) {
                comps.add(ComponentSerializer.json(c));
            }

            return json;
        }
    }

    public static final class HoverDisplayStorageService extends POIDataService<VirtualArmorStand> {
        public HoverDisplayStorageService() {
            super("sl_hover_display");
        }

        @Override
        public VirtualArmorStand create(UUID id, String name, String world, Vector3d p) {
            return new VirtualArmorStand(id, name, world, p.x(), p.y(), p.z());
        }
    }
}
