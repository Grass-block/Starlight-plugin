package org.atcraftmc.starlight.velocity.basic;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.modular.Registrations;
import me.gb2022.modular.module.ApplicationModule;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.shared.ConfigDataModel;
import org.atcraftmc.starlight.shared.Configurations;
import org.atcraftmc.starlight.velocity.core.ProxyPlayerTrackService;
import org.atcraftmc.starlight.velocity.framework.VelocityAbstractModule;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

//todo: refresh command
@ApplicationModule(id = "proxy-motd")
@AutoRegister(Registrations.SERVER_EVENT)
public final class ProxyMotd extends VelocityAbstractModule {
    public static final Logger LOGGER = LogManager.getLogger("CustomMotd");

    private ConfigurationSection setting;
    private Favicon favicon;
    private ServerPing ping;
    private long lastUpdate;

    private void refresh() {
        this.setting = Configurations.standalone("motd.yml");
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
        var playerCount = owrPlayerCount ? ProxyPlayerTrackService.getAllPlayersInProxy().size() : getProxyServer().getPlayerCount();
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
