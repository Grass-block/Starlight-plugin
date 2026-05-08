package org.atcraftmc.starlight.core.http;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public interface HttpResponses {
    static void dump(ChannelHandlerContext ctx, FullHttpRequest request) {
        var sb = new StringBuilder();

        sb.append("\n========== HTTP REQUEST ==========\n");

        sb.append("Remote Address: ").append(ctx.channel().remoteAddress()).append('\n');
        sb.append("Method: ").append(request.method()).append('\n');
        sb.append("URI: ").append(request.uri()).append('\n');
        sb.append("Protocol: ").append(request.protocolVersion()).append('\n');
        sb.append('\n');

        sb.append("----- Headers -----\n");

        for (var entry : request.headers()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }

        sb.append('\n');

        // Decoder Result
        sb.append("----- Decoder Result -----\n");

        sb.append("Success: ").append(request.decoderResult().isSuccess()).append('\n');

        if (!request.decoderResult().isSuccess()) {
            sb.append("Cause: ").append(request.decoderResult().cause()).append('\n');
        }

        sb.append('\n');

        // Content
        var content = request.content();

        sb.append("----- Content -----\n");
        sb.append("Readable Bytes: ").append(content.readableBytes()).append('\n');

        if (content.isReadable()) {
            var text = content.toString(StandardCharsets.UTF_8);
            sb.append(text).append('\n');
        }

        sb.append("==================================\n");

        System.out.println(sb);
    }

    static void header(ChannelHandlerContext ctx, long len, Consumer<HttpHeaders> headers){
        var response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        var head = response.headers();

        head.set(HttpHeaderNames.CONTENT_LENGTH, len);
        headers.accept(head);

        if(len == 0){
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return;
        }

        ctx.write(response);
    }

    static void end(ChannelHandlerContext ctx){
        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
    }

    static void error(ChannelHandlerContext ctx, HttpResponseStatus status){
        var response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
