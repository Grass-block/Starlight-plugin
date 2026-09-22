package org.atcgroup.starlight.bundle.mission;

import me.gb2022.gluon.module.ApplicationModule;
import org.atcgroup.starlight.bundle.mission.commission.Commission;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.starlight.api.event.PlayerReadyEvent;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.framework.module.SLCommandModule;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationModule(id = "commission")
@BukkitCommand(name = "commission")
public final class Commissions extends SLCommandModule {
    private static final int PAGE_SIZE = 10;
    private static final String[] FILTERS = {"user:owned", "user:participated", "type:", "name:", "page:0"};

    @EventHandler
    public void onPlayerReady(final PlayerReadyEvent event) {
        final var player = event.getPlayer();

        QLib.task().async().run(() -> {
            if (CommissionService.instance().list().isEmpty()) {
                return;
            }
            language().item("remind-new").send(QLib.audience(player));
        });
    }

    @Override
    public void suggest(CommandSuggestion suggestion) {
        suggestion.suggest(0, "list", "info", "join", "leave", "kick", "complete");

        suggestion.matchArgument(0, "list", ctx -> {
            for (int i = 1; i <= 5; i++) {
                ctx.suggest(i, FILTERS);
            }
        });

        suggestion.matchArgument(0, "info", ctx -> ctx.suggest(1, openUuids()));
        suggestion.matchArgument(0, "join", ctx -> ctx.suggest(1, openUuids()));
        suggestion.matchArgument(0, "leave", ctx -> ctx.suggest(1, openUuids()));
        suggestion.matchArgument(0, "complete", ctx -> ctx.suggest(1, openUuids()));
        suggestion.matchArgument(0, "kick", ctx -> {
            ctx.suggest(1, openUuids());
            ctx.suggestPlayers(2);
        });
    }

    private Set<String> openUuids() {
        return CommissionService.instance().list().stream().map((c) -> c.getUuid().toString()).collect(Collectors.toSet());
    }

    @Override
    public void execute(CommandExecution context) {
        var sender = context.requireSenderAsPlayer();

        switch (context.requireEnum(0, "list", "info", "join", "leave", "kick", "complete")) {
            case "list" -> {
                var args = new HashSet<String>();
                var raw = context.getArgs();

                if (raw.length > 1) {
                    args.addAll(Arrays.asList(raw).subList(1, raw.length));
                }

                search(sender, args);
            }
            case "info" -> {
                try {
                    info(sender, UUID.fromString(context.requireArgumentAt(1)));
                } catch (IllegalArgumentException e) {
                    this.sendExceptionMessage(sender);
                }
            }
            case "join" -> {
                try {
                    join(sender, UUID.fromString(context.requireArgumentAt(1)));
                } catch (IllegalArgumentException e) {
                    this.sendExceptionMessage(sender);
                }
            }
            case "leave" -> {
                try {
                    leave(sender, UUID.fromString(context.requireArgumentAt(1)));
                } catch (IllegalArgumentException e) {
                    this.sendExceptionMessage(sender);
                }
            }
            case "kick" -> {
                try {
                    kick(sender, UUID.fromString(context.requireArgumentAt(1)), context.requireArgumentAt(2));
                } catch (IllegalArgumentException e) {
                    this.sendExceptionMessage(sender);
                }
            }
            case "complete" -> {
                try {
                    complete(sender, UUID.fromString(context.requireArgumentAt(1)));
                } catch (IllegalArgumentException e) {
                    this.sendExceptionMessage(sender);
                }
            }
        }
    }

    public void search(Player sender, Set<String> args) {
        var audience = QLib.audience(sender);

        sender.sendMessage(QLib.textEngine().renderString("{#line}"));
        language().item("list-header").send(audience);

        var stream = CommissionService.instance().list().stream();
        var page = 0;

        for (var arg : args) {
            if (arg == null || !arg.contains(":")) {
                continue;
            }

            var split = arg.split(":", 2);
            var key = split[0];
            var value = split[1];

            switch (key) {
                case "user" -> {
                    if (Objects.equals(value, "owned")) {
                        stream = stream.filter((c) -> c.getCreator().equals(sender.getUniqueId()));
                    } else if (Objects.equals(value, "participated")) {
                        stream = stream.filter((c) -> c.getParticipants().contains(sender.getUniqueId()));
                    }
                }
                case "type" -> {
                    var ids = CommissionService.instance().byType(value).stream()
                            .map(Commission::getUuid)
                            .collect(Collectors.toSet());
                    stream = stream.filter((c) -> ids.contains(c.getUuid()));
                }
                case "name" -> stream = stream.filter((c) -> c.getName() != null && c.getName().contains(value));
                case "page" -> {
                    try {
                        page = Math.max(0, Integer.parseInt(value));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        var entries = stream.skip((long) page * PAGE_SIZE).limit(PAGE_SIZE).toList();

        for (var commission : entries) {
            language().item("list-entry").send(audience, commission.getName(), commission.getUuid(), commission.getUuid());
        }

        language().item("list-control").send(audience, pageCommand(args, page - 1), pageCommand(args, page + 1));

        sender.sendMessage(QLib.textEngine().renderString("{#line}"));
    }

    private String pageCommand(Set<String> args, int page) {
        var builder = new StringBuilder("/commission list");

        for (var arg : args) {
            if (arg == null || arg.startsWith("page:")) {
                continue;
            }
            builder.append(' ').append(arg);
        }

        return builder.append(" page:").append(Math.max(0, page)).toString();
    }

    public void info(Player sender, UUID uuid) {
        var optional = CommissionService.instance().byUUID(uuid);

        if (optional.isEmpty()) {
            MessageAccessor.send(this.language(), sender, "info-not-exist", uuid);
            return;
        }

        var commission = optional.get();
        var locale = LocaleService.locale(sender);
        var type = this.language().handle().item(commission.getType(CommissionService.instance().registry())).raw(locale);

        sender.sendMessage(QLib.textEngine().renderString("{#line}"));
        MessageAccessor.send(this.language(), sender, "info-name", commission.getName(), commission.getStatus().name());
        MessageAccessor.send(this.language(), sender, "info-creator", commission.getCreator());
        MessageAccessor.send(this.language(), sender, "info-type", type);
        MessageAccessor.send(this.language(), sender, "info-max", commission.getParticipantLimit());
        MessageAccessor.send(this.language(), sender, "info-other", commission.getFormattedMetadata());
        MessageAccessor.send(this.language(), sender, "info-description", commission.getDesc());
        MessageAccessor.send(this.language(), sender, "info-uuid", commission.getUuid());
        sender.sendMessage(QLib.textEngine().renderString("{#line}"));
    }

    public void join(Player sender, UUID uuid) {
        CommissionService.instance().join(uuid, sender);
        language().item("join").send(QLib.audience(sender));
    }

    public void leave(Player sender, UUID uuid) {
        CommissionService.instance().leave(uuid, sender);
        language().item("leave").send(QLib.audience(sender));
    }

    public void kick(Player sender, UUID uuid, String targetName) {
        var optional = CommissionService.instance().byUUID(uuid);

        if (optional.isEmpty() || !optional.get().getCreator().equals(sender.getUniqueId())) {
            this.sendExceptionMessage(sender);
            return;
        }

        var target = Bukkit.getPlayerExact(targetName);

        if (target == null) {
            this.sendExceptionMessage(sender);
            return;
        }

        CommissionService.instance().kick(uuid, target);
        language().item("kick").send(QLib.audience(sender), target.getName());
    }

    public void complete(Player sender, UUID uuid) {
        var optional = CommissionService.instance().byUUID(uuid);

        if (optional.isEmpty() || !optional.get().getCreator().equals(sender.getUniqueId())) {
            this.sendExceptionMessage(sender);
            return;
        }

        CommissionService.instance().close(uuid);
        language().item("complete").send(QLib.audience(sender));
    }
}
