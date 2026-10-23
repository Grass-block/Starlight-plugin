package org.atcraftmc.starlight;

import me.gb2022.commons.file.FilePath;
import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public interface SelfBootUtil {
    static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java -jar starlight-bukkit-core.jar [options]");
            System.out.println("Options:");
            System.out.println("  -fix-wg: remove unused worldgen settings in level.dat");
            return;
        }

        switch (args[0]) {
            case "-fix-wg" -> fixWorldGenerationLevelDAT();
        }
    }

    static Set<String> scanForModIds() {
        var serverDirectory = Path.of("").toAbsolutePath().normalize();
        if (serverDirectory == null) {
            return Set.of();
        }

        var modsDirectory = serverDirectory.resolve("mods");
        if (!Files.isDirectory(modsDirectory)) {
            return Set.of();
        }

        var result = new LinkedHashSet<String>();
        try (var files = Files.list(modsDirectory)) {
            files.filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().toLowerCase().endsWith(".jar"))
                    .sorted(Comparator.comparing(file -> file.getFileName().toString()))
                    .forEach(file -> scanModJar(file, result));
        } catch (IOException ignored) {
            // A missing/unreadable mods directory is equivalent to no installed mods.
        }
        return Collections.unmodifiableSet(result);
    }

    private static void scanModJar(Path file, Set<String> result) {
        try (var jar = new JarFile(file.toFile())) {
            scanMetadata(jar, "META-INF/neoforge.mods.toml", MODS_TOML_ID, result);
            scanMetadata(jar, "META-INF/mods.toml", MODS_TOML_ID, result);
            scanMetadata(jar, "fabric.mod.json", FABRIC_MOD_ID, result);
            scanMetadata(jar, "mcmod.info", LEGACY_FORGE_MOD_ID, result);
        } catch (IOException ignored) {
            // Ignore invalid or partially copied mod jars and continue scanning.
        }
    }

    private static void scanMetadata(JarFile jar, String entryName, Pattern pattern, Set<String> result)
            throws IOException {
        var entry = jar.getJarEntry(entryName);
        if (entry == null) {
            return;
        }

        String content;
        try (var stream = jar.getInputStream(entry)) {
            content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }

        var matcher = pattern.matcher(content);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
    }

    Pattern MODS_TOML_ID = Pattern.compile("(?m)^\\s*modId\\s*=\\s*[\\\"]([^\\\"]+)[\\\"]");
    Pattern FABRIC_MOD_ID = Pattern.compile("(?m)[\\\"]id[\\\"]\\s*:\\s*[\\\"]([^\\\"]+)[\\\"]");
    Pattern LEGACY_FORGE_MOD_ID = Pattern.compile("(?m)[\\\"]modid[\\\"]\\s*:\\s*[\\\"]([^\\\"]+)[\\\"]");

    static void fixWorldGenerationLevelDAT() throws IOException {
        var currentMods = scanForModIds();
        var levelDATFile = FilePath.RUNTIME.append("world").append("level.dat").file();
        var backupFile = FilePath.RUNTIME.append("world").append("level-backup_%s.dat".formatted(System.currentTimeMillis())).file();

        if (!levelDATFile.exists()) {
            return;
        }

        if (!backupFile.exists()) {
            backupFile.createNewFile();
        }

        Files.copy(levelDATFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        NBTTagCompound root;

        try (var in = new FileInputStream(levelDATFile)) {
            root = ((NBTTagCompound) NBT.readZipped(in));
        }

        var wgRoot = root.getCompoundTag("Data").getCompoundTag("WorldGenSettings").getCompoundTag("dimensions");

        for (var dim : new HashSet<>(wgRoot.getTagMap().entrySet())) {
            var mid = dim.getKey().split(":")[0];

            if (!currentMods.contains(mid) && !Objects.equals(mid, "minecraft")) {
                System.out.println(" > Removed unused dimension: " + dim.getKey());
                wgRoot.remove(dim.getKey());
                continue;
            }

            var cnt = 0;
            var biomes = wgRoot.getCompoundTag(dim.getKey())
                    .getCompoundTag("generator")
                    .getCompoundTag("biome_source")
                    .getTagList("biomes");

            var it = biomes.getTagList().iterator();

            while (it.hasNext()) {
                var tag = it.next();
                var biomeNS = ((NBTTagCompound) tag).getString("biome").split(":")[0];

                if (!currentMods.contains(biomeNS) && !Objects.equals(biomeNS, "minecraft")) {
                    it.remove();
                    cnt++;
                }
            }

            System.out.printf("> Removed %s unused biome in %s%n", cnt, dim.getKey());
        }

        try (var o = new FileOutputStream(levelDATFile)) {
            NBT.writeZipped(root, o);
        }
    }
}
