package org.atcraftmc.starlight.internal.command;

import me.gb2022.commons.TriState;
import me.gb2022.modular.ObjectOperationResult;
import me.gb2022.modular.pack.ApplicationPackage;
import me.gb2022.modular.pack.PackageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.atcraftmc.qlib.command.QuarkCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.starlight.ProductInfo;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.foundation.TextSender;
import org.atcraftmc.starlight.foundation.command.CoreCommand;
import org.atcraftmc.starlight.framework.SLPluginConcept;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@QuarkCommand(name = "package", permission = "-starlight.packages")
public final class PackageCommand extends CoreCommand {
    private final PackageManager handle = Starlight.instance().context().getPackageManager();


    static String messageId(ObjectOperationResult result, String success) {
        return switch (result) {
            case SUCCESS -> success;
            case NOT_FOUND -> "not-found";
            case ALREADY_OPERATED -> "already-op";
            case INTERNAL_ERROR -> "internal-error";
            case BLOCKED_INTERNAL -> "blocked-internal";
        };
    }

    private static @NotNull String getPackageDisplayHover(ApplicationPackage pkg, String owner, String ownerVer) {
        var service = pkg.getServices();
        var module = pkg.getModules();
        return """
                &7ID: &b%s
                &7Owner: &a%s
                &7Service: %s
                &7Modules: %s
                &f
                &8[click to view modules]
                """.formatted(
                pkg.meta().id(),
                owner + ":" + ownerVer,
                service == null ? "&7[empty]" : "&a" + service.size(),
                module == null ? "&7[empty]" : "&a" + module.size()
        );
    }

    private void sendMessage(CommandSender sender, String id, Object... fmt) {
        this.getLanguage().item(id).send(sender, fmt);
    }

    @Override
    public void suggest(CommandSuggestion suggestion) {
        suggestion.suggest(0, "enable", "disable", "list");
        suggestion.matchArgument(0, "list", (c) -> c.suggest(1, "<search meta>"));
        suggestion.matchArgument(0, "list", (c) -> c.suggest(1, this.handle.getPackages().keySet()));
        suggestion.matchArgument(0, "enable", (c) -> c.suggest(1, this.handle.getIdsByStatus(TriState.TRUE)));
        suggestion.matchArgument(0, "disable", (c) -> c.suggest(1, this.handle.getIdsByStatus(TriState.FALSE)));
    }

    @Override
    public void execute(CommandExecution context) {
        var sender = context.getSender();
        var id = !context.hasArgumentAt(1) ? null : context.requireArgumentAt(1);

        switch (context.requireEnum(0, "list", "info", "enable", "disable", "reload", "enable-all", "disable-all", "reload-all")) {
            case "list" -> list(sender, !context.hasArgumentAt(1) ? "" : context.requireArgumentAt(1));
            case "enable-all" -> {
                this.handle.enableAll();
                this.getLanguage().item("enable-all").send(sender);
            }
            case "disable-all" -> {
                this.handle.disableAll();
                this.getLanguage().item("disable-all").send(sender);
            }
            case "enable" -> sendMessage(sender, messageId(this.handle.enable(id), "enable"), id);
            case "disable" -> sendMessage(sender, messageId(this.handle.disable(id), "disable"), id);
        }
    }

    private void listPackages(CommandSender sender) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, List<String>> map = new HashMap<>();
        for (String s : this.handle.getAllPackages().keySet().stream().sorted().toList()) {
            var namespace = this.handle.get(s).holder(SLPluginConcept.class).name();
            if (!map.containsKey(namespace)) {
                map.put(namespace, new ArrayList<>());
            }
            map.get(namespace).add(s);
        }

        for (String namespace : map.keySet()) {
            List<String> list = map.get(namespace);
            sb.append(ChatColor.GOLD)
                    .append(namespace)
                    .append("@")
                    .append(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin(namespace)).getDescription().getVersion())
                    .append("(")
                    .append(list.size())
                    .append("):\n");
            for (String id : list) {
                sb.append(ChatColor.RESET).append(" - ");
                if (this.handle.getStatus(id) == TriState.FALSE) {
                    sb.append(ChatColor.GREEN);
                } else {
                    sb.append(ChatColor.GRAY);
                }
                sb.append(id);
                sb.append('\n');
            }
        }

        this.getLanguage().item("list").send(sender, sb.toString());
    }

    private Component buildModuleInfo(ApplicationPackage pkg) {
        var state = this.handle.isEnabled(pkg.meta().id()) ? "&aE" : "&cD";
        var owner = pkg.holder(SLPluginConcept.class).name();
        //var ownerVer = pkg.holder(Plugin.class).getDescription().getVersion();
        var line = "&f[%s&f]%s".formatted(state, pkg.meta().id());

        var command = "/starlight module list %s";
        var hover = getPackageDisplayHover(pkg, owner, ProductInfo.version());

        return Component.text(ChatColor.translateAlternateColorCodes('&', line))
                .clickEvent(ClickEvent.runCommand(command.formatted(pkg.meta().id())))
                .hoverEvent(HoverEvent.showText(Component.text(ChatColor.translateAlternateColorCodes('&', hover))));
    }

    private void list(CommandSender sender, String prefix) {
        var nodes = this.handle.getPackages()
                .values()
                .stream()
                .sorted(Comparator.comparing(m -> m.holder(SLPluginConcept.class).name()))
                .filter((m) -> m.meta().id().contains(prefix))
                .toList();
        getLanguage().item("list").send(sender, "");
        for (var meta : nodes) {
            Component msg = buildModuleInfo(meta);
            TextSender.sendMessage(sender, msg);
        }
    }

    @Override
    public String getLanguageNamespace() {
        return "package";
    }
}
