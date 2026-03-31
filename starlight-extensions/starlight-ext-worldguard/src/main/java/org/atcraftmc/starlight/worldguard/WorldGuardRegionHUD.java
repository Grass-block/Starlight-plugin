package org.atcraftmc.starlight.worldguard;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.texts.TextBuilder;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.TaskService;
import org.atcraftmc.starlight.core.view.PlayerUIService;
import org.atcraftmc.starlight.foundation.TextSender;
import org.atcraftmc.starlight.foundation.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
public final class WorldGuardRegionHUD extends BukkitAbstractModule {
    private final Map<UUID, ProtectedRegion> stateCache = new HashMap<>();

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requirePlugin("WorldGuard");
        Compatibility.requirePlugin("WorldEdit");
    }

    private String render(Player player) {
        var region = this.stateCache.get(player.getUniqueId());

        if (region == null) {
            return "";
        }

        var owners = "{msg#ui-empty-owners}";
        var players = region.getOwners().getUniqueIds()
                .stream()
                .map(Bukkit::getOfflinePlayer)
                .map(OfflinePlayer::getName).collect(Collectors.toSet());

        if (!players.isEmpty()) {
            owners = "[" + String.join(", ", players) + "]";
        }

        var template = config().value("template").string()
                .replace("{name}", region.getId())//todo: extra id
                .replace("{owner}", owners)
                .replace("{id}", region.getId());

        return this.language().inline(template, LocaleService.locale(player));
    }

    private void startRender(Player player) {
        PlayerUIService.getInstance(player).getActionbar_v2().registerIntervalProcess(
                this.getFullId(),
                0,
                2,
                TaskService::entity,
                (p, c) -> {
                    var comp = TextBuilder.buildComponent(render(p));
                    TextSender.sendActionbarTitle(p, comp);
                }
        );
    }

    private void stopRender(Player player) {
        PlayerUIService.getInstance(player).getActionbar_v2().removeProcess(this.getFullId());
    }

    public void tick(Player player) {
        var pos = player.getLocation();
        var region = WorldGuardRegionService.getRegionAt(pos.getWorld(), pos.getX(), pos.getY(), pos.getZ());
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
        TaskService.global().timer("starlight:worldguard-hud:main", 5, 5, () -> {
            for (var player : Bukkit.getOnlinePlayers()) {
                tick(player);
            }
        });
    }

    @Override
    public void disable() throws Exception {
        TaskService.global().cancel("starlight:worldguard-hud:main");

        for (var player : Bukkit.getOnlinePlayers()) {
            stopRender(player);
        }
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
}
