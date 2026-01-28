package org.atcraftmc.starlight.velocity.core;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.kyori.adventure.audience.Audience;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.shared.service.AbstractLocaleService;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public final class VelocityLocaleService extends AbstractLocaleService<Audience> {
    public static final MinecraftChannelIdentifier CHANNEL = MinecraftChannelIdentifier.from("starlight:locale");

    @Override
    public boolean isNativeAudience(Audience pointer) {
        return pointer instanceof Player;
    }

    @Override
    public UUID getIdentifier(Audience pointer) {
        if (!(pointer instanceof Player p)) {
            throw new UnsupportedOperationException("Cannot get uuid of " + pointer.getClass().getName());
        }
        return p.getUniqueId();
    }

    @Override
    public void announceLanguageUpdated(Audience audience, MinecraftLocale locale) {
        ((Player) audience).sendPluginMessage(CHANNEL, locale.minecraft().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public MinecraftLocale getLocaleNatively(Audience pointer) {
        if (!(pointer instanceof Player p)) {
            throw new UnsupportedOperationException("Cannot get locale of " + pointer.getClass().getName());
        }
        return MinecraftLocale.locale(Optional.ofNullable(p.getEffectiveLocale()).orElse(Locale.getDefault()));
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        if (SLPluginEnvironment.getPlugin().config().value("starlight-velocity.locale.detect").bool()) {
            checkClientLocale(event.getPlayer(), getLocaleNatively(event.getPlayer()).minecraft());
        }
    }

    @Subscribe
    public void onPlayerLeave(DisconnectEvent event) {
        this.invalidateCachedLocale(event.getPlayer().getUniqueId());
    }

    @Subscribe
    public void onPluginMessageEvent(PluginMessageEvent event) {
        if (!CHANNEL.equals(event.getIdentifier())) {
            return;
        }

        if (!(event.getSource() instanceof ServerConnection)) {
            return;
        }

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        var locale = new String(event.getData(), StandardCharsets.UTF_8);
        this.onLanguageUpdated(((ServerConnection) event.getSource()).getPlayer(), MinecraftLocale.minecraft(locale));
    }


}
