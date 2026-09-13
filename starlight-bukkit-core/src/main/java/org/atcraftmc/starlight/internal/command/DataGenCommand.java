package org.atcraftmc.starlight.internal.command;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.gb2022.commons.file.FilePath;
import me.gb2022.gluon.module.ApplicationModule;
import me.gb2022.gluon.module.ModuleContainer;
import me.gb2022.gluon.pack.ApplicationPackage;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.atcraftmc.starlight.core.command.CoreCommand;
import org.atcraftmc.starlight.framework.PluginModuleAttachment;
import org.atcraftmc.starlight.util.IndexWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@BukkitCommand(name = "data-gen", permission = "-starlight.core.datagen")
public final class DataGenCommand extends CoreCommand {

    @Override
    public void suggest(CommandSuggestion suggestion) {
        super.suggest(suggestion);
    }

    private HashMap<String, List<ModuleContainer>> indexModules() {
        var groups = new HashMap<String, List<ModuleContainer>>();

        for (var meta : StarlightBukkitCore.instance().getGluonContext().getModuleManager().getModules().values()) {
            groups.computeIfAbsent(meta.getMetadata().fullId().split(":")[0], (k) -> new ArrayList<>()).add(meta);
        }

        return groups;
    }

    private FilePath storage() {
        return SLPluginEnvironment.getPathManager().getCurrentPluginFolder().append("debug").append("data-gen");
    }

    private void generateDocTemplate() {
        try {
            var f = storage().append("module-doc");
            var groups = indexModules();
            var template = new String(this.getClass().getResourceAsStream("/assets/doc-template.md").readAllBytes());
            var count = 0;

            for (var gid : groups.keySet()) {
                var group = groups.get(gid);
                var pth = f.append(gid);

                for (var mod : group) {
                    var key = mod.getMetadata().key();
                    var name = mod.getAttachment(PluginModuleAttachment.class).displayNameOrId(MinecraftLocale.ZH_CN);

                    var file = pth.append(key.id() + ".md").file();

                    if (file.exists() && file.length() > 0) {
                        continue;
                    }

                    file.getParentFile().mkdirs();
                    file.createNewFile();

                    try (var o = new FileOutputStream(file)) {
                        var doc = template.replace("{name}", name)
                                .replace("{id}", key.id())
                                .replace("{fid}", key.fullId())
                                .replace(
                                        "{version}",
                                        mod.getMetadata()
                                                .version()
                                )
                                .replace("{beta}", mod.getMetadata().beta() ? "是" : "否")
                                .replace(
                                        "{internal}",
                                        mod.getMetadata().internal() ? "是" : "否"
                                )
                                .replace("{default-enable}", mod.getMetadata().defaultEnabled() ? "是" : "否")
                                .replace(
                                        "{description}",
                                        mod.getMetadata().description()
                                );

                        o.write(doc.getBytes(StandardCharsets.UTF_8));
                        count++;
                    }
                }
            }
            System.out.printf("done. %s modules indexed.%n", count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(CommandExecution context) {
        switch (context.requireEnum(0, "module-index", "module-list", "module-doc", "module-list-raw")) {
            case "module-index" -> IndexWriter.index(new DocIndexWriter(storage().append("module-index.json").file()));
            case "module-list" -> IndexWriter.index(new MDIndexWriter(storage().append("module-list.md").file()));
            case "module-list-raw" -> IndexWriter.index(new RawIndexWriter(storage().append("module-list.txt").file()));
            case "module-doc" -> generateDocTemplate();
        }
    }

    public static abstract class FileWriter implements IndexWriter {
        private final FileOutputStream stream;

        protected FileWriter(File file) {
            try {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                if (!file.exists()) {
                    file.createNewFile();
                }

                this.stream = new FileOutputStream(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public final void print(String s, Object... fmt) {
            try {
                if (fmt.length == 0) {
                    this.stream.write((s + "\n").getBytes(StandardCharsets.UTF_8));
                    return;
                }
                this.stream.write((s + "\n").formatted(fmt).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() {
            try {
                this.stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static final class DocIndexWriter extends FileWriter {
        private final JsonArray root = new JsonArray();
        private JsonObject packageDOM;
        private JsonArray packageContentList;

        private DocIndexWriter(File file) {
            super(file);
        }

        @Override
        public void writePackageHeader(ApplicationPackage pkg) {
            this.packageDOM = new JsonObject();
            this.packageContentList = new JsonArray();

            this.packageDOM.addProperty("text", pkg.meta().id());
            this.packageDOM.add("items", this.packageContentList);
        }

        @Override
        public void writeModuleInfo(ModuleContainer mod) {
            var a2 = new JsonObject();
            var t = mod.getAttachment(PluginModuleAttachment.class).displayNameOrId(MinecraftLocale.ZH_CN);

            a2.addProperty("text", t.replaceAll("&.", ""));
            a2.addProperty("link", "/starlight/content/" + mod.getMetadata().fullId().replace(":", "/"));

            this.packageContentList.add(a2);
        }

        @Override
        public void writePackageEnd(ApplicationPackage pkg) {
            this.root.add(this.packageDOM);
        }

        @Override
        public void close() {
            var gson = new GsonBuilder().setPrettyPrinting().create();
            this.print(gson.toJson(this.root));

            super.close();
        }
    }

    public static final class RawIndexWriter extends FileWriter {
        private RawIndexWriter(File file) {
            super(file);
        }

        @Override
        public void writePackageHeader(ApplicationPackage pkg) {
            print("%s [%s]:", pkg.meta().id(), IndexWriter.getOwnerFile(pkg).getName());
        }

        @Override
        public void writeModuleInfo(ModuleContainer mod) {
            print(" - %s", mod.getMetadata().key().id());
        }

        @Override
        public void writePackageEnd(ApplicationPackage pkg) {
            print("");
        }
    }

    public static final class MDIndexWriter extends FileWriter {
        private MDIndexWriter(File file) {
            super(file);
        }

        @Override
        public void writePackageHeader(ApplicationPackage pkg) {
            print("### %s:", pkg.meta().id());
        }

        @Override
        public void writeModuleInfo(ModuleContainer mod) {
            var k = mod.getMetadata().key();
            var ns = k.namespace();
            var key = k.id();
            var desc = mod.getReference().getAnnotation(ApplicationModule.class).description();
            var url = "https://dev.atcraftmc.cn/starlight/content/%s/%s.html".formatted(ns, key);
            var name = mod.getAttachment(PluginModuleAttachment.class).displayNameOrId(MinecraftLocale.EN_US);

            print("- %s: %s [doc↗](%s)", name, desc, url);
        }

        @Override
        public void writePackageEnd(ApplicationPackage pkg) {
            print("");
        }
    }
}
