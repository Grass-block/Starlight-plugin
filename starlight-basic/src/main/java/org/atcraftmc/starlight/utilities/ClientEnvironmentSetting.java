package org.atcraftmc.starlight.utilities;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.assertion.NumberLimitation;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.foundation.command.CommandProvider;
import org.atcraftmc.starlight.foundation.command.ModuleCommand;
import org.atcraftmc.starlight.foundation.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationModule(id = "client-environment-setting")
@CommandProvider({ClientEnvironmentSetting.LocalWeatherCommand.class, ClientEnvironmentSetting.LocalTimeCommand.class})
public final class ClientEnvironmentSetting extends BukkitAbstractModule {
    private TimeManager timeManager;

    @Override
    public void enable() throws Exception {
        this.timeManager.init();
    }

    @Override
    public void disable() throws Exception {
        this.timeManager.destroy();
    }

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        try {
            Compatibility.requirePlugin("ProtocolLib");
            this.timeManager = new ProtocolLibTimeManager();
        } catch (APIIncompatibleException e) {
            Compatibility.requireMethod((() -> Player.class.getMethod("setPlayerTime", long.class, boolean.class)));
            Compatibility.requireMethod((() -> Player.class.getMethod("resetPlayerTime")));
            Compatibility.requireMethod((() -> Player.class.getMethod("setPlayerWeather", WeatherType.class)));
            Compatibility.requireMethod((() -> Player.class.getMethod("resetPlayerWeather")));
            this.timeManager = new DirectTimeManager();
        }

        this.handle().getLogger().info("Using {} as time manager", this.timeManager.getClass().getName());
    }

    interface TimeManager {
        default void init() {

        }

        default void destroy() {

        }

        void setPlayerTime(Player player, long time, boolean relative);

        void resetPlayerTime(Player player);
    }

    @BukkitCommand(name = "local-weather", permission = "+starlight.client.weather")
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

            getLanguage().item("weather-" + mode).send(QLib.audience(player));
        }
    }

    @BukkitCommand(name = "local-time", permission = "+starlight.client.time")
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
                    this.getModule().timeManager.setPlayerTime(player, val, true);
                    getLanguage().item("time-offset").send(QLib.audience(player), val);
                }
                case "fixed" -> {
                    var val = context.requireArgumentInteger(1, NumberLimitation.bound(-0.1, 24000.1));
                    this.getModule().timeManager.setPlayerTime(player, val, false);
                    getLanguage().item("time-fixed").send(QLib.audience(player), val);
                }
                case "off" -> {
                    this.getModule().timeManager.resetPlayerTime(player);
                    getLanguage().item("time-off").send(QLib.audience(player));
                }
            }
        }
    }

    private static final class DirectTimeManager implements TimeManager {
        @Override
        public void setPlayerTime(Player player, long time, boolean relative) {
            player.setPlayerTime(time, relative);
        }

        @Override
        public void resetPlayerTime(Player player) {
            player.resetPlayerTime();
        }
    }

    private static final class ProtocolLibTimeManager extends PacketAdapter implements TimeManager {
        private final Map<UUID, Long> fixedPlayerTimes = new HashMap<>();

        public ProtocolLibTimeManager() {
            super(Starlight.instance(), ListenerPriority.HIGHEST, PacketType.Play.Server.UPDATE_TIME);
        }

        private void sendTimePacket(Player player, long time, boolean relative) {
            var packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.UPDATE_TIME);

            packet.getLongs().write(0, player.getWorld().getFullTime());
            packet.getLongs().write(1, time == -1 ? player.getWorld().getTime() : (relative ? time : -time));

            try {
                if (time == -1) {
                    player.resetPlayerTime();
                }
                player.setPlayerTime(time, relative);
            } catch (Exception ignored) {
            }

            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        }

        @Override
        public void init() {
            ProtocolLibrary.getProtocolManager().addPacketListener(this);
        }

        @Override
        public void destroy() {
            ProtocolLibrary.getProtocolManager().removePacketListener(this);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            if (!this.fixedPlayerTimes.containsKey(event.getPlayer().getUniqueId())) {
                return;
            }

            var packet = event.getPacket();

            var fullTime = packet.getLongs().read(0);
            var frozenTime = this.fixedPlayerTimes.get(event.getPlayer().getUniqueId());

            packet.getLongs().write(0, fullTime);          // fullTime 无所谓
            packet.getLongs().write(1, -frozenTime);       // 关键：负数
        }

        @Override
        public void setPlayerTime(Player player, long time, boolean relative) {
            if (relative) {
                this.fixedPlayerTimes.remove(player.getUniqueId());
            }
            if (!relative) {
                this.fixedPlayerTimes.put(player.getUniqueId(), time);
            }
            sendTimePacket(player, time, relative);
        }

        @Override
        public void resetPlayerTime(Player player) {
            this.fixedPlayerTimes.remove(player.getUniqueId());
            sendTimePacket(player, -1, true);
        }
    }
}
