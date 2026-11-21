package org.atcgroup.starlight.bundle.mission;

import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.atcgroup.starlight.bundle.mission.api.Quest;
import org.atcgroup.starlight.bundle.mission.api.QuestRegistry;
import org.atcgroup.starlight.bundle.mission.api.QuestStatus;
import org.atcgroup.starlight.bundle.mission.api.QuestStorage;
import org.atcraftmc.starlight.framework.BukkitService;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ApplicationService(id = "quest-service", impl = QuestService.Impl.class)
public interface QuestService extends BukkitService {
    UUID SYSTEM_UUID = UUID.fromString("33550336-0000-0000-0000-000000000000");

    @ServiceInject
    ServiceHolder<QuestService> INSTANCE = new ServiceHolder<>();

    static QuestService instance() {
        return INSTANCE.get();
    }

    QuestRegistry getRegistry();

    QuestStorage getStorage();

    void close(UUID uuid);

    default boolean delete(UUID uuid) {
        return this.getStorage().delete(uuid);
    }

    default Set<Quest> byCreator(UUID creator) {
        return this.getStorage().byCreator(creator);
    }

    default Set<Quest> byParticipant(UUID participant) {
        return this.getStorage().byParticipant(participant);
    }

    default Set<Quest> byType(String type) {
        return this.getStorage().byType(type);
    }

    default Optional<Quest> byUUID(UUID uuid) {
        return this.getStorage().byUUID(uuid);
    }

    default Set<Quest> list() {
        return this.getStorage().list();
    }

    default void join(UUID uuid, Player sender) {
        byUUID(uuid).ifPresent((q) -> {
            q.getParticipants().add(uuid);
            q.onPlayerJoined(sender);
        });
    }

    default void leave(UUID uuid, Player sender) {
        byUUID(uuid).ifPresent((q) -> {
            q.getParticipants().remove(uuid);
            q.onPlayerLeft(sender);
        });
    }


    class Impl implements QuestService {
        private final QuestRegistry registry = new QuestRegistry();
        private final QuestStorage storage = new QuestStorage("SL_QUESTS", this.registry);

        @Override
        public QuestRegistry getRegistry() {
            return registry;
        }

        @Override
        public QuestStorage getStorage() {
            return storage;
        }

        @Override
        public void close(UUID uuid) {
            var o = byUUID(uuid);
            if (o.isEmpty()) {
                return;
            }
            var c = o.get();

            this.storage.updateStatus(uuid, QuestStatus.COMPLETED);

            for (var p : c.getParticipants()) {
                RewardService.instance().triggerEvent(p, c);
            }
            RewardService.instance().triggerEvent(c.getCreator(), c);
        }
    }
}
