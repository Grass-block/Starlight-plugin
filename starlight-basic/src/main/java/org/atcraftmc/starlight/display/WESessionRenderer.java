package org.atcraftmc.starlight.display;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.starlight.api.event.worldedit.WESessionSelectEvent;
import org.atcraftmc.starlight.core.WESessionTrackService;
import org.atcraftmc.starlight.core.command.CommandProvider;
import org.atcraftmc.starlight.core.command.ModuleCommand;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.core.platform.Players;
import org.atcraftmc.starlight.data.JDBCPlayerData;
import org.atcraftmc.starlight.data.jdbc.document.DocumentField;
import org.atcraftmc.starlight.data.jdbc.document.DocumentFieldCodec;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.atcraftmc.starlight.shared.data.flex.FlexibleMapService;
import org.atcraftmc.starlight.shared.data.flex.TableColumn;
import org.atcraftmc.starlight.shared.service.JDBCData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationModule(id = "we-session-renderer", version = "1.0.0")
@AutoRegister(Registrations.SERVER_EVENT)
@CommandProvider(WESessionRenderer.WESessionRenderCommand.class)
public final class WESessionRenderer extends BukkitAbstractModule implements FlexibleMapService.Codec<WESessionRenderer.RenderMode>, DocumentFieldCodec<WESessionRenderer.RenderMode> {
    private final TableColumn<RenderMode> RENDER_MODE_L = TableColumn.custom("we_render_mode", 24, RenderMode.UPDATE, this);
    private final DocumentField<RenderMode> RENDER_MODE = DocumentField.custom("we-session-render-mode", RenderMode.UPDATE, this);

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requirePlugin("WorldEdit");
    }

    @Override
    public void enable() {
        QLib.task().global().timer("quark:we-renderer:main", 0, 10, () -> {
            for (var p : Bukkit.getOnlinePlayers()) {
                if (getMode(p) != RenderMode.PERSISTENT) {
                    continue;
                }

                draw(p);
            }
        });
    }

    @Override
    public void disable() {
        QLib.task().global().cancel("quark:we-renderer:main");
    }

    @EventHandler
    public void onSelectionUpdate(WESessionSelectEvent event) {
        if (getMode(event.getPlayer()) != RenderMode.UPDATE) {
            return;
        }

        render(event.getPlayer());
    }

    private void draw(Player p) {
        var r = WESessionTrackService.getRegion(p);
        if (r == null) {
            return;
        }
        Players.show3DBox(p, r.getPoint0(), r.getPoint1());
    }

    private void render(Player p) {
        var t = new AtomicInteger();

        QLib.task().global().timer(0, 10, (ctx) -> {
            t.addAndGet(5);

            if (t.get() > 25) {
                ctx.cancel();
            }

            draw(p);
        });
    }

    private RenderMode getMode(Player player) {
        var data = JDBCData.PLAYER_SHARED.get(player.getUniqueId());

        if (!RENDER_MODE.exist(data)) {
            if (RENDER_MODE_L.exist(JDBCPlayerData.PLAYER_SHARED)) {
                RENDER_MODE.set(data, RENDER_MODE_L.get(JDBCPlayerData.PLAYER_SHARED, player.getUniqueId()));
            }
        }

        return RENDER_MODE.get(data);
    }

    @Override
    public JsonElement encodeJson(RenderMode value) {
        return new JsonPrimitive(value.name());
    }

    @Override
    public RenderMode decodeJson(JsonElement value) {
        return RenderMode.of(value.getAsString());
    }

    @Override
    public String encode(RenderMode data) {
        return data.name();
    }

    @Override
    public RenderMode decode(String data) {
        return RenderMode.valueOf(data);
    }

    private void setMode(Player player, RenderMode mode) {
        var data = JDBCData.PLAYER_SHARED.get(player.getUniqueId());
        RENDER_MODE.set(data, mode);
    }

    public enum RenderMode {
        NEVER,
        UPDATE,
        PERSISTENT;

        static RenderMode of(String id) {
            return switch (id) {
                case "update" -> RenderMode.UPDATE;
                case "persistent" -> RenderMode.PERSISTENT;
                default -> RenderMode.NEVER;
            };
        }
    }

    @BukkitCommand(name = "we-selection", aliases = {"render-we", "/render-sel", "/render-selection", "/render", "render-we-selection"})
    public static final class WESessionRenderCommand extends ModuleCommand<WESessionRenderer> {
        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, "off", "render", "update", "persistent");
        }

        @Override
        public void execute(CommandExecution context) {
            if (!context.hasArgumentAt(0)) {
                MessageAccessor.send(this.getLanguage(), context.getSender(), "render");
                this.getModule().render(context.requireSenderAsPlayer());
                return;
            }

            var action = context.requireEnum(0, "off", "render", "update", "persistent");

            if (Objects.equals(action, "render")) {
                MessageAccessor.send(this.getLanguage(), context.getSender(), "render");
                this.getModule().render(context.requireSenderAsPlayer());
                return;
            }

            MessageAccessor.send(this.getLanguage(), context.getSender(), "mode-" + action);

            this.getModule().setMode(context.requireSenderAsPlayer(), RenderMode.of(action));
        }
    }
}
