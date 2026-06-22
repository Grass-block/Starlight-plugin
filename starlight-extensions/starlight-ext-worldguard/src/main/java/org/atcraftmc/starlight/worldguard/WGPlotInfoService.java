package org.atcraftmc.starlight.worldguard;

import com.google.gson.JsonObject;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import me.gb2022.commons.nbt.NBTTagCompound;
import me.gb2022.commons.nbt.NBTTagList;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.Service;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.starlight.core.command.CoreCommand;
import org.atcraftmc.starlight.shared.JDBCService;
import org.atcraftmc.starlight.shared.jdbc.JDBCData;
import org.atcraftmc.starlight.shared.jdbc.document.NamedDocumentDataService;
import org.atcraftmc.starlight.worldguard.api.RegionKey;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//todo: untested
@ApplicationService(id = "wg-extra-info-v2", impl = WGPlotInfoService.Impl.class, export = true)
public interface WGPlotInfoService extends Service {
    @ServiceInject
    ServiceHolder<WGPlotInfoService> INSTANCE = new ServiceHolder<>();

    static WGPlotInfoService instance() {
        return INSTANCE.get();
    }

    NamedDocumentDataService getStorage();

    JsonObject getData(RegionKey key);

    final class Impl implements WGPlotInfoService {
        private final NamedDocumentDataService dataService = new NamedDocumentDataService("sl_plot_info");

        @Override
        public void enable() {
            this.dataService.initService(JDBCService.dataSource(JDBCData.SL_LOCAL));
            QLib.task().async().timer("wg-extra-v2:purge-timer", 10, 5 * 60 * 20, this::purge);
        }

        @Override
        public void disable() {
            QLib.task().async().cancel("wg-extra-v2:purge-cancel");
        }

        private void purge() {
            var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            var delete = new ArrayList<String>();

            if (container == null) {
                return;
            }

            for (var k : this.dataService.getAllNames()) {
                var rk = RegionKey.fromDatabaseId(k);
                var world = Bukkit.getWorld(rk.getWorldId());

                if (world == null) {
                    continue;
                }

                var regionManager = container.get(BukkitAdapter.adapt(world));

                if (regionManager == null) {
                    continue;
                }

                if (regionManager.getRegion(rk.getRegionId()) != null) {
                    continue;
                }

                delete.add(k);
            }

            this.dataService.delete(delete.toArray(new String[0]));
        }

        @Override
        public NamedDocumentDataService getStorage() {
            return dataService;
        }

        @Override
        public JsonObject getData(RegionKey key) {
            return this.dataService.get(key.toDatabaseId());
        }
    }

    final class LinkHandler {
        // ID -> V-GROUP
        private final ConcurrentHashMap<String, String> id2VGroupMap = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<String, Set<String>> VGroup2idsMap = new ConcurrentHashMap<>();

        public void load(NBTTagCompound tag) {
            this.VGroup2idsMap.clear();
            this.id2VGroupMap.clear();

            for (var entry : tag.getTagMap().entrySet()) {
                var key = entry.getKey();
                var list = ((NBTTagList) entry.getValue()).getTagList()
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.toSet());

                for (var rk : list) {
                    this.id2VGroupMap.put(rk, key);
                }

                this.VGroup2idsMap.put(key, list);
            }
        }

        public NBTTagCompound save() {
            var tag = new NBTTagCompound();

            for (var key : this.VGroup2idsMap.keySet()) {
                var group = this.VGroup2idsMap.get(key);

                if (group == null) {
                    continue;
                }

                var gl = new NBTTagList();

                for (var rk : group) {
                    gl.addString(rk);
                }

                tag.setTag(key, gl);
            }

            return tag;
        }

        public Set<String> getVGroups() {
            return this.VGroup2idsMap.keySet();
        }

        public void link(String key, String VGroup) {
            this.unlink(key);

            this.id2VGroupMap.put(key, VGroup);
            this.VGroup2idsMap.computeIfAbsent(VGroup, (k) -> new HashSet<>()).add(key);
        }

        public void unlink(String key) {
            var group = this.id2VGroupMap.remove(key);

            if (group == null) {
                return;
            }

            this.VGroup2idsMap.get(group).remove(key);
        }

        public void removeGroup(String key) {
            var ids = this.VGroup2idsMap.remove(key);
            if (ids == null) {
                return;
            }

            for (var id : ids) {
                this.id2VGroupMap.remove(id);
            }
        }
    }

    @BukkitCommand(name = "link")
    final class CreateLinkCommand extends CoreCommand {

    }
}
