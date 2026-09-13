package org.atcgroup.starlight.bundle.music.resolve;

import java.io.File;

public interface MusicParser {
    MusicData load(File file, int offset, boolean remap, float speedMod, int interpolation);
}
