package org.atcraftmc.starlight.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.atcraftmc.starlight.framework.SLPluginHandle;
import org.atcraftmc.starlight.util.dependency.GradleDependency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ProductMetadata {
    private final String version;
    private final String buildTime;
    private final int apiVersion;
    private final List<GradleDependency> dependencies;

    public ProductMetadata(String version, String buildTime, int apiVersion, List<GradleDependency> dependencies) {
        this.version = version;
        this.buildTime = buildTime;
        this.apiVersion = apiVersion;
        this.dependencies = dependencies;
    }

    public ProductMetadata(JsonObject json) {
        this.version = json.get("version").getAsString();
        this.buildTime = json.get("build-time").getAsString();
        this.apiVersion = json.get("api-version").getAsInt();
        this.dependencies = new ArrayList<>();

        for (var s : json.getAsJsonArray("libraries")) {
            this.dependencies.add(GradleDependency.fromGradle(s.getAsString()));
        }
    }

    public static ProductMetadata createFromResource(SLPluginHandle plugin) {
        var res = plugin.getResource(plugin.name() + ".product-meta.json");
        if (res == null) {
            throw new RuntimeException("Failed to load product meta: " + plugin.name() + ".product-meta.json");
        }

        try {
            return new ProductMetadata(JsonParser.parseString(new String(res.readAllBytes())).getAsJsonObject());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getVersion() {
        return version;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public List<GradleDependency> getDependencies() {
        return dependencies;
    }
}
