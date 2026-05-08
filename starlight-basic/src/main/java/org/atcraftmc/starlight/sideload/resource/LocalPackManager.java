package org.atcraftmc.starlight.sideload.resource;

import me.gb2022.commons.file.FilePath;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class LocalPackManager {
    public static final Logger LOGGER = SLPluginEnvironment.createLogger("ResourcePackManager");
    public static final String INDEX_FILE_NAME = "_index.dat";
    public static final String COMPILED_FILE_NAME = "_compiled.zip";

    private final FilePath base;

    public LocalPackManager(FilePath base) {
        this.base = base;
        if (base.file().mkdirs()) {
            LOGGER.info("Created directory: {}", base.file().getAbsolutePath());
        }
    }

    public File[] list() {
        return this.base.file().listFiles((file, s) -> {
            if (Objects.equals(s, INDEX_FILE_NAME)) {
                return false;
            }

            return !Objects.equals(s, COMPILED_FILE_NAME);
        });
    }

    public boolean isEmpty() {
        return list().length == 0;
    }

    public void compile() throws IOException {
        var cache = SLPluginEnvironment.getPathManager()
                .getCurrentPluginFolder()
                .append("cache")
                .append("resource-compile-" + System.currentTimeMillis());

        if (cache.file().mkdirs()) {
            LOGGER.info("Created working directory: {}", cache.file().getPath());
        }

        var source = list();

        LOGGER.info("Source: {}", Arrays.stream(source).map(File::getName).toList());

        for (var pack : source) {
            try (var zis = new ZipInputStream(new FileInputStream(pack))) {

                ZipEntry entry;

                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        continue;
                    }

                    var file = cache.append(entry.getName()).file();

                    if (!file.exists() || file.length() == 0) {
                        file.getParentFile().mkdirs();

                        if (!file.createNewFile()) {
                            LOGGER.error("Failed to create file: {}", file);
                        }

                        try (var out = new FileOutputStream(file)) {
                            out.write(zis.readAllBytes());
                        }
                    } else {
                        LOGGER.info("Duplicated entry: {}", entry.getName());
                        try (var out = new FileOutputStream(file)) {
                            out.write(zis.readAllBytes());
                        }
                    }
                }
            }
        }

        var file = this.base.append(COMPILED_FILE_NAME).file();
        var cachePath = cache.file().toPath();

        try (var zos = new ZipOutputStream(new FileOutputStream(file))) {
            Files.walk(cachePath).filter(Files::isRegularFile).forEach(path -> {
                try {
                    var relativePath = cachePath.relativize(path);
                    var name = relativePath.toString().replace("\\", "/");

                    var zipEntry = new ZipEntry(name);

                    zos.putNextEntry(zipEntry);

                    Files.copy(path, zos);

                    zos.closeEntry();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }

        LOGGER.info("Compiled resource pack - {}KiB", file.length() / 1024);
    }

    public void update() {
        if (isEmpty()) {
            LOGGER.info("No packs added, update aborted.");
            return;
        }

        var shaFile = this.base.append(INDEX_FILE_NAME).file();
        try {
            var localSha = ResourcePackSourceInfo.load(shaFile);
            var fileSha = ResourcePackSourceInfo.get(list());

            if (localSha.equals(fileSha)) {
                LOGGER.info("No file change discovered, update aborted.");
                return;
            }
            compile();

            ResourcePackSourceInfo.write(shaFile, fileSha);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public File getCompiledFile() {
        return this.base.append(COMPILED_FILE_NAME).file();
    }
}
