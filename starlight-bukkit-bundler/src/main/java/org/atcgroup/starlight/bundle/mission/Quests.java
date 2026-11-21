package org.atcgroup.starlight.bundle.mission;

import me.gb2022.commons.reflect.Inject;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcgroup.starlight.bundle.mission.api.Quest;
import org.atcraftmc.qlib.audience.PointedAudience;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.framework.module.SLCommandModule;
import org.atcraftmc.starlight.migration.MessageAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationModule(id = "quests")
public final class Quests extends SLCommandModule {
    private static final int CMD_PAGE_SIZE = 10;
    private static final String[] FILTERS = {"user:owned", "user:participated", "type:", "name:", "page:0"};

    @Inject("-starlight.quest.admin")
    private Permission adminPermission;

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
        return QuestService.instance().list().stream().map((c) -> c.getUuid().toString()).collect(Collectors.toSet());
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


    public void complete(Player sender, UUID uuid) {
        var optional = QuestService.instance().byUUID(uuid);

        if (optional.isEmpty() || !optional.get().getCreator().equals(sender.getUniqueId())) {
            language().item("not-found").send(QLib.audience(sender));
            return;
        }

        if (!optional.get().getCreator().equals(sender.getUniqueId()) && !sender.hasPermission(this.adminPermission)) {
            language().item("no-perm").send(QLib.audience(sender));
        }

        QuestService.instance().close(uuid);

        language().item("completed").send(QLib.audience(sender));
    }

    public void kick(Player sender, UUID uuid, String targetName) {
        var optional = QuestService.instance().byUUID(uuid);

        if (optional.isEmpty()) {
            language().item("not-found").send(QLib.audience(sender));
            return;
        }

        if (!optional.get().getCreator().equals(sender.getUniqueId()) && !sender.hasPermission(this.adminPermission)) {
            language().item("no-perm").send(QLib.audience(sender));
        }

        var target = Bukkit.getPlayerExact(targetName);

        if (target == null) {
            language().item("not-found-player").send(QLib.audience(sender), targetName);
            return;
        }

        leave(sender, uuid);

        language().item("kick").send(QLib.audience(sender), target.getName());
    }

    public void join(Player sender, UUID uuid) {
        QuestService.instance().join(uuid, sender);
        language().item("join").send(QLib.audience(sender));
    }

    public void leave(Player sender, UUID uuid) {
        QuestService.instance().leave(uuid, sender);
        language().item("leave").send(QLib.audience(sender));
    }

    public void search(Player sender, Set<String> args) {
        var audience = QLib.audience(sender);

        sender.sendMessage(QLib.textEngine().renderString("{#line}"));
        language().item("list-header").send(audience);

        var stream = QuestService.instance().list().stream();
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
                    var ids = QuestService.instance().byType(value).stream()
                                          .map(Quest::getUuid)
                                          .collect(Collectors.toSet());
                    stream = stream.filter((c) -> ids.contains(c.getUuid()));
                }
                case "page" -> {
                    try {
                        page = Math.max(0, Integer.parseInt(value));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        var entries = stream.skip((long) page * CMD_PAGE_SIZE).limit(CMD_PAGE_SIZE).toList();

        for (var commission : entries) {
            var name = commission.getDisplayName().message(((PointedAudience) audience).locale());
            language().item("list-entry")
                      .send(audience, name, commission.getUuid(), commission.getUuid());
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
        var optional = QuestService.instance().byUUID(uuid);

        if (optional.isEmpty()) {
            MessageAccessor.send(this.language(), sender, "info-not-exist", uuid);
            return;
        }

        var quest = optional.get();
        var locale = LocaleService.locale(sender);
        var name = quest.getDisplayName().message(((PointedAudience) sender).locale());
        //var type = this.language().handle().item(quest.getType(QuestService.instance().registry())).raw(locale);

        sender.sendMessage(QLib.textEngine().renderString("{#line}"));
        MessageAccessor.send(this.language(), sender, "info-name", name, quest.getStatus().name());
        MessageAccessor.send(this.language(), sender, "info-creator", quest.getCreator());
        //MessageAccessor.send(this.language(), sender, "info-type", type);
        MessageAccessor.send(this.language(), sender, "info-other", quest.getFormattedMetadata(quest.getData()));
        MessageAccessor.send(this.language(), sender, "info-description", quest.getData());
        MessageAccessor.send(this.language(), sender, "info-uuid", quest.getUuid());
        sender.sendMessage(QLib.textEngine().renderString("{#line}"));
    }
}
