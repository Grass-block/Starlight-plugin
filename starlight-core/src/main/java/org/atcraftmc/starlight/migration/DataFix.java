package org.atcraftmc.starlight.migration;

import com.google.common.io.Files;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.shared.FilePath;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("DuplicatedCode")
public interface DataFix {
    Logger LOGGER = SLPluginEnvironment.createLogger("DataFix");

    static void moveFolder(String origin, String dest) {
        try {
            String base = FilePath.pluginFolder("quark");

            File legacyFolder = new File(base + origin);
            if (!legacyFolder.exists()) {
                return;
            }

            LOGGER.info("fixing up folder {} -> {}", origin, dest);

            File folder = new File(base + dest);
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    LOGGER.info("created new folder {}", dest);
                }
            }

            for (File f : Objects.requireNonNull(legacyFolder.listFiles())) {
                File moved = new File(base + "/" + dest + "/" + f.getName());
                try {
                    Files.copy(f, moved);
                } catch (IOException e) {
                    //Starlight.getInstance().getLogger().warning("failed to move file {}",f.getName()));
                }
                if (!f.delete()) {
                    //Starlight.getInstance().getLogger().warning("failed to remove file {}",f.getName()));
                }
            }

            if (legacyFolder.delete()) {
                LOGGER.info("removed folder {}", origin);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void move(File origin, File dest) {
        if (origin.isDirectory()) {
            var fs = origin.listFiles();

            if (fs == null) {
                return;
            }

            for (var f : fs) {
                move(f, new File(dest.getAbsolutePath() + File.separator + f.getName()));
            }

            return;
        }

        try {
            Files.copy(origin, dest);
            if (!origin.delete()) {
                //Starlight.getInstance().getLogger().warning("failed to remove file {}",origin.getName()));
            }
        } catch (Exception e) {
            //Starlight.getInstance().getLogger().warning("failed to move file {}",origin.getName()));
        }
    }

    static void redirectDataFolder(String origin, String dest) {
        var legacyFolder = new File(FilePath.pluginsFolder() + "/" + origin);
        var destFolder = new File(FilePath.pluginsFolder() + "/" + dest);

        if (!legacyFolder.exists()) {
            return;
        }

        move(legacyFolder, destFolder);
    }

    static void moveFile(String origin, String dest) {
        var base = FilePath.pluginFolder("quark");
        var originFile = new File(base + origin);
        var destFile = new File(base + dest);

        if (!originFile.exists()) {
            return;
        }
        if (destFile.exists() && destFile.length() > 0) {
            return;
        }

        LOGGER.info("fixing up folder {} -> {}", origin, dest);

        try {
            Files.copy(originFile, destFile);
        } catch (IOException e) {
            LOGGER.warn("failed to move file {}", origin);
        }

        if (originFile.delete()) {
            LOGGER.info("removed file {}", dest);
        }
    }
}
