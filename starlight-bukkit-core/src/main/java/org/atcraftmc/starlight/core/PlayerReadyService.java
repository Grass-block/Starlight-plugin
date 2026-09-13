package org.atcraftmc.starlight.core;

import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import me.gb2022.gluon.service.ServiceLayer;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.api.event.PlayerReadyEvent;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.atcraftmc.starlight.framework.BukkitService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@ApplicationService(id = "locale-service", impl = PlayerReadyService.class, export = true,layer = ServiceLayer.FRAMEWORK)
public final class PlayerReadyService implements BukkitService {
    @ServiceInject
    public static final ServiceHolder<PlayerReadyService> INSTANCE = new ServiceHolder<>();
    public static final Logger LOGGER = SLPluginEnvironment.createLogger("PlayerReadyService");

    private final Set<String> registeredWaits = new HashSet<>();
    private final Map<UUID, LockFuture> locks = new HashMap<>();

    public static PlayerReadyService instance() {
        return INSTANCE.get();
    }

    public void registerWait(String id) {
        this.registeredWaits.add(id);
    }

    public void unregisterWait(String id) {
        this.registeredWaits.remove(id);
    }

    public void complete(final UUID uuid, String id) {
        getLock(uuid).complete(id);
    }

    public LockFuture getLock(UUID uuid) {
        return this.locks.computeIfAbsent(uuid, u -> new LockFuture(this.registeredWaits));
    }

    @Override
    public void enable() throws Exception {
        BukkitUtil.registerEventListener(this);
    }

    @Override
    public void disable() throws Exception {
        BukkitUtil.unregisterEventListener(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var future = getLock(event.getPlayer().getUniqueId());
        var player = event.getPlayer();

        QLib.task().async().run(() -> {
            if (future.completed()) {
                BukkitUtil.callEvent(new PlayerReadyEvent(player));
                return;
            }

            future.thenRun(() -> BukkitUtil.callEvent(new PlayerReadyEvent(player)));
        });

        QLib.task().async().delay(15, () -> {
            if(!future.completed()) {
                LOGGER.warn("waiting process for player {}({}) timed out (15ticks) !", player.getName(), player.getUniqueId());
                future.complete((Void) null);
            }
        });
    }

    public static final class LockFuture extends CompletableFuture<Void> {
        private final Set<String> currentLocks = new HashSet<>();
        private final Set<String> expectedLocks;

        private LockFuture(Set<String> expectedLocks) {
            this.expectedLocks = expectedLocks;
        }

        public void complete(String lock) {
            if (!this.expectedLocks.contains(lock)) {
                return;
            }

            this.currentLocks.add(lock);

            if (completed()) {
                this.complete((Void) null);
            }
        }

        public boolean completed() {
            return this.currentLocks.size() == this.expectedLocks.size();
        }
    }
}
