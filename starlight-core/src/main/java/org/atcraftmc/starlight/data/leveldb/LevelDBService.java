package org.atcraftmc.starlight.data.leveldb;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.iq80.leveldb.util.Snappy;
import sun.misc.Unsafe;

import java.io.File;
import java.io.IOException;

public interface LevelDBService {
    LevelDBService INSTANCE = new JavaLevelDBService();

    static DB create(File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a directory: " + folder);
        }

        try {
            return INSTANCE.createDB(folder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static DB getDB(String db) {
        throw new UnsupportedOperationException();
    }

    DB createDB(File folder) throws IOException;

    class JavaLevelDBService implements LevelDBService {
        static void fixSnappy() {
            try {
                var c_snappy = Class.forName("org.iq80.leveldb.util.Snappy");
                var f_snappy = c_snappy.getDeclaredField("SNAPPY");

                Unsafe unsafe = getUnsafe();

                var base = unsafe.staticFieldBase(f_snappy);
                var offset = unsafe.staticFieldOffset(f_snappy);

                f_snappy.setAccessible(true);
                unsafe.putObject(base, offset, new Snappy.IQ80Snappy());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static Unsafe getUnsafe() {
            try {
                var f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                return (Unsafe) f.get(null);
            } catch (Throwable t) {
                throw new RuntimeException("Unable to acquire Unsafe", t);
            }
        }

        @Override
        public DB createDB(File folder) throws IOException {
            fixSnappy();

            return new Iq80DBFactory().open(folder, new Options());
        }
    }


}
