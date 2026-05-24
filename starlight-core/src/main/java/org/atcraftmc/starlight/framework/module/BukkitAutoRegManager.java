package org.atcraftmc.starlight.framework.module;

import me.gb2022.gluon.Registrations;
import org.atcraftmc.starlight.core.VisualScoreboardService;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.atcraftmc.starlight.core.view.PlayerUIService;
import org.atcraftmc.starlight.core.view.ScoreboardTrackingStateCallback;
import org.atcraftmc.starlight.core.view.UITrackingStateCallback;
import org.atcraftmc.starlight.util.PluginAutoRegManager;
import org.bukkit.event.Listener;

public final class BukkitAutoRegManager extends PluginAutoRegManager {

    @Override
    public void initCustom() {
        Builder.build(this, (i) -> {
            i.attach(Registrations.SERVER_EVENT, (o) -> BukkitUtil.registerEventListener(((Listener) o)));
            i.detach(Registrations.SERVER_EVENT, (o) -> BukkitUtil.unregisterEventListener(((Listener) o)));
            i.attach(PlayerUIService.TRACKING, (o) -> PlayerUIService.TRACKER.attachCallback(((UITrackingStateCallback) o)));
            i.detach(PlayerUIService.TRACKING, (o) -> PlayerUIService.TRACKER.detachCallback(((UITrackingStateCallback) o)));
            i.attach(
                    VisualScoreboardService.TRACKING,
                    (o) -> VisualScoreboardService.instance().attachCallback(((ScoreboardTrackingStateCallback) o))
            );
            i.detach(
                    VisualScoreboardService.TRACKING,
                    (o) -> VisualScoreboardService.instance().detachCallback(((ScoreboardTrackingStateCallback) o))
            );
        });
    }
}
