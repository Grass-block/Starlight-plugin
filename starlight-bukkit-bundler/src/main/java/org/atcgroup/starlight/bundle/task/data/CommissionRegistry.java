package org.atcgroup.starlight.bundle.task.data;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CommissionRegistry {
    private final Map<String, Class<? extends Commission>> registry = new ConcurrentHashMap<>();
    private final Map<Class<? extends Commission>, String> reverseMapper = new ConcurrentHashMap<>();

    public void register(String id, Class<? extends Commission> clazz) {
        this.registry.put(id, clazz);
        this.reverseMapper.put(clazz, id);
    }

    public void unregister(String id) {
        this.registry.remove(id);
        this.reverseMapper.remove(id);
    }

    public <C> C create(UUID uuid, String id, UUID creator) {
        try {
            var type = this.registry.get(id);
            var constructor = type.getDeclaredConstructor(UUID.class, UUID.class);
            return (C) constructor.newInstance(uuid, creator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String id(Class<? extends Commission> clazz) {
        return this.reverseMapper.get(clazz);
    }
}
