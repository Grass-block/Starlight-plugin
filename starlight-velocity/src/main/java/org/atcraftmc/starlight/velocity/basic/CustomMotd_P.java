package org.atcraftmc.starlight.velocity.basic;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import me.gb2022.modular.module.ApplicationModule;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.texts.TextBuilder;
import org.atcraftmc.starlight.velocity.core.PlayerStatisticService;
import org.atcraftmc.starlight.velocity.framework.module.SLVPackageModule;
import org.bukkit.configuration.file.YamlConfiguration;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

@ApplicationModule(id = "custom-motd")
public class CustomMotd_P extends SLVPackageModule {
    public static final Pattern PATTERN = Pattern.compile("\\{[a-z]+}");
    public static final Logger LOGGER = LogManager.getLogger("CustomMotd");

    private YamlConfiguration setting;
    private ServerPing ping;
    private long lastUpdate;

    @Override
    public void enable() {
        this.syncGlobal();
    }

    private void syncGlobal() {
        try {
            var server = config().value("sync-target").string();

            getProxyServer().getServer(server).ifPresentOrElse((s) -> {
                try {
                    this.ping = s.ping().get();
                    this.lastUpdate = System.currentTimeMillis();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }, () -> LOGGER.error("no server motd provider named {}", server));
        } catch (Exception e) {
            LOGGER.error("Caught exception when updating motd:");
            LOGGER.catching(e);
        }
    }

    public ServerPing getSyncedMotd() {
        var interval = System.currentTimeMillis() - this.lastUpdate;

        if (this.ping == null || interval > config().value("sync-interval").longValue()) {
            this.syncGlobal();
        }

        return this.ping;
    }

    public ServerPing buildLocalServerPing(ServerPing origin) {
        var root = this.setting.getConfigurationSection("motd");

        if (root == null) {
            throw new RuntimeException("invalid config!");
        }

        var template = root.getString("motd-title") + "\n{#reset}" + root.getString("motd-subtitle");
        var matcher = PATTERN.matcher(template);

        while (matcher.find()) {
            var raw = matcher.group();

            var key = raw.replace("{", "").replace("}", "");

            if (key.startsWith("$")) {
                continue;
            }

            String content;

            if (!root.contains(key)) {
                content = key;
            } else if (root.isString(key)) {
                content = root.getString(key);
            } else {
                List<String> list = root.getStringList(key);
                content = list.get(new Random().nextInt(list.size()));
            }

            if (content == null) {
                content = key;
            }

            template = template.replace(raw, content);
        }

        var desc = TextBuilder.build(template).toSingleLine();
        var icon = Favicon.create(Path.of());

        return origin.asBuilder().favicon(icon).description(desc).build();
    }


    @Subscribe
    public void onPing(ProxyPingEvent event) {
        event.setResult(ResultedEvent.GenericResult.allowed());

        var sync = config().value("sync").bool();
        var owrVersion = config().value("overwrite-version").bool();
        var owrText = config().value("overwrite-text").bool();
        var owrPlayerCount = config().value("overwrite-player-count").bool();
        var owrPlayerCaps = config().value("overwrite-player-max").intValue();

        var origin = event.getPing();
        var ping = sync ? this.getSyncedMotd() : this.buildLocalServerPing(origin);

        var version = owrVersion ? origin.getVersion() : ping.getVersion();
        var playerCount = owrPlayerCount ? PlayerStatisticService.getAllPlayersInProxy().size() : getProxyServer().getPlayerCount();
        var playerCapacity = owrPlayerCaps == -1 ? origin.getPlayers()
                .map(ServerPing.Players::getMax)
                .orElse(owrPlayerCaps) : owrPlayerCaps;

        var players = new ServerPing.Players(playerCount, playerCapacity, List.of());
        var desc = ping.getDescriptionComponent();

        if (owrText) {
            var serializer = JSONComponentSerializer.json();
            var rawMotd = serializer.serialize(desc);

            desc = serializer.deserialize(rawMotd);
        }

        event.setPing(new ServerPing(version, players, desc, ping.getFavicon().orElseThrow()));
    }
}
