package org.atcraftmc.starlight.worldguard.api;

import org.atcraftmc.starlight.shared.jdbc.document.DocumentField;
import org.atcraftmc.starlight.worldguard.WGExtraInfoService;
import org.atcraftmc.starlight.worldguard.WGExtraInfoServiceV2;

public interface WGCustomNameAPI {
    String DEFAULT_VALUE = "__default__";
    DocumentField<String> REGION_CUSTOM_NAME = DocumentField.string("custom-name", DEFAULT_VALUE);

    static String getRegionCustomName(RegionKey key) {
        var dom = WGExtraInfoServiceV2.instance().getData(key);

        if (!WGCustomNameAPI.REGION_CUSTOM_NAME.exist(dom)) {
            var h = WGExtraInfoService.getInstance().getDataHandle(key.legacy());

            if (h.has("custom-name")) {
                WGCustomNameAPI.REGION_CUSTOM_NAME.set(dom, h.getString("custom-name", WGCustomNameAPI.DEFAULT_VALUE));
            }
        }

        return WGCustomNameAPI.REGION_CUSTOM_NAME.get(dom);
    }

    static void setRegionCustomName(RegionKey key, String value) {
        var data = WGExtraInfoServiceV2.instance().getData(key);
        WGCustomNameAPI.REGION_CUSTOM_NAME.set(data, value);
    }
}
