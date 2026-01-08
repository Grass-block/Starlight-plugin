package org.atcraftmc.starlight.utilities;

import me.gb2022.modular.APIIncompatibleException;
import me.gb2022.modular.module.ApplicationModule;
import org.atcraftmc.qlib.command.QuarkCommand;
import org.atcraftmc.qlib.command.assertion.NumberLimitation;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.starlight.foundation.command.CommandProvider;
import org.atcraftmc.starlight.foundation.command.ModuleCommand;
import org.atcraftmc.starlight.foundation.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

@ApplicationModule(id = "client-environment-setting")
@CommandProvider({ClientEnvironmentSetting.LocalWeatherCommand.class, ClientEnvironmentSetting.LocalTimeCommand.class})
public final class ClientEnvironmentSetting extends BukkitAbstractModule {

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requireMethod((() -> Player.class.getMethod("setPlayerTime", long.class, boolean.class)));
        Compatibility.requireMethod((() -> Player.class.getMethod("resetPlayerTime")));
        Compatibility.requireMethod((() -> Player.class.getMethod("setPlayerWeather", WeatherType.class)));
        Compatibility.requireMethod((() -> Player.class.getMethod("resetPlayerWeather")));
    }

    @QuarkCommand(name = "local-weather", permission = "+starlight.client.weather")
    public static final class LocalWeatherCommand extends ModuleCommand<ClientEnvironmentSetting> {
        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, "none", "rain", "off");
        }

        @Override
        public void execute(CommandExecution context) {
            var player = context.requireSenderAsPlayer();
            var mode = context.requireEnum(0, "none", "rain", "off");

            switch (mode) {
                case "none" -> player.setPlayerWeather(WeatherType.CLEAR);
                case "rain" -> player.setPlayerWeather(WeatherType.DOWNFALL);
                case "off" -> player.resetPlayerWeather();
            }

            getLanguage().item("weather-" + mode).send(player);
        }
    }

    @QuarkCommand(name = "local-time", permission = "+starlight.client.time")
    public static final class LocalTimeCommand extends ModuleCommand<ClientEnvironmentSetting> {
        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, "offset", "fixed", "off");
            suggestion.matchArgument(0, "offset", (c) -> c.suggest(1, "-1000", "0", "1000"));
            suggestion.matchArgument(0, "fixed", (c) -> c.suggest(1, "0", "1000", "12600", "18000", "24000"));
        }

        @Override
        public void execute(CommandExecution context) {
            var player = context.requireSenderAsPlayer();
            var mode = context.requireEnum(0, "offset", "fixed", "off");

            switch (mode) {
                case "offset" -> {
                    var val = context.requireArgumentInteger(1);
                    player.setPlayerTime(val, true);
                    getLanguage().item("time-offset").send(player, val);
                }
                case "fixed" -> {
                    var val = context.requireArgumentInteger(1, NumberLimitation.bound(-0.1, 24000.1));
                    player.setPlayerTime(val, false);
                    getLanguage().item("time-fixed").send(player, val);
                }
                case "off" -> {
                    player.resetPlayerTime();
                    getLanguage().item("time-off").send(player);
                }
            }
        }
    }
}
