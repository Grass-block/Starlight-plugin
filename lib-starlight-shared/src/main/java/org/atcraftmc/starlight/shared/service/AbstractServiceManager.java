package org.atcraftmc.starlight.shared.service;

import me.gb2022.modular.APIIncompatibleException;
import me.gb2022.modular.service.ApplicationService;
import me.gb2022.modular.service.Service;
import me.gb2022.modular.service.ServiceManager;
import me.gb2022.modular.service.injection.ServiceProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.config.ConfigEntry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class AbstractServiceManager extends ServiceManager<Service> {
    public static final Logger LOGGER = LogManager.getLogger("SLServiceManager");
    protected final HashMap<String, Class<? extends Service>> services = new HashMap<>(24);

    public static <T extends Service> T createImplementation(Class<T> clazz, ConfigEntry config) {
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getAnnotation(ServiceProvider.class) == null) {
                continue;
            }

            try {
                if (m.getParameterTypes().length == 0) {
                    return clazz.cast(m.invoke(null));
                }

                return clazz.cast(m.invoke(null, config));
            } catch (NoClassDefFoundError ignored) {
            } catch (IllegalAccessException | InvocationTargetException e) {
                if (e.getCause() instanceof APIIncompatibleException) {
                    return null;
                }
                if (e.getCause() instanceof NoClassDefFoundError) {
                    return null;
                }

                throw new RuntimeException(e);
            }
        }

        var implClass = clazz.getAnnotation(ApplicationService.class).impl();

        if (implClass == Service.class) {
            return null;
        }

        try {
            return clazz.cast(implClass.getDeclaredConstructor(ConfigEntry.class).newInstance(config));
        } catch (NoSuchMethodException e) {
            try {
                return clazz.cast(implClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                if (e.getCause() instanceof APIIncompatibleException) {
                    return null;
                }
                if (e.getCause() instanceof NoClassDefFoundError) {
                    return null;
                }
                throw new RuntimeException(ex);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            if (ex.getCause() instanceof APIIncompatibleException) {
                return null;
            }
            if (ex.getCause() instanceof NoClassDefFoundError) {
                return null;
            }
            throw new RuntimeException(ex);
        }
    }


    @Override
    public final Logger getLogger() {
        return LOGGER;
    }

    @Override
    public final void handleException(Throwable e) {
        LOGGER.catching(e);
    }

}
