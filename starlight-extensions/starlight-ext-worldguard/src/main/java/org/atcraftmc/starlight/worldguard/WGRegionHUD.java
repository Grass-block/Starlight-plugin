package org.atcraftmc.starlight.worldguard;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import net.kyori.adventure.text.ComponentLike;
import org.apache.commons.lang3.function.TriFunction;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.util.pipe.Pipeline;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.platform.Compatibility;
import org.atcraftmc.starlight.core.view.PlayerUIService;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationModule(id = "wg-region-hud", description = "Create an HUD displaying WorldGuard region info.")
@AutoRegister(Registrations.SERVER_EVENT)
public final class WGRegionHUD extends BukkitAbstractModule {

    public static final Pipeline<Formatter> PIPELINE = new Pipeline<>();
    private final Map<UUID, ProtectedRegion> stateCache = new HashMap<>();

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requirePlugin("WorldGuard");
        Compatibility.requirePlugin("WorldEdit");
    }

    public String format(ProtectedRegion region, World world, String s) {
        var owners = "{msg#ui-empty-owners}";
        var players = region.getOwners().getUniqueIds().stream().map(Bukkit::getOfflinePlayer).map(OfflinePlayer::getName).collect(
                Collectors.toSet());

        if (!players.isEmpty()) {
            owners = "[" + String.join(", ", players) + "]";
        }

        return s.replace("{name}", region.getId()).replace("{owner}", owners).replace("{id}", region.getId());
    }

    private String render(Player player) {
        var region = this.stateCache.get(player.getUniqueId());

        if (region == null) {
            return "";
        }


        var template = config().value("template").string();

        for (var p : PIPELINE.list()) {
            template = p.apply(region, player.getWorld(), template);
        }

        return this.language().inline(template, LocaleService.locale(player));
    }

    private void startRender(Player player) {
        PlayerUIService.getInstance(player).getActionbar_v2().registerIntervalProcess(
                this.getFullId(),
                0,
                2,
                entity -> QLib.task().entity(entity),
                (p, c) -> {
                    var comp = QLib.textBuilder().buildComponent(render(p));
                    QLib.audience(p).sendActionBar((ComponentLike) comp);
                }
        );
    }

    private void stopRender(Player player) {
        PlayerUIService.getInstance(player).getActionbar_v2().removeProcess(this.getFullId());
    }

    public void tick(Player player) {
        var pos = player.getLocation();
        var region = WGRegionService.getRegionAt(pos.getWorld(), pos.getX(), pos.getY(), pos.getZ());
        var currentState = region.orElse(null);

        var previousState = this.stateCache.get(player.getUniqueId());

        if (!this.stateCache.containsKey(player.getUniqueId())) {
            this.stateCache.put(player.getUniqueId(), currentState);
        }

        if (currentState != null && previousState == null) {
            startRender(player);
        }

        if (currentState == null && previousState != null) {
            stopRender(player);
        }

        this.stateCache.put(player.getUniqueId(), currentState);
    }

    @Override
    public void enable() {
        PIPELINE.addLast("starlight:default", this::format);

        QLib.task().global().timer("starlight:worldguard-hud:main", 5, 5, () -> {
            for (var player : Bukkit.getOnlinePlayers()) {
                tick(player);
            }
        });
    }

    @Override
    public void disable() {
        QLib.task().global().cancel("starlight:worldguard-hud:main");

        for (var player : Bukkit.getOnlinePlayers()) {
            stopRender(player);
        }

        PIPELINE.clear();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.stateCache.remove(event.getPlayer().getUniqueId());
        tick(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        stopRender(event.getPlayer());
        this.stateCache.remove(event.getPlayer().getUniqueId());
    }


    public interface Formatter extends TriFunction<ProtectedRegion, World, String, String> {
    }
}
