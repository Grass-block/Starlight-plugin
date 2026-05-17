package org.atcraftmc.starlight.util;

import java.lang.reflect.Modifier;

public interface EarlyLoadingManager {
    static void scan(Class<?> type, Object obj) {
        try {
            for (var m : type.getDeclaredMethods()) {
                if (!m.isAnnotationPresent(EarlyLoading.class)) {
                    continue;
                }

                m.setAccessible(true);

                if (Modifier.isStatic(m.getModifiers())) {
                    m.invoke(null);
                } else {
                    if (obj != null) {
                        m.invoke(obj);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
