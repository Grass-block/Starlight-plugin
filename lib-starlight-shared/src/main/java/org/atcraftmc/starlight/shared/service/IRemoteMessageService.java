package org.atcraftmc.starlight.shared.service;

import io.netty.buffer.ByteBuf;
import me.gb2022.apm.remote.RemoteMessenger;
import me.gb2022.apm.remote.RemoteQuery;
import me.gb2022.apm.remote.connector.RemoteConnector;
import me.gb2022.apm.remote.event.MessengerEventChannel;
import me.gb2022.apm.remote.event.channel.MessageChannel;
import me.gb2022.modular.service.Service;

import java.util.function.Consumer;

public interface IRemoteMessageService extends Service {
    void registerEventHandler(Object handler);

    void registerEventHandler(Class<?> handler);

    void removeMessageHandler(Object handler);

    void removeMessageHandler(Class<?> handler);

    RemoteConnector getConnector();

    RemoteMessenger getMessenger();

    String getIdentifier();

    MessengerEventChannel eventChannel();

    MessageChannel messageChannel(String channel);

    String message(String uuid, String target, String channel, ByteBuf msg);

    String message(String target, String channel, ByteBuf msg);

    String broadcast(String channel, ByteBuf msg);

    RemoteQuery<ByteBuf> query(String target, String channel, ByteBuf msg);

    String message(String uuid, String target, String channel, Consumer<ByteBuf> writer);

    String message(String target, String channel, Consumer<ByteBuf> writer);

    String broadcast(String channel, Consumer<ByteBuf> writer);

    RemoteQuery<ByteBuf> query(String target, String channel, Consumer<ByteBuf> writer);

    <I> String message(String uuid, String target, String channel, I object);

    <I> String message(String target, String channel, I object);

    <I> String broadcast(String channel, I object);

    <I> RemoteQuery<I> query(String target, String channel, I msg);
}
