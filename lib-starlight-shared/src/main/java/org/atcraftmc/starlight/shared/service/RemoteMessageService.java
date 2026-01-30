package org.atcraftmc.starlight.shared.service;

import io.netty.buffer.ByteBuf;
import me.gb2022.apm.remote.RemoteMessenger;
import me.gb2022.apm.remote.RemoteQuery;
import me.gb2022.apm.remote.connector.RemoteConnector;
import me.gb2022.apm.remote.event.MessengerEventChannel;
import me.gb2022.apm.remote.event.channel.MessageChannel;
import me.gb2022.modular.service.*;
import org.atcraftmc.starlight.shared.Configurations;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

@ApplicationService(id = "remote-message-service", export = true, impl = RemoteMessageService.class, layer = ServiceLayer.FRAMEWORK)
public final class RemoteMessageService implements Service, IRemoteMessageService {
    public static final Logger LOGGER = Logger.getLogger("Starlight:RemoteMessageService");
    public static final Runnable EMPTY_ACTION = () -> {};

    @ServiceInject
    public static final ServiceHolder<RemoteMessageService> INSTANCE = new ServiceHolder<>();

    private final IRemoteMessageService handle;

    public RemoteMessageService() {
        this.handle = create();
    }

    public static IRemoteMessageService create() {
        var config = Configurations.secret("plugin-vpn");

        if (!config.getBoolean("enabled")) {
            return new EmptyImplementation();
        }

        return new SimpleImplementation(
                config.getString("identifier"),
                new InetSocketAddress(Objects.requireNonNull(config.getString("address")), config.getInt("port")),
                config.getBoolean("exchange"),
                Objects.requireNonNull(config.getString("secret")).getBytes(StandardCharsets.UTF_8)
        );
    }

    public static IRemoteMessageService instance() {
        return INSTANCE.get();
    }

    public static RemoteMessenger messenger() {
        return instance().getMessenger();
    }

    static RemoteConnector connector() {
        return instance().getConnector();
    }

    public IRemoteMessageService getHandle() {
        return handle;
    }

    @Override
    public void registerEventHandler(Object handler) {
        this.handle.registerEventHandler(handler);
    }

    @Override
    public void registerEventHandler(Class<?> handler) {
        this.handle.registerEventHandler(handler);
    }

    @Override
    public void removeMessageHandler(Object handler) {
        this.handle.removeMessageHandler(handler);
    }

    @Override
    public void removeMessageHandler(Class<?> handler) {
        this.handle.removeMessageHandler(handler);
    }

    @Override
    public RemoteConnector getConnector() {
        return this.handle.getConnector();
    }

    @Override
    public RemoteMessenger getMessenger() {
        return this.handle.getMessenger();
    }

    @Override
    public String getIdentifier() {
        return this.handle.getIdentifier();
    }

    @Override
    public MessengerEventChannel eventChannel() {
        return this.handle.eventChannel();
    }

    @Override
    public MessageChannel messageChannel(String channel) {
        return this.handle.messageChannel(channel);
    }

    @Override
    public String message(String uuid, String target, String channel, ByteBuf msg) {
        return this.handle.message(uuid, target, channel, msg);
    }

    @Override
    public String message(String target, String channel, ByteBuf msg) {
        return this.handle.message(target, channel, msg);
    }

    @Override
    public String broadcast(String channel, ByteBuf msg) {
        return this.handle.broadcast(channel, msg);
    }

    @Override
    public RemoteQuery<ByteBuf> query(String target, String channel, ByteBuf msg) {
        return this.handle.query(target, channel, msg);
    }

    @Override
    public String message(String uuid, String target, String channel, Consumer<ByteBuf> writer) {
        return this.handle.message(uuid, target, channel, writer);
    }

    @Override
    public String message(String target, String channel, Consumer<ByteBuf> writer) {
        return this.handle.message(target, channel, writer);
    }

    @Override
    public String broadcast(String channel, Consumer<ByteBuf> writer) {
        return this.handle.broadcast(channel, writer);
    }

    @Override
    public RemoteQuery<ByteBuf> query(String target, String channel, Consumer<ByteBuf> writer) {
        return this.handle.query(target, channel, writer);
    }

    @Override
    public <I> String message(String uuid, String target, String channel, I object) {
        return this.handle.message(uuid, target, channel, object);
    }

    @Override
    public <I> String message(String target, String channel, I object) {
        return this.handle.message(target, channel, object);
    }

    @Override
    public <I> String broadcast(String channel, I object) {
        return this.handle.broadcast(channel, object);
    }

    @Override
    public <I> RemoteQuery<I> query(String target, String channel, I msg) {
        return this.handle.query(target, channel, msg);
    }

    static final class EmptyImplementation implements IRemoteMessageService {

        @Override
        public RemoteConnector getConnector() {
            throw new UnsupportedOperationException("Empty implementation");
        }

        @Override
        public RemoteMessenger getMessenger() {
            throw new UnsupportedOperationException("Empty implementation");
        }

        @Override
        public void registerEventHandler(Object handler) {
            LOGGER.warning("register on null handler");
        }

        @Override
        public void registerEventHandler(Class<?> handler) {
            LOGGER.warning("register on null handler");
        }

        @Override
        public void removeMessageHandler(Object handler) {
            LOGGER.warning("remove on null handler");
        }

        @Override
        public void removeMessageHandler(Class<?> handler) {
            LOGGER.warning("remove on null handler");
        }

        @Override
        public String getIdentifier() {
            return "!";
        }

        @Override
        public MessengerEventChannel eventChannel() {
            throw new UnsupportedOperationException("Empty implementation");
        }

        @Override
        public MessageChannel messageChannel(String channel) {
            throw new UnsupportedOperationException("Empty implementation");
        }

        @Override
        public String message(String uuid, String target, String channel, ByteBuf msg) {
            return UUID.randomUUID().toString();
        }

        @Override
        public String message(String target, String channel, ByteBuf msg) {
            return UUID.randomUUID().toString();
        }

        @Override
        public String broadcast(String channel, ByteBuf msg) {
            return UUID.randomUUID().toString();
        }

        @Override
        public RemoteQuery<ByteBuf> query(String target, String channel, ByteBuf msg) {
            return new RemoteQuery<>(UUID.randomUUID().toString(), ByteBuf.class, (s) -> {});
        }

        @Override
        public String message(String target, String channel, Consumer<ByteBuf> writer) {
            return UUID.randomUUID().toString();
        }

        @Override
        public String message(String uuid, String target, String channel, Consumer<ByteBuf> writer) {
            return UUID.randomUUID().toString();
        }

        @Override
        public String broadcast(String channel, Consumer<ByteBuf> writer) {
            return UUID.randomUUID().toString();
        }

        @Override
        public RemoteQuery<ByteBuf> query(String target, String channel, Consumer<ByteBuf> writer) {
            return new RemoteQuery<>(UUID.randomUUID().toString(), ByteBuf.class, (s) -> {});
        }

        @Override
        public <I> String message(String uuid, String target, String channel, I object) {
            return UUID.randomUUID().toString();
        }

        @Override
        public <I> String message(String target, String channel, I object) {
            return UUID.randomUUID().toString();
        }

        @Override
        public <I> String broadcast(String channel, I object) {
            return UUID.randomUUID().toString();
        }

        @Override
        public <I> RemoteQuery<I> query(String target, String channel, I msg) {
            return (RemoteQuery<I>) RemoteQuery.of(null, msg.getClass(), (uuid) -> {
                this.message(uuid, target, channel, msg);
            });
        }
    }

    static final class SimpleImplementation implements IRemoteMessageService {
        private final RemoteMessenger messenger;

        public SimpleImplementation(String id, InetSocketAddress address, boolean proxy, byte[] key) {
            this.messenger = new RemoteMessenger(proxy, id, address, key);
        }

        @Override
        public void disable() {
            this.messenger.stop();
        }

        @Override
        public void registerEventHandler(Object handler) {
            this.messenger.registerEventHandler(handler);
        }

        @Override
        public void registerEventHandler(Class<?> handler) {
            this.messenger.registerEventHandler(handler);
        }

        @Override
        public void removeMessageHandler(Object handler) {
            this.messenger.removeMessageHandler(handler);
        }

        @Override
        public void removeMessageHandler(Class<?> handler) {
            this.messenger.removeMessageHandler(handler);
        }

        @Override
        public RemoteConnector getConnector() {
            return this.messenger.connector();
        }

        @Override
        public RemoteMessenger getMessenger() {
            return messenger;
        }

        @Override
        public String getIdentifier() {
            return this.messenger.getIdentifier();
        }

        @Override
        public MessengerEventChannel eventChannel() {
            return this.messenger.eventChannel();
        }

        @Override
        public MessageChannel messageChannel(String channel) {
            return this.messenger.messageChannel(channel);
        }

        @Override
        public String message(String uuid, String target, String channel, ByteBuf msg) {
            return this.messenger.message(uuid, target, channel, msg);
        }

        @Override
        public String message(String target, String channel, ByteBuf msg) {
            return this.messenger.message(target, channel, msg);
        }

        @Override
        public String broadcast(String channel, ByteBuf msg) {
            return this.messenger.broadcast(channel, msg);
        }

        @Override
        public RemoteQuery<ByteBuf> query(String target, String channel, ByteBuf msg) {
            return this.messenger.query(target, channel, msg);
        }

        @Override
        public String message(String uuid, String target, String channel, Consumer<ByteBuf> writer) {
            return this.messenger.message(uuid, target, channel, writer);
        }

        @Override
        public String message(String target, String channel, Consumer<ByteBuf> writer) {
            return this.messenger.message(target, channel, writer);
        }

        @Override
        public String broadcast(String channel, Consumer<ByteBuf> writer) {
            return this.messenger.broadcast(channel, writer);
        }

        @Override
        public RemoteQuery<ByteBuf> query(String target, String channel, Consumer<ByteBuf> writer) {
            return this.messenger.query(target, channel, writer);
        }

        @Override
        public <I> String message(String uuid, String target, String channel, I object) {
            return this.messenger.message(uuid, target, channel, object);
        }

        @Override
        public <I> String message(String target, String channel, I object) {
            return this.messenger.message(target, channel, object);
        }

        @Override
        public <I> String broadcast(String channel, I object) {
            return this.messenger.broadcast(channel, object);
        }

        @Override
        public <I> RemoteQuery<I> query(String target, String channel, I msg) {
            return this.messenger.query(target, channel, msg);
        }
    }
}
