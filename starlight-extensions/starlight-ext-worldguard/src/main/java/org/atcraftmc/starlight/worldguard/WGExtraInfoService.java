package org.atcraftmc.starlight.worldguard;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.gb2022.commons.container.ObjectContainer;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.atcraftmc.starlight.framework.BukkitService;
import org.atcraftmc.starlight.worldguard.data.JsonDataHandle;
import org.atcraftmc.starlight.worldguard.data.RegionKey_L;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@ApplicationService(id = "wg-extra-info", impl = WGExtraInfoService.Impl.class)
@Deprecated(since = "26.6.1")
public interface WGExtraInfoService extends BukkitService {
    String WG_FLAG_NAME = "--starlight-data";
    String WG_FLAG_DEFAULT = "{}";
    StringFlag INITIAL_FLAG = new StringFlag(WG_FLAG_NAME, WG_FLAG_DEFAULT);

    @ServiceInject
    ServiceHolder<WGExtraInfoService> INSTANCE = new ServiceHolder<>();
    ObjectContainer<StringFlag> WG_MAIN_FLAG = new ObjectContainer<>();

    static WGExtraInfoService getInstance() {
        return INSTANCE.get();
    }

    static StringFlag getMainFlag() {
        return WG_MAIN_FLAG.get();
    }

    static void validateFlag() {
        var registry = WorldGuard.getInstance().getFlagRegistry();

        Flag<?> f1 = registry.get(WGExtraInfoService.WG_FLAG_NAME);

        if (f1 == null) {
            registry.register(INITIAL_FLAG);
            WG_MAIN_FLAG.set(INITIAL_FLAG);
        } else {
            WG_MAIN_FLAG.set(((StringFlag) f1));
        }

        Starlight.LOGGER.info("WorldGuard data validating result: {}", WG_MAIN_FLAG.get());
    }

    void suggestFlush(RegionKey_L id);

    JsonDataHandle getDataHandle(RegionKey_L id);

    final class Impl implements WGExtraInfoService, RemovalListener<RegionKey_L, JsonDataHandle> {
        private final Cache<RegionKey_L, JsonDataHandle> handleCache = CacheBuilder.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(3))
                .removalListener(this)
                .build();

        private static void writeAttachmentData(ProtectedRegion region, JsonObject data) {
            region.setFlag(getMainFlag(), data.toString());
        }

        private static JsonObject readAttachmentData(ProtectedRegion region) {
            var s = Objects.requireNonNullElse(region.getFlag(getMainFlag()), "{}");
            return JsonParser.parseString(s).getAsJsonObject();
        }

        private static void write(RegionKey_L key, JsonDataHandle handle) {
            var region = WGRegionService.getRegion(key);

            if (region.isEmpty()) {
                return;
            }

            writeAttachmentData(region.get(), handle.getHandle());
        }

        @Override
        public void enable() {
            BukkitUtil.registerEventListener(this);

            QLib.task().global().timer("starlight:region-auto-save", 100, 100, () -> this.handleCache.asMap().forEach((k, v) -> {
                if (v.isDirty() && v.isFree()) {
                    write(k, v);
                    v.setDirty(false);
                }
            }));
        }

        @Override
        public void disable() {
            QLib.task().global().cancel("starlight:region-auto-save");

            BukkitUtil.unregisterEventListener(this);
            this.handleCache.asMap().forEach((k, v) -> flush(k));
            this.handleCache.invalidateAll();
        }


        @Override
        public void onRemoval(RemovalNotification<RegionKey_L, JsonDataHandle> notification) {
            var key = notification.getKey();
            var handle = notification.getValue();

            if (key == null || handle == null) {
                return;
            }

            if (!handle.isFree()) {
                this.handleCache.put(key, handle);
                return;
            }

            write(key, handle);
        }

        @Override
        public void suggestFlush(RegionKey_L id) {
            var data = this.handleCache.getIfPresent(id);

            if (data == null) {
                return;
            }

            write(id, data);
        }

        @Override
        public JsonDataHandle getDataHandle(RegionKey_L id) {
            try {
                return this.handleCache.get(id, () -> new JsonDataHandle(this, id, getData(id)));
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }


        @EventHandler
        public void onServerCommand(ServerCommandEvent event) {
            if (!(event.getCommand().contains("wg reload"))) {
                return;
            }

            this.disable();
        }


        @EventHandler(priority = EventPriority.LOWEST)
        public void onWorldUnload(WorldUnloadEvent event) {
            var w = event.getWorld();
            var m = this.handleCache.asMap();

            for (var k : new HashSet<>(m.keySet())) {
                if (!k.world().equals(w)) {
                    continue;
                }

                flush(k);
            }
        }

        private void flush(RegionKey_L k) {
            var d = this.handleCache.getIfPresent(k);
            this.handleCache.invalidate(k);

            if (d == null) {
                return;
            }

            d.waitUntilFree();
            d.invalidate();

            write(k, d);
        }

        private JsonObject getData(RegionKey_L id) {
            var region = WGRegionService.getRegion(id);
            if (region.isEmpty()) {
                return new JsonObject();
            }

            return readAttachmentData(region.get());
        }
    }


}
