package org.atcraftmc.starlight.framework;

import org.atcraftmc.starlight.util.ProductMetadata;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public interface SLPluginConcept {
    ClassLoader classLoader();

    File getFile();

    ProductMetadata getMetadata();

    default InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = classLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    String name();
}
