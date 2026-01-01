package org.atcraftmc.starlight.core.permission;

import me.gb2022.modular.service.ApplicationService;
import me.gb2022.modular.service.ServiceInject;
import me.gb2022.modular.service.ServiceLayer;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.framework.BukkitService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashMap;
import java.util.Map;

@ApplicationService(id = "permission", impl = PermissionService.Impl.class, layer = ServiceLayer.FOUNDATION)
public interface PermissionService extends BukkitService {
    PermissionService INSTANCE = new Impl();

    @ServiceInject
    static void start() throws Exception {
        INSTANCE.enable();
    }

    @ServiceInject
    static void stop() throws Exception {
        INSTANCE.disable();
    }

    static Permission getPermission(String codec) {
        String name = codec;

        if (codec.matches("^[+\\-!].*")) {
            name = codec.substring(1);
        }

        Permission permission = Bukkit.getPluginManager().getPermission(name);

        if (permission == null) {
            if (codec.matches("^[+\\-!].*")) {
                Starlight.instance().getLogger().warning("created unregistered permission " + codec);
                permission = createPermissionObject(codec);
            }
        }

        return permission;
    }

    static Permission createObject(String perm) {
        String id = perm.substring(1);
        if (!id.matches("^[a-z.]+$")) {
            throw new IllegalArgumentException("Invalid permission id: " + id);
        }

        return new Permission(id, switch (perm.charAt(0)) {
            case '+' -> PermissionDefault.TRUE;
            case '-' -> PermissionDefault.OP;
            case '!' -> PermissionDefault.FALSE;
            default -> throw new RuntimeException("invalid value:" + perm.charAt(0));
        });
    }

    static void createPermission(String fmt) {
        INSTANCE.create(fmt);
    }

    static Permission createPermissionObject(String fmt) {
        return INSTANCE.create(fmt);
    }

    static void update() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.recalculatePermissions();
        }
    }

    static void deletePermission(String fmt) {
        INSTANCE.delete(fmt);
    }

    Permission create(String fmt);

    void delete(String fmt);

    final class Impl implements PermissionService {
        private final Map<String, Permission> map = new HashMap<>();

        @Override
        public void disable() {
            for (Permission m : this.map.values()) {
                Bukkit.getPluginManager().removePermission(m);
            }
        }

        @Override
        public Permission create(String perm) {
            Permission permission = createObject(perm);

            if (perm.matches("^[a-z.]+$")) {
                perm = perm.substring(1);
            }

            Permission replacement = Bukkit.getPluginManager().getPermission(perm);
            if (replacement != null) {
                replacement.setDefault(permission.getDefault());
            } else {
                try {
                    Bukkit.getPluginManager().addPermission(permission);
                } catch (Exception ignored) {
                    //Starlight.getInstance().getLogger().warning("duplicated permission:" + perm);
                }
            }

            this.map.put(permission.getName(), permission);
            return permission;
        }

        @Override
        public void delete(String perm) {
            String id = perm.substring(1);
            Bukkit.getPluginManager().removePermission(id);
            this.map.remove(id);
        }
    }
}
