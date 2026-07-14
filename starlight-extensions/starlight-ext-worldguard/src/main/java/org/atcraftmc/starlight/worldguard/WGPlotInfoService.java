package org.atcraftmc.starlight.worldguard;

import com.google.gson.JsonObject;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import me.gb2022.commons.file.FilePath;
import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import me.gb2022.commons.nbt.NBTTagList;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.Service;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.core.command.CoreCommand;
import org.atcraftmc.starlight.shared.JDBCService;
import org.atcraftmc.starlight.shared.jdbc.JDBCData;
import org.atcraftmc.starlight.shared.jdbc.document.NamedDocumentDataService;
import org.atcraftmc.starlight.worldguard.api.RegionKey;
import org.bukkit.Bukkit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

//todo: untested
@ApplicationService(id = "wg-extra-info-v2", impl = WGPlotInfoService.Impl.class, export = true)
public interface WGPlotInfoService extends Service {
    @ServiceInject
    ServiceHolder<WGPlotInfoService> INSTANCE = new ServiceHolder<>();

    static LanguageItem lang(String id) {
        return SLPluginEnvironment.getApplication().language().item("starlight-worldguard:wg-plot-info:" + id);
    }

    static WGPlotInfoService instance() {
        return INSTANCE.get();
    }

    Set<RegionKey> getAllInGroup(RegionKey groupId);

    NamedDocumentDataService getStorage();

    JsonObject getData(RegionKey key);

    final class Impl implements WGPlotInfoService {
        private final NamedDocumentDataService dataService = new NamedDocumentDataService("sl_plot_info");
        private final LinkHandler linkHandler = new LinkHandler();
        private final LinkManagementCommand command = new LinkManagementCommand(this.linkHandler);

        @Override
        public void enable() {
            this.dataService.initService(JDBCService.dataSource(JDBCData.SL_LOCAL));
            QLib.task().async().timer("wg-extra-v2:purge-timer", 10, 5 * 60 * 20, this::purge);
            WGCommandService.COMMAND.registerSubCommand(this.command);

            var file = linkFile().file();

            if(!file.exists()||file.length()==0) {
                return;
            }

            try(var in = new FileInputStream(file)) {
                this.linkHandler.load((NBTTagCompound) NBT.readZipped(in));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void disable() {
            QLib.task().async().cancel("wg-extra-v2:purge-cancel");
            WGCommandService.COMMAND.unregisterSubCommand(this.command);
        }

        @Override
        public NamedDocumentDataService getStorage() {
            return dataService;
        }

        @Override
        public JsonObject getData(RegionKey key) {
            return this.dataService.get(this.toGroup(key).toDatabaseId());
        }

        @Override
        public Set<RegionKey> getAllInGroup(RegionKey groupId) {
            var gk = groupId.toDatabaseId();

            if (!this.linkHandler.id2VGroupMap.containsKey(gk)) {
                return Set.of(groupId);
            }

            return this.linkHandler.getGroupedRegions(gk).stream().map(RegionKey::fromDatabaseId).collect(Collectors.toSet());
        }

        private FilePath linkFile() {
            return SLPluginEnvironment.getPathManager().getCurrentPluginFolder().append("/data/wg-links.dat");
        }

        private RegionKey toGroup(RegionKey input) {
            var k = input.toDatabaseId();
            if (!this.linkHandler.id2VGroupMap.containsKey(k)) {
                return input;
            }

            return RegionKey.fromDatabaseId(this.linkHandler.id2VGroupMap.get(k));
        }

        private void purge() {
            var container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            var delete = new ArrayList<String>();

            if (container == null) {
                return;
            }

            for (var k : this.dataService.getAllNames()) {
                try {
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
                }catch (Exception e) {
                    delete.add(k);
                }
            }

            this.dataService.delete(delete.toArray(new String[0]));
        }
    }

    final class LinkHandler {
        private final ConcurrentHashMap<String, String> id2VGroupMap = new ConcurrentHashMap<>();
        private final ConcurrentHashMap<String, Set<String>> VGroup2idsMap = new ConcurrentHashMap<>();

        public void load(NBTTagCompound tag) {
            this.VGroup2idsMap.clear();
            this.id2VGroupMap.clear();

            for (var entry : tag.getTagMap().entrySet()) {
                var key = entry.getKey();
                var list = ((NBTTagList) entry.getValue()).getTagList().stream().map(Object::toString).collect(Collectors.toSet());

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

        public Set<String> getGroupedRegions(String key) {
            if (!this.VGroup2idsMap.containsKey(key)) {
                return Set.of();
            }

            return this.VGroup2idsMap.get(key);
        }
    }

    @BukkitCommand(name = "link", permission = "-starlight.plot.link")
    final class LinkManagementCommand extends CoreCommand {
        private final LinkHandler linkHandler;

        public LinkManagementCommand(LinkHandler linkHandler) {
            this.linkHandler = linkHandler;
        }

        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, "create", "unlink", "destroy-group");
            suggestion.matchArgument(0, "unlink", (s) -> WGCommandService.suggestRegions(s, 1));
            suggestion.matchArgument(0, "destroy-group", (s) -> s.suggest(1, this.linkHandler.VGroup2idsMap.keySet()));
            suggestion.matchArgument(0, "link", (s) -> {
                WGCommandService.suggestRegions(s, 1);
                WGCommandService.suggestRegions(s, 2);
            });
        }

        @Override
        public void execute(CommandExecution context) {
            switch (context.requireEnum(0, "create", "unlink", "destroy-group")) {
                case "create" -> {
                    var rid = context.requireArgumentAt(1);
                    var region = RegionKey.fromSearchId(rid);
                    var group = context.requireArgumentAt(2);

                    if (WGRegionService.getRegion(region).isEmpty()) {
                        lang("invalid-region").send(QLib.audience(context.getSender()));
                        return;
                    }

                    this.linkHandler.link(rid, group);
                    lang("link-success").send(QLib.audience(context.getSender()), rid, group);
                }
                case "unlink" -> {
                    var rid = context.requireArgumentAt(1);
                    var region = RegionKey.fromSearchId(rid);

                    if (WGRegionService.getRegion(region).isEmpty()) {
                        lang("invalid-region").send(QLib.audience(context.getSender()));
                        return;
                    }

                    this.linkHandler.unlink(rid);
                    lang("unlink-success").send(QLib.audience(context.getSender()), rid);
                }
                case "destroy-group" -> {
                    var gid = context.requireArgumentAt(1);


                    if (!this.linkHandler.VGroup2idsMap.containsKey(gid)) {
                        lang("invalid-group").send(QLib.audience(context.getSender()));
                        return;
                    }

                    this.linkHandler.removeGroup(gid);
                    lang("destroy-group-success").send(QLib.audience(context.getSender()), gid);
                }
            }
        }
    }
}
