package org.atcraftmc.starlight.util;

import me.gb2022.apm.local.PluginMessenger;
import me.gb2022.apm.remote.event.MessengerEventChannel;
import me.gb2022.apm.remote.event.RemoteEventListener;
import me.gb2022.commons.reflect.AutoRegisterManager;
import me.gb2022.modular.Registrations;
import me.gb2022.modular.module.AppModule;
import me.gb2022.modular.module.ModuleContainer;
import org.atcraftmc.starlight.shared.service.IRemoteMessageService;
import org.atcraftmc.starlight.shared.service.RemoteMessageService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PluginAutoRegManager extends AutoRegisterManager<Object> {
    public PluginAutoRegManager() {
        init();
        initCustom();
    }

    public final void attachModuleContainer(ModuleContainer object) {
        this.attach(object.getHandle(AppModule.class));

        for (var component : object.getComponentContainer().getComponents().values()) {
            this.attach(component);
        }
    }

    public final void detachModuleContainer(ModuleContainer object) {
        this.detach(object.getHandle(AppModule.class));

        for (var component : object.getComponentContainer().getComponents().values()) {
            this.detach(component);
        }
    }

    @Override
    public final void init() {
        Builder.build(this, (i) -> {
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

    @Override
    public void handleAttachFailed(Object object, String type) {
        System.out.println(this + " > no module service named " + type);
    }

    @Override
    public void handleDetachFailed(Object object, String type) {
        System.out.println("no module service named " + type);
    }


    public void initCustom() {
    }

    protected static final class Builder {
        private final Map<String, Consumer<Object>> attachFunctions = new HashMap<>();
        private final Map<String, Consumer<Object>> detachFunctions = new HashMap<>();

        public static void build(AutoRegisterManager<Object> target, Consumer<Builder> func) {
            var builder = new Builder();
            func.accept(builder);
            builder.build(target);
        }

        public static Consumer<Object> apmService(BiConsumer<Object, IRemoteMessageService> func) {
            return Object -> func.accept(Object, RemoteMessageService.instance());
        }

        public static Consumer<Object> apmEvent(BiConsumer<RemoteEventListener, MessengerEventChannel> func) {
            return Object -> func.accept((RemoteEventListener) Object, RemoteMessageService.instance().eventChannel());
        }

        public void attach(String id, Consumer<Object> function) {
            attachFunctions.put(id, function);
        }

        public void detach(String id, Consumer<Object> function) {
            detachFunctions.put(id, function);
        }

        public void build(AutoRegisterManager<Object> target) {
            for (var s : attachFunctions.keySet()) {
                target.registerHandler(s, this.attachFunctions.get(s), this.detachFunctions.get(s));
            }
        }
    }
}
