package org.atcgroup.starlight.bundle.mission.api;

import com.google.gson.JsonObject;

import java.sql.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class QuestRegistry {
    private final Map<String, Class<? extends Quest>> registry = new ConcurrentHashMap<>();
    private final Map<Class<? extends Quest>, String> reverseMapper = new ConcurrentHashMap<>();

    public void register(String id, Class<? extends Quest> clazz) {
        this.registry.put(id, clazz);
        this.reverseMapper.put(clazz, id);
    }

    public void unregister(String id) {
        this.reverseMapper.remove(this.registry.remove(id), id);
    }

    @SuppressWarnings("unchecked")
    public <C extends Quest> C create(String typeId, UUID uuid, UUID creator, QuestStatus status, Date expiration, Set<UUID> participants, JsonObject metadata) {
        try {
            var type = this.registry.get(typeId);
            var constructor = type.getDeclaredConstructor(UUID.class, UUID.class, QuestStatus.class, Date.class, Set.class, JsonObject.class);
            return (C) constructor.newInstance(uuid, creator, status, expiration, participants, metadata);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String id(Class<? extends Quest> clazz) {
        return this.reverseMapper.get(clazz);
    }
}
