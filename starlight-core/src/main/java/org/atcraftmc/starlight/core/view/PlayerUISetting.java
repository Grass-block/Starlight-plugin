package org.atcraftmc.starlight.core.view;

import java.util.HashSet;
import java.util.Set;

public final class PlayerUISetting {
    final Set<String> rejectChannels = new HashSet<>();
    final Set<String> rejectRenderers = new HashSet<>();
    private boolean rejectAll;

    public PlayerUISetting() {
    }

    public PlayerUISetting(PlayerUISetting setting) {
        this.rejectAll = setting.rejectAll;
        this.rejectChannels.addAll(setting.rejectChannels);
        this.rejectRenderers.addAll(setting.rejectRenderers);
    }

    public void rejectChannel(final String channel) {
        this.rejectChannels.add(channel);
    }

    public void unrejectChannel(final String channel) {
        this.rejectChannels.remove(channel);
    }

    public void rejectRenderer(final String renderer) {
        this.rejectRenderers.add(renderer);
    }

    public void unrejectRenderer(final String renderer) {
        this.rejectRenderers.remove(renderer);
    }

    public boolean isChannelRejected(final String channel) {
        return this.rejectChannels.contains(channel);
    }

    public boolean isRendererRejected(final String renderer) {
        return this.rejectRenderers.contains(renderer);
    }

    public void rejectAllChannels(boolean rejectAll) {
        this.rejectAll = rejectAll;
    }

    public boolean isRejectAllChannels() {
        return this.rejectAll;
    }
}
