package org.atcraftmc.starlight.migration;

import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.language.LanguageEntry;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.bukkit.command.CommandSender;
import org.atcraftmc.starlight.core.TextSender;
import org.atcraftmc.starlight.core.LocaleService;

public interface MessageAccessor {
    static void broadcast(LanguageEntry language, boolean b, boolean b1, String s, Object... format) {
        var audience = QLib.context().audiences().players();

        if(b1){
            audience.forwarding().add(QLib.context().audiences().console().pointed());
        }

        if(b){
            audience.forwarding().getAudiences().removeIf(audience1 -> !audience1.getPointer(CommandSender.class).isOp());
        }

        audience.sendMessage(language.item(s).message(format));
    }

    static void send(LanguageEntry language, CommandSender sender, String s, Object... format) {
        language.item(s).send(QLib.audience(sender), format);
    }

    static String getMessage(LanguageEntry language, MinecraftLocale locale, String s, Object... format) {
        return language.item(s).message(locale, format).render();
    }

    static void sendTemplate(LanguageEntry language, CommandSender sender, String ui) {
        TextSender.sendMessage(sender, QLib.textBuilder().build(buildTemplate(language, LocaleService.locale(sender), ui)));
    }

    static String buildTemplate(LanguageEntry language, MinecraftLocale locale, String s) {
        return language.inline(s, locale);
    }

}
