package org.atcraftmc.starlight.framework.module;

import me.gb2022.apm.local.PluginMessenger;
import me.gb2022.apm.remote.event.MessengerEventChannel;
import me.gb2022.apm.remote.event.RemoteEventListener;
import me.gb2022.commons.reflect.Annotations;
import me.gb2022.commons.reflect.AutoRegisterManager;
import me.gb2022.commons.reflect.DependencyInjector;
import me.gb2022.modular.APIIncompatibleException;
import me.gb2022.modular.Registrations;
import me.gb2022.modular.module.ModuleContainer;
import me.gb2022.modular.module.component.ComponentProvider;
import me.gb2022.modular.module.component.SubComponent;
import me.gb2022.modular.pack.ApplicationPackage;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.qlib.command.AbstractCommand;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.qlib.language.LanguageItem;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.core.data.BanEntryService;
import org.atcraftmc.starlight.core.data.WaypointService;
import org.atcraftmc.starlight.core.data.region.SimpleRegionService;
import org.atcraftmc.starlight.core.permission.PermissionService;
import org.atcraftmc.starlight.data.assets.Asset;
import org.atcraftmc.starlight.data.assets.AssetGroup;
import org.atcraftmc.starlight.foundation.command.CommandProvider;
import org.atcraftmc.starlight.foundation.command.ModuleCommand;
import org.atcraftmc.starlight.foundation.command.StarlightCommandManager;
import org.atcraftmc.starlight.foundation.platform.BukkitUtil;
import org.atcraftmc.starlight.framework.BukkitModule;
import org.atcraftmc.starlight.framework.ModuleCommandHolder;
import org.atcraftmc.starlight.shared.data.flex.FlexibleMapService;
import org.atcraftmc.starlight.shared.service.IRemoteMessageService;
import org.atcraftmc.starlight.shared.service.JDBCService;
import org.atcraftmc.starlight.shared.service.RemoteMessageService;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;


public interface ModuleServices {
    ModuleAutoRegManager AUTO_REG = new ModuleAutoRegManager();
    DependencyInjector<BukkitModule> MODULE_DEPENDENCY_INJECTOR = new ModuleDependencyInjector();

    static void onEnable(ModuleContainer module) {
        module.getComponentContainer().clear();
        for (var component : createComponents(module.getHandle(BukkitModule.class))) {
            module.getComponentContainer().getComponents().put((Class<? extends SLModuleComponent<?>>) component.getClass(), (component));
        }

        MODULE_DEPENDENCY_INJECTOR.inject(module.getHandle(BukkitModule.class));
        AUTO_REG.attach(module);
        initCommands(module);
    }

    static void onDisable(ModuleContainer module) {
        AUTO_REG.detach(module);

        if (module.getClass().getDeclaredAnnotation(CommandProvider.class) != null) {
            for (AbstractCommand cmd : module.getAttachment(ModuleCommandHolder.class).getCommands()) {
                Starlight.instance().getCommandManager().unregister(cmd);
            }
        }

        module.getComponentContainer().getComponents().clear();
    }

    static <E> Set<SubComponent<E>> createComponents(E holder) {
        var components = new HashSet<SubComponent<E>>();

        Annotations.matchAnnotation(holder, ComponentProvider.class, (a) -> {
            for (var clazz : a.value()) {
                SubComponent<E> component;

                try {
                    component = (SubComponent<E>) clazz.getDeclaredConstructor().newInstance();
                } catch (NoClassDefFoundError ignored) {
                    continue;
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }

                try {
                    component.checkCompatibility();
                } catch (APIIncompatibleException ignored) {
                    continue;
                }

                component.ctx(holder);

                components.add(component);
            }
        });

        return components;
    }

    @SuppressWarnings({"rawtypes"})
    static void initCommands(ModuleContainer handle) {
        var module = handle.getHandle(BukkitModule.class);

        if (!module.getClass().isAnnotationPresent(CommandProvider.class)) {
            return;
        }

        handle.getAttachment(ModuleCommandHolder.class).getCommands().clear();

        var annotation = module.getClass().getAnnotation(CommandProvider.class);

        for (Class<? extends AbstractCommand> commandClass : annotation.value()) {
            AbstractCommand cmd;
            try {
                cmd = commandClass.getConstructor().newInstance();
                if (cmd instanceof ModuleCommand c) {
                    c.initContext(handle.getHandle(BukkitModule.class));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            StarlightCommandManager.getInstance().register(cmd);
            handle.getAttachment(ModuleCommandHolder.class).getCommands().add(cmd);
        }
    }

    //todo: 拆分公用部分
    final class ModuleDependencyInjector extends DependencyInjector<BukkitModule> {
        public ModuleDependencyInjector() {
            Function<String[], Boolean> useCacheForAsset = (p) -> p.length == 1 || Boolean.parseBoolean(p[1]);

            registerInjector(Asset.class, (p, m) -> new Asset(m.owner(), p[0], useCacheForAsset.apply(p)));
            registerInjector(AssetGroup.class, (p, m) -> new AssetGroup(m.owner(), p[0], useCacheForAsset.apply(p)));
            registerInjector(Permission.class, (p, m) -> PermissionService.createPermissionObject(p[0]));

            registerInjector(ApplicationPackage.class, (p, m) -> m.parent());
            registerInjector(Plugin.class, (p, m) -> m.owner(Plugin.class));

            registerInjector(Logger.class, (p, m) -> m.handle().getLogger());
            registerInjector(LanguageEntry.class, (p, m) -> m.language());
            registerInjector(LanguageItem.class, (p, m) -> Starlight.lang().item(m.parent().meta().id(), m.id(), p[0]));

            registerInjector(SimpleRegionService.class, (a, m) -> {
                var service = new SimpleRegionService(a[1]);
                try {
                    service.init(JDBCService.getDB(a[0]).orElseThrow());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return service;
            });
            registerInjector(WaypointService.class, (a, m) -> {
                var service = new WaypointService(a[1]);
                try {
                    service.init(JDBCService.getDB(a[0]).orElseThrow());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return service;
            });
            registerInjector(BanEntryService.class, (a, m) -> {
                var service = new BanEntryService(a[1]);
                try {
                    service.init(JDBCService.getDB(a[0]).orElseThrow());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return service;
            });
            registerInjector(FlexibleMapService.class, (a, m) -> {
                var service = new FlexibleMapService(a[1]);
                try {
                    service.init(JDBCService.getDB(a[0]).orElseThrow());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return service;
            });
        }

        @Override
        public <T> T createInjection(Class<T> type, BukkitModule owner, String argument) {
            return super.createInjection(type, owner, argument.replace("/", ";"));
        }
    }

    final class ModuleAutoRegManager extends AutoRegisterManager<Listener> {
        public ModuleAutoRegManager() {
            Builder.build(this, (i) -> {
                i.attach(Registrations.SERVER_EVENT, BukkitUtil::registerEventListener);
                i.detach(Registrations.SERVER_EVENT, BukkitUtil::unregisterEventListener);
                i.attach(Registrations.PLUGIN_MESSAGE, PluginMessenger.EVENT_BUS::registerEventListener);
                i.detach(Registrations.PLUGIN_MESSAGE, PluginMessenger.EVENT_BUS::unregisterEventListener);
                i.attach(Registrations.PLUGIN_VPN_EVENT, Builder.apmService((l, s) -> s.registerEventHandler(l)));
                i.detach(Registrations.PLUGIN_VPN_EVENT, Builder.apmService((l, s) -> s.removeMessageHandler(l)));
                i.attach(Registrations.PLUGIN_VPN_LISTENER, Builder.apmEvent((l, s) -> s.addListener(l)));
                i.detach(Registrations.PLUGIN_VPN_LISTENER, Builder.apmEvent((l, s) -> s.removeListener(l)));
                i.attach(Registrations.CLIENT_MESSAGE, (l) -> System.out.println("deprecated register: client message API"));
                i.detach(Registrations.CLIENT_MESSAGE, (l) -> System.out.println("deprecated register: client message API"));
            });
        }

        public void attach(ModuleContainer object) {
            this.attach(object.getHandle(BukkitModule.class));

            for (var component : object.getComponentContainer().getComponents().values()) {
                this.attach((Listener) component);
            }
        }

        public void detach(ModuleContainer object) {
            this.detach(object.getHandle(BukkitModule.class));

            for (var component : object.getComponentContainer().getComponents().values()) {
                this.detach((Listener) component);
            }
        }

        @Override
        public void handleAttachFailed(Listener object, String type) {
            System.out.println("no module service named " + type);
        }

        @Override
        public void handleDetachFailed(Listener object, String type) {
            System.out.println("no module service named " + type);
        }

        private static final class Builder {
            private final Map<String, Consumer<Listener>> attachFunctions = new HashMap<>();
            private final Map<String, Consumer<Listener>> detachFunctions = new HashMap<>();

            static void build(AutoRegisterManager<Listener> target, Consumer<Builder> func) {
                var builder = new Builder();
                func.accept(builder);
                builder.build(target);
            }

            public static Consumer<Listener> apmService(BiConsumer<Listener, IRemoteMessageService> func) {
                return listener -> func.accept(listener, RemoteMessageService.instance());
            }

            public static Consumer<Listener> apmEvent(BiConsumer<RemoteEventListener, MessengerEventChannel> func) {
                return listener -> func.accept((RemoteEventListener) listener, RemoteMessageService.instance().eventChannel());
            }

            public void attach(String id, Consumer<Listener> function) {
                attachFunctions.put(id, function);
            }

            public void detach(String id, Consumer<Listener> function) {
                detachFunctions.put(id, function);
            }

            public void build(AutoRegisterManager<Listener> target) {
                for (var s : attachFunctions.keySet()) {
                    target.registerHandler(s, this.attachFunctions.get(s), this.detachFunctions.get(s));
                }
            }
        }
    }
}
