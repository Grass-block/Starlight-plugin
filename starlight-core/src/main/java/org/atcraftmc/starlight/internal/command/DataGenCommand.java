package org.atcraftmc.starlight.internal.command;
//todo: data gen doc and module list.

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.gb2022.commons.file.FilePath;
import me.gb2022.gluon.module.ModuleContainer;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.foundation.command.CoreCommand;
import org.atcraftmc.starlight.framework.PluginModuleAttachment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@BukkitCommand(name = "data-gen", permission = "-starlight.core.datagen")
public class DataGenCommand extends CoreCommand {

    @Override
    public void suggest(CommandSuggestion suggestion) {
        super.suggest(suggestion);
    }

    private HashMap<String, List<ModuleContainer>> indexModules() {
        var groups = new HashMap<String, List<ModuleContainer>>();

        for (var meta : Starlight.instance().context().getModuleManager().getModules().values()) {
            groups.computeIfAbsent(meta.getMetadata().fullId().split(":")[0], (k) -> new ArrayList<>()).add(meta);
        }

        return groups;
    }

    private FilePath storage() {
        return SLPluginEnvironment.getPathManager().getCurrentPluginFolder().append("debug").append("data-gen");
    }

    private void generateIndexFile() {
        var f = storage().append("module-index.json").file();
        var count = 0;

        try {
            f.getParentFile().mkdirs();
            f.createNewFile();

            var arr = new JsonArray();
            var groups = indexModules();

            for (var gid : groups.keySet()) {
                var group = groups.get(gid);

                var obj = new JsonObject();
                var a = new JsonArray();

                obj.addProperty("text", gid);
                obj.add("items", a);

                for (var mod : group) {
                    var a2 = new JsonObject();
                    var t = mod.getAttachment(PluginModuleAttachment.class).displayNameOrId(MinecraftLocale.ZH_CN);

                    a2.addProperty("text", t.replaceAll("&.", ""));
                    a2.addProperty("link", "/starlight/content/" + mod.getMetadata().fullId().replace(":", "/"));

                    a.add(a2);
                    count++;
                }

                arr.add(obj);
            }

            try (var o = new FileOutputStream(f)) {
                var gson = new GsonBuilder().setPrettyPrinting().create();

                o.write(gson.toJson(arr).getBytes(StandardCharsets.UTF_8));
            }

            System.out.printf("done. %s modules indexed.%n", count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateDocTemplate() throws IOException {
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
                            .replace("{version}", mod.getMetadata().version())
                            .replace("{beta}", mod.getMetadata().beta() ? "是" : "否")
                            .replace("{internal}", mod.getMetadata().internal() ? "是" : "否")
                            .replace("{default-enable}", mod.getMetadata().defaultEnabled() ? "是" : "否")
                            .replace("{description}", mod.getMetadata().description());

                    o.write(doc.getBytes(StandardCharsets.UTF_8));
                    count++;
                }
            }
        }
        System.out.printf("done. %s modules indexed.%n", count);
    }


    @Override
    public void execute(CommandExecution context) {
        switch (context.requireEnum(0, "module-index", "service-index", "module-doc")) {
            case "module-index" -> generateIndexFile();
            case "service-index" -> {

            }
            case "module-doc" -> {
                try {
                    generateDocTemplate();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
