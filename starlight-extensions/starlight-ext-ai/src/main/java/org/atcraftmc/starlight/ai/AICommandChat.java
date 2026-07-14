package org.atcraftmc.starlight.ai;

import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.starlight.ai.chat.ChatRequest;
import org.atcraftmc.starlight.framework.module.SLCommandModule;

@BukkitCommand(name = "ai", permission = "+starlight.ai.chat")
@ApplicationModule(id = "ai-command-chat")
public final class AICommandChat extends SLCommandModule {

    @Override
    public void execute(CommandExecution context) {
        QLib.task().async().run(() -> {
            var content = context.requireRemainAsParagraph(0, true);
            var user = context.getSender().getName();
            var contextId = context.getSender().getName();
            var audience = QLib.audience(context.getSender());

            var req = new ChatRequest("", content, user, contextId);
            var last = System.currentTimeMillis();

            language().item("ai-thinking").send(audience);
            var res = AIChatService.instance().defaultHandler().chat(req);

            if (!res.isSuccess()) {
                language().item("ai-error").send(audience, res.getError());
                return;
            }

            var passed = (System.currentTimeMillis() - last) / 1000f;

            language().item("ai-report").send(audience, res.getContent(), passed);
        });
    }
}
