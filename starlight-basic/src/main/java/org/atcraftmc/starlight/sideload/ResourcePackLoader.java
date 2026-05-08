package org.atcraftmc.starlight.sideload;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.stream.ChunkedFile;
import me.gb2022.commons.file.FilePath;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import net.kyori.adventure.text.Component;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.core.command.CommandProvider;
import org.atcraftmc.starlight.core.command.ModuleCommand;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.core.http.HttpResponses;
import org.atcraftmc.starlight.core.http.HttpService;
import org.atcraftmc.starlight.core.http.HttpHandler;
import org.atcraftmc.starlight.sideload.resource.LocalPackManager;
import org.atcraftmc.starlight.sideload.resource.ResourcePackSourceInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.RandomAccessFile;

@ApplicationModule(id = "resource-pack-loader")
@AutoRegister({Registrations.SERVER_EVENT})
@CommandProvider(ResourcePackLoader.GetResourceCommand.class)
public final class ResourcePackLoader extends BukkitAbstractModule implements HttpHandler {
    private static final FilePath PATH = SLPluginEnvironment.getPathManager().getCurrentPluginFolder().append("assets").append(
            "resource-packs");
    private final LocalPackManager localPackManager = new LocalPackManager(PATH);

    @Override
    public void enable() throws Exception {
        this.localPackManager.update();

        HttpService.instance().ifPresent((i) -> i.addHandler("/resource-pack", this));
    }

    @Override
    public void disable() throws Exception {
        super.disable();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.localPackManager.isEmpty()) {
            return;
        }

        sendResourcePack(event.getPlayer());
    }

    public void sendResourcePack(Player player) {
        try {
            var prompt = Component.text("");//language().item("prompt").component(LocaleService.locale(player)).asComponent();
            var sha = ResourcePackSourceInfo.fileSHA1Raw(this.localPackManager.getCompiledFile());
            var uri = "http://localhost:8080/resource-pack";

            player.setResourcePack(uri, sha, prompt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void request(FullHttpRequest request, ChannelHandlerContext ctx) {
        var file = this.localPackManager.getCompiledFile();

        if (!file.exists() || file.length() == 0) {
            HttpResponses.error(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);

            return;
        }

        try (var raf = new RandomAccessFile(file, "r")) {
            HttpResponses.header(ctx, raf.length(), (h) -> {
                h.set(HttpHeaderNames.CONTENT_TYPE, "application/zip");
                h.set(HttpHeaderNames.USER_AGENT, "starlight::" + Starlight.instance().getInstanceUUID());
                h.set(HttpHeaderNames.ACCEPT_RANGES, "none");
                h.set(HttpHeaderNames.CONNECTION, "close");
            });

            ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf))).sync();

            HttpResponses.end(ctx);
        } catch (Exception e) {
            HttpResponses.error(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @BukkitCommand(name = "get-resource")
    public static final class GetResourceCommand extends ModuleCommand<ResourcePackLoader> {
        @Override
        public void execute(CommandExecution context) {
            this.getModule().sendResourcePack(context.requireSenderAsPlayer());
        }
    }
}
