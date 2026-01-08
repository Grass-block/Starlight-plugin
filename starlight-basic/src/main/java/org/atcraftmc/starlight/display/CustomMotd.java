package org.atcraftmc.starlight.display;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.modular.APIIncompatibleException;
import me.gb2022.modular.Registrations;
import me.gb2022.modular.module.ApplicationModule;
import me.gb2022.modular.module.component.ComponentProvider;
import net.kyori.adventure.text.Component;
import org.atcraftmc.qlib.command.QuarkCommand;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.api.event.QueryPingEvent;
import org.atcraftmc.starlight.foundation.ComponentSerializer;
import org.atcraftmc.starlight.foundation.TextSender;
import org.atcraftmc.starlight.foundation.command.CommandProvider;
import org.atcraftmc.starlight.foundation.command.ModuleCommand;
import org.atcraftmc.starlight.foundation.command.PluginCommandExecutor;
import org.atcraftmc.starlight.foundation.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.SLModuleComponent;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.atcraftmc.starlight.shared.ConfigDataModel;
import org.atcraftmc.starlight.shared.Configurations;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.util.CachedServerIcon;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@ApplicationModule(id = "custom-motd", version = "1.0.2")
@CommandProvider({CustomMotd.MotdCommand.class})
@AutoRegister(Registrations.SERVER_EVENT)
@ComponentProvider(CustomMotd.ProtocolLibSender.class)
public final class CustomMotd extends BukkitAbstractModule implements PluginCommandExecutor {
    private CachedServerIcon cachedServerIcon;
    private YamlConfiguration setting;

    @Inject
    private LanguageEntry language;

    @Override
    public void enable() {
        this.refreshIcon();
        this.refreshText();
    }

    private void refreshText() {
        try (var i = new FileInputStream(Configurations.file("motd.yml", true))) {
            this.setting = YamlConfiguration.loadConfiguration(new InputStreamReader(i));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshIcon() {
        try {
            this.cachedServerIcon = Bukkit.loadServerIcon(Configurations.file("motd.png", true));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Component getMessage() {
        return ConfigDataModel.motd(this.setting);
    }

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        try {
            e.motd(getMessage());
        } catch (Error ex) {
            e.setMotd(getMessage().toString());
        }

        if (this.cachedServerIcon == null) {
            return;
        }
        e.setServerIcon(this.cachedServerIcon);
    }

    @EventHandler
    public void onPing(QueryPingEvent e) {
        e.setMotd(ComponentSerializer.legacy(getMessage()));

        if (this.cachedServerIcon == null) {
            return;
        }
        e.setServerIcon(this.cachedServerIcon);
    }


    @Override
    public void onCommand(CommandSender sender, String[] args) {
        switch (args[0]) {
            case "refresh-icon" -> {
                refreshIcon();
                MessageAccessor.send(this.language, sender, "icon-refresh");
            }
            case "refresh-text" -> {
                this.refreshText();
                MessageAccessor.send(this.language, sender, "text-refresh");
            }
            case "text" -> {
                MessageAccessor.send(this.language, sender, "motd-command");
                TextSender.sendMessage(sender, getMessage());
            }
        }
    }

    @Override
    public void onCommandTab(CommandSender sender, String[] buffer, List<String> tabList) {
        if (buffer.length == 1) {
            tabList.add("refresh-icon");
            tabList.add("refresh-text");
            tabList.add("text");
        }
    }

    @QuarkCommand(name = "motd", permission = "-quark.motd.command")
    public static final class MotdCommand extends ModuleCommand<CustomMotd> {
        @Override
        public void init(CustomMotd module) {
            this.setExecutor(module);
        }
    }

    public static final class ProtocolLibSender extends SLModuleComponent<CustomMotd> {
        private PacketAdapter handler;

        @Override
        public void checkCompatibility() throws APIIncompatibleException {
            Compatibility.requireClass(() -> Class.forName("com.comphenix.protocol.ProtocolLibrary"));
        }

        @Override
        public void enable() {
            this.handler = new PacketAdapter(Starlight.instance(), PacketType.Status.Server.OUT_SERVER_INFO) {
                @Override
                public void onPacketSending(PacketEvent e) {
                    WrappedServerPing ping = e.getPacket().getServerPings().read(0);
                    ping.setMotD(WrappedChatComponent.fromJson(ComponentSerializer.json(parent.getMessage())));
                }
            };
            ProtocolLibrary.getProtocolManager().addPacketListener(this.handler);
        }

        @Override
        public void disable() {
            ProtocolLibrary.getProtocolManager().removePacketListener(this.handler);
        }
    }

}