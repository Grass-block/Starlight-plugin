package org.atcgroup.starlight.bundle.mission;

import com.google.gson.JsonObject;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcgroup.starlight.bundle.mission.api.Quest;
import org.atcgroup.starlight.bundle.mission.api.QuestStatus;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.language.LocalizedMessageSupplier;
import org.atcraftmc.starlight.api.event.PlayerReadyEvent;
import org.atcraftmc.starlight.framework.module.SLCommandModule;
import org.bukkit.event.EventHandler;

import java.sql.Date;
import java.util.Set;
import java.util.UUID;


@ApplicationModule(id = "commission")
@BukkitCommand(name = "commission")
public final class Commissions extends SLCommandModule {

    @EventHandler
    public void onPlayerReady(final PlayerReadyEvent event) {
        final var player = event.getPlayer();

        QLib.task().async().run(() -> {
            if (QuestService.instance().list().isEmpty()) {
                return;
            }
            language().item("remind-new").send(QLib.audience(player));
        });
    }






    public static abstract class CommissionQuest extends Quest {
        public CommissionQuest(UUID uuid, UUID creator, Date expiration, LocalizedMessageSupplier displayName, LocalizedMessageSupplier description) {
            super(uuid, creator, expiration, displayName, description);
        }

        public CommissionQuest(UUID uuid, UUID creator, QuestStatus status, Date expiration, Set<UUID> participants, JsonObject metadata) {
            super(uuid, creator, status, expiration, participants, metadata);
        }


    }

}


