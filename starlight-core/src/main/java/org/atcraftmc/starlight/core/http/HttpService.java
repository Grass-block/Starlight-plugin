package org.atcraftmc.starlight.core.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import me.gb2022.commons.container.ObjectContainer;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.Service;
import me.gb2022.gluon.service.ServiceInject;
import org.apache.logging.log4j.Logger;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.config.Configurations;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationService(id = "http-server")
public interface HttpService extends Service {
    Logger LOGGER = SLPluginEnvironment.createLogger("HttpService");

    ObjectContainer<HttpService> INSTANCE = new ObjectContainer<>();

    static Optional<HttpService> instance() {
        return Optional.ofNullable(INSTANCE.get());
    }

    @ServiceInject
    static void start() {
        var config = Configurations.standalone("http-server");

        if (config.getBoolean("enabled")) {
            LOGGER.info("HTTP Server disabled. to config, see <plugin>/config/http-server.yml");
        }

        var port = config.getInt("port");
        var threads = config.getInt("threads");

        INSTANCE.set(new NettyHTTPService(port, threads));
        try {
            INSTANCE.get().enable();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("HTTP Server started on {} using {} threads.", port, threads);
    }

    @ServiceInject
    static void stop() {
        try {
            INSTANCE.get().disable();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void addHandler(String path, HttpHandler handler);

    void removeHandler(String id);

    final class NettyHTTPService implements HttpService {
        private final Map<String, HttpHandler> handlers = new ConcurrentHashMap<>();
        private final NioEventLoopGroup boss;
        private final NioEventLoopGroup worker = new NioEventLoopGroup();
        private final ServerBootstrap bootstrap = new ServerBootstrap();
        private final int port;
        private List<String> sortedKeys = new ArrayList<>();

        public NettyHTTPService(int port, int threads) {
            this.boss = new NioEventLoopGroup(threads);
            this.port = port;
        }

        @Override
        public void enable() throws Exception {
            this.bootstrap.group(this.boss, this.worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) {
                            var p = ch.pipeline();

                            p.addLast(new HttpServerCodec());
                            p.addLast(new HttpObjectAggregator(1024 * 1024));
                            p.addLast(new ChunkedWriteHandler());

                            p.addLast(new NettyHttpHandler(NettyHTTPService.this));
                        }
                    });

            this.bootstrap.bind(this.port);

            addHandler("/hello", (request, ctx) -> {
                var responseDOM = """
                        <h1>Hello World</h1>
                        <p>request: %s</p>
                        """.formatted(request.uri());

                var content = Unpooled.copiedBuffer(
                        responseDOM,
                        StandardCharsets.UTF_8
                );

                HttpResponses.header(ctx, content.writerIndex(), (h) -> {});

                ctx.write(content);

                HttpResponses.end(ctx);
            });
        }

        private void updateHandlers() {
            this.sortedKeys = this.handlers.keySet().stream().sorted(Comparator.comparingInt((String s) -> (int) s.chars()
                    .filter(ch -> ch == '/')
                    .count()).thenComparing(Comparator.naturalOrder())).toList();
        }

        @Override
        public void addHandler(String id, HttpHandler handler) {
            this.handlers.put(id, handler);
            updateHandlers();
        }

        @Override
        public void removeHandler(String id) {
            this.handlers.remove(id);
            updateHandlers();
        }

        @Override
        public void disable() throws Exception {
            this.boss.shutdownGracefully();
            this.worker.shutdownGracefully();
        }
    }

    final class NettyHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        private final NettyHTTPService service;

        public NettyHttpHandler(NettyHTTPService service) {
            this.service = service;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
            var uri = request.uri();

            LOGGER.info("{} -> {}", ctx.channel().remoteAddress(), uri);

            for (var s : this.service.sortedKeys) {
                if (!uri.startsWith(s)) {
                    continue;
                }

                var handler = this.service.handlers.get(s);

                try {
                    handler.request(request, ctx);
                } catch (Exception e) {
                    LOGGER.error("An error occurred while handling request", e);
                    LOGGER.catching(e);
                }

                return;
            }

            HttpResponses.error(ctx, HttpResponseStatus.NOT_FOUND);
        }
    }
}
