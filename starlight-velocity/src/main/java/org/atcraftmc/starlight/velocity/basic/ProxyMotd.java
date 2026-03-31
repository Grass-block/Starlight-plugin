package org.atcraftmc.starlight.velocity.basic;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.platform.PluginPlatform;
import org.atcraftmc.starlight.shared.ConfigDataModel;
import org.atcraftmc.starlight.shared.Configurations;
import org.atcraftmc.starlight.velocity.core.ProxyPlayerDiscoveryService;
import org.atcraftmc.starlight.velocity.framework.VelocityAbstractModule;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

//todo: refresh command
@ApplicationModule(id = "proxy-motd")
@AutoRegister(Registrations.SERVER_EVENT)
public final class ProxyMotd extends VelocityAbstractModule {
    public static final Logger LOGGER = SLPluginEnvironment.createLogger("CustomMotd");

    private ConfigurationSection setting;
    private Favicon favicon;
    private ServerPing ping;
    private long lastUpdate;

    private void refresh() {
        this.setting = Configurations.standalone("motd");
        try {
            this.favicon = Favicon.create(Path.of(Configurations.file("motd.png", false).getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void enable() {
        this.refresh();
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
        return origin.asBuilder().favicon(this.favicon).description(ConfigDataModel.motd(this.setting)).build();
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
        var playerCount = owrPlayerCount ? ProxyPlayerDiscoveryService.INSTANCE.get()
                .getAllPlayerNames()
                .size() : getProxyServer().getPlayerCount();
        var playerCapacity = owrPlayerCaps == -1 ? origin.getPlayers()
                .map(ServerPing.Players::getMax)
                .orElse(owrPlayerCaps) : owrPlayerCaps;

        var sample = new ArrayList<ServerPing.SamplePlayer>();

        for (var s : config().value("player-prompt").list(String.class)) {
            var str = s.replace("{all}", Integer.toString(playerCount))
                    .replace("{node}", Integer.toString(getProxyServer().getPlayerCount()))
                    .replace("{all-cap}", Integer.toString(owrPlayerCaps));

            sample.add(new ServerPing.SamplePlayer(PluginPlatform.global().globalFormatMessage(str), UUID.randomUUID()));
        }

        var players = new ServerPing.Players(playerCount, playerCapacity, sample);


        var desc = ping.getDescriptionComponent();

        if (owrText) {
            var serializer = JSONComponentSerializer.json();
            var rawMotd = serializer.serialize(desc);

            var list = config().value("text-rewrites").section();

            for (var rw : list.getKeys(false)) {
                rawMotd = rawMotd.replace("{{" + rw + "}}", Objects.requireNonNull(list.getString(rw)));
            }

            desc = serializer.deserialize(rawMotd);
        }

        event.setPing(new ServerPing(version, players, desc, ping.getFavicon().orElseThrow()));
    }
}
