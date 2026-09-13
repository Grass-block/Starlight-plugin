package org.atcraftmc.starlight.util.version;

import com.google.gson.JsonParser;
import me.gb2022.commons.http.HttpMethod;
import me.gb2022.commons.http.HttpRequest;

import java.util.Objects;

public interface ModrinthVersionAPI {
    String API = "api.modrinth.com/v2/project/starlight-plugin/version";

    static VersionInfo checkVersion(String product) {
        var json = HttpRequest.https(HttpMethod.GET, API).build().request();
        var arr = new JsonParser().parse(json).getAsJsonArray();

        for (var e : arr) {
            var obj = e.getAsJsonObject();
            var version = VersionInfo.parse(obj.get("version_number").getAsString());

            if (Objects.equals(version.product, product)) {
                return version;
            }
        }

        return null;
    }
}
