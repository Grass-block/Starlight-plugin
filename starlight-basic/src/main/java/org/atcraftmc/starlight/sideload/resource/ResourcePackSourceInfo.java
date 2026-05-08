package org.atcraftmc.starlight.sideload.resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

public interface ResourcePackSourceInfo {
    static Set<String> load(File file) throws IOException {
        if (!file.exists() || file.length() == 0) {
            return Set.of();
        }

        try (var in = new FileInputStream(file)) {
            var result = new HashSet<String>();
            var arr = new String(in.readAllBytes()).split(";");

            for (var s:arr){
                result.add(s.trim());
            }

            return result;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static void write(File file, Set<String> sha) throws IOException {
        file.getParentFile().mkdirs();

        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Could not create file " + file.getAbsolutePath());
            }
        }

        try (var out = new FileOutputStream(file)) {
            out.write(String.join(";", sha).getBytes(StandardCharsets.UTF_8));
        }
    }

    static Set<String> get(File[] files) throws Exception {
        var result = new HashSet<String>();

        for (File file : files) {
            result.add(fileSHA1(file));
        }

        return result;
    }

    static byte[] fileSHA1Raw(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");

        try (InputStream is = new FileInputStream(file)) {

            byte[] buffer = new byte[8192];

            int read;

            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }

        return digest.digest();
    }

    static String fileSHA1(File file) throws Exception {
        StringBuilder sb = new StringBuilder();

        for (byte b : fileSHA1Raw(file)) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
