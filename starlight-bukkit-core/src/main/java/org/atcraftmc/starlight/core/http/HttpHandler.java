package org.atcraftmc.starlight.core.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface HttpHandler {
    void request(FullHttpRequest request, ChannelHandlerContext ctx);
}
