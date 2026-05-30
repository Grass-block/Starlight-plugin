package org.atcraftmc.starlight.internal;

import me.gb2022.commons.TriState;
import me.gb2022.commons.reflect.AutoRegister;
import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.Registrations;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.atcraftmc.starlight.core.command.ModuleCommand;
import org.atcraftmc.starlight.core.command.PluginCommandExecutor;
import org.atcraftmc.starlight.framework.PluginApplication;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.atcraftmc.starlight.util.version.ModrinthVersionAPI;
import org.atcraftmc.starlight.util.version.VersionInfo;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.util.function.BiConsumer;

@ApplicationModule(id = "modrinth-version-check", internal = true, description = "Checks for plugin updates on Modrinth")
@AutoRegister(Registrations.SERVER_EVENT)
public final class ModrinthVersionCheck extends BukkitAbstractModule implements PluginCommandExecutor {
    String VERSION_PAGE = "https://modrinth.com/plugin/starlight-plugin/version/%s";

    @Inject("-starlight.version.announce")
    private Permission updateAnnounce;

    @Inject
    private LanguageEntry language;

    private TriState cachedState;
    private VersionInfo cachedVersion;

    @Override
    public void enable() {
        StarlightBukkitCore.instance().getCommandManager().getCommand("starlight").registerSubCommand(new CheckVersionCommand(this));

        check((state, version) -> {
        });
    }

    public void check(BiConsumer<TriState, VersionInfo> callback) {
        QLib.task().async().run(() -> {
            var latestVersion = ModrinthVersionAPI.checkVersion("");

            if (latestVersion == null) {
                return;
            }

            var currentVersion = VersionInfo.parse(Starlight.instance().getDescription().getVersion());
            this.cachedVersion = latestVersion;
            var result = currentVersion.compareTo(latestVersion);

            if (result == TriState.FALSE) {
                callback.accept(TriState.TRUE, latestVersion);
                this.cachedState = TriState.TRUE;
                return;
            }
            callback.accept(TriState.FALSE, currentVersion);
            this.cachedState = TriState.FALSE;
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission(this.updateAnnounce)) {
            return;
        }
        if (this.cachedState == TriState.TRUE) {
            String page = VERSION_PAGE.formatted(this.cachedVersion);
            this.language.item("require").send(QLib.audience(event.getPlayer()), this.cachedVersion, page);
        }
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 2) {
            var v1 = VersionInfo.parse(args[0]);
            var v2 = VersionInfo.parse(args[1]);
            var c = switch (v1.compareTo(v2)) {
                case TRUE -> ">=";
                case FALSE -> "<";
                default -> "??";
            };

            sender.sendMessage("[DEBUG] Compare: '%s' %s '%s'".formatted(v1, c, v2));

            return;
        }

        this.language.item("checking").send(QLib.audience(sender));
        this.check((state, version) -> {
            switch (state) {
                case TRUE -> language.item("require").send(QLib.audience(sender), version, VERSION_PAGE.formatted(this.cachedVersion));
                case FALSE -> language.item("no-require").send(QLib.audience(sender), version);
                case UNKNOWN -> language.item("exception").send(QLib.audience(sender));
            }
        });
    }

    @BukkitCommand(name = "check-version", permission = "-starlight.version.check")
    public static final class CheckVersionCommand extends ModuleCommand<ModrinthVersionCheck> {
        public CheckVersionCommand(ModrinthVersionCheck module) {
            setExecutor(module);
        }
    }
}
