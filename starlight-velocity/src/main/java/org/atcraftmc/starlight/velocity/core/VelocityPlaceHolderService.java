package org.atcraftmc.starlight.velocity.core;

import me.gb2022.modular.service.ApplicationService;
import me.gb2022.modular.service.Service;
import me.gb2022.modular.service.ServiceInject;
import org.atcraftmc.qlib.texts.placeholder.GloballyPlaceHolder;
import org.atcraftmc.qlib.texts.placeholder.PlaceHolder;
import org.atcraftmc.qlib.texts.placeholder.StringExtraction;
import org.atcraftmc.starlight.PlaceHolders;
import org.atcraftmc.starlight.data.GlobalVars;

import java.util.Map;
import java.util.regex.Pattern;

@ApplicationService(id = "place-holder")
public interface VelocityPlaceHolderService extends Service {
    StringExtraction PATTERN = new StringExtraction(Pattern.compile("\\{#(.*?)}"), 2, 1);
    GlobalVars EXTERNAL_VARS = new GlobalVars();

    GloballyPlaceHolder GLOBAL_VAR = new GloballyPlaceHolder();
    GloballyPlaceHolder TEXT_STYLE = PlaceHolders.chat();

    @ServiceInject
    static void start() {
        reloadExternal();
    }

    static void reloadExternal() {
        GLOBAL_VAR.clear();

        Map<String, String> map = EXTERNAL_VARS.loadMap();

        for (String key : map.keySet()) {
            GLOBAL_VAR.register(key, map.get(key));
        }
    }

    static String format(String input) {
        return PlaceHolder.format(PATTERN, input, GLOBAL_VAR, TEXT_STYLE);
    }

    static String format(String s, GloballyPlaceHolder... placeHolders) {
        return PlaceHolder.format(PATTERN, s, placeHolders);
    }
}
