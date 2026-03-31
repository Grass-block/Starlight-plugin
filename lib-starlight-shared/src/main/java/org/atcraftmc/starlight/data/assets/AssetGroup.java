package org.atcraftmc.starlight.data.assets;

import me.gb2022.commons.file.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public final class AssetGroup {
    private static final String FOLDER_PATH = "/assets/%s";

    private final Map<String, FileLoader> loaders = new HashMap<>();
    private final Object provider;
    private final String name;
    private final boolean cached;
    private final File folder;
    private final Logger logger;

    public AssetGroup(Object provider, String name, boolean cached) {
        this.provider = provider;
        this.name = name;
        this.cached = cached;
        this.logger = SLPluginEnvironment.createLogger(name);
        this.folder = new File(this.folder());
    }

    public int trim() {
        var c = 0;
        for (var fn : this.loaders.keySet()) {
            var f = new File(this.folder(), fn);
            if (f.renameTo(new File(this.folder(), fn.replace(" ", "_")))) {
                c++;
            }
        }
        return c;
    }

    public void save(String name) {
        var path = FOLDER_PATH.formatted(this.name) + "/" + (name);
        var stream = this.provider.getClass().getResourceAsStream(path);

        if (stream == null) {
            return;
        }

        Files.copy(stream, getFile(name));
    }


    public Set<String> list() {
        Set<String> names = new HashSet<>();

        for (File f : Objects.requireNonNull(this.getFolder().listFiles())) {
            names.add(f.getName());
        }

        return names;
    }

    private String file(String name) {
        return folder() + "/" + name;
    }

    private String folder() {
        return SLPluginEnvironment.getPathManager().getCurrentPluginFolder()
                .append("assets")
                .append(this.name)
                .toString();
    }

    public File getFile(String name) {
        File f = new File(file(name));

        if (f.exists()) {
            return f;
        }


        if (f.getParentFile().mkdirs()) {
            this.logger.info("created folder {}", f.getParentFile().getPath());
        }
        try {
            if (f.createNewFile()) {
                this.logger.info("created file {}", f.getName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return f;
    }

    public File getFolder() {
        if (this.folder.exists()) {
            return this.folder;
        }

        if (this.folder.mkdirs()) {
            this.logger.info("create resource folder {}.", this.folder.getPath());
        }

        return this.folder;
    }


    public FileLoader getLoader(String name) {
        if (!this.loaders.containsKey(name)) {
            File f = this.getFile(name);
            if (!f.exists()) {
                throw new AssetNotFoundException(name);
            }
            this.loaders.put(name, this.cached ? FileLoader.cached(f) : FileLoader.direct(f));
        }

        return this.loaders.get(name);
    }

    public byte[] getData(String name) {
        return this.getLoader(name).load();
    }


    public String asText(String name) {
        return new String(this.getData(name), StandardCharsets.UTF_8);
    }

    public void asInputStream(String name, Consumer<InputStream> reader) {
        byte[] arr = this.getData(name);
        if (arr == null) {
            return;
        }
        InputStream stream = new ByteArrayInputStream(arr);

        reader.accept(stream);

        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existFolder() {
        return this.folder.exists() && Objects.requireNonNull(this.folder.listFiles()).length != 0;
    }
}
