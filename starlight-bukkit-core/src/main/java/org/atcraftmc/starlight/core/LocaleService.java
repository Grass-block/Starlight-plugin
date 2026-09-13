package org.atcraftmc.starlight.core;

import com.google.gson.JsonParser;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.ServiceLayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.atcraftmc.qlib.bukkit.QLib;
import org.atcraftmc.qlib.command.AbstractCommand;
import org.atcraftmc.qlib.command.BukkitCommand;
import org.atcraftmc.qlib.command.execute.CommandExecution;
import org.atcraftmc.qlib.command.execute.CommandSuggestion;
import org.atcraftmc.qlib.language.LocaleMapping;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.starlight.StarlightBukkitCore;
import org.atcraftmc.starlight.api.event.ClientLocaleChangeEvent;
import org.atcraftmc.starlight.core.command.CoreCommand;
import org.atcraftmc.starlight.core.platform.BukkitUtil;
import org.atcraftmc.starlight.core.ui.InventoryUI;
import org.atcraftmc.starlight.core.ui.TextRenderer;
import org.atcraftmc.starlight.core.ui.UI;
import org.atcraftmc.starlight.core.ui.providing.GUIProvider;
import org.atcraftmc.starlight.core.ui.view.InventoryUIView;
import org.atcraftmc.starlight.framework.BukkitService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.net.URI;
import java.net.URL;
import java.util.*;

@ApplicationService(id = "locale", layer = ServiceLayer.FRAMEWORK)
public interface LocaleService extends BukkitService {
    AbstractCommand LANGUAGE_COMMAND = new LanguageDecideCommand();

    @SuppressWarnings("deprecation")//because other server software still uses it.
    static MinecraftLocale locale(Object sender) {
        if (sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender) {
            return MinecraftLocale.locale(Locale.getDefault());
        }
        if (!(sender instanceof Player p)) {
            return MinecraftLocale.locale(Locale.getDefault());
        }

        return BukkitLocaleService.getInstance().getLocale(p);
    }

    static void setCustomLanguage(Player user, String value) {
        var locale = "auto".equals(value) ? null : MinecraftLocale.minecraft(value);
        BukkitLocaleService.getInstance().setCustomLocale(user, locale);
    }

    static String remapLanguageNames(String id) {
        return switch (id) {
            case "zh_cn" -> "zh-Hans(简体中文)";
            case "zh_tw" -> "zh-Hant-TW(繁体中文)";
            case "zh_hk" -> "zh-Hant-HK(繁体中文-香港地区)";
            default -> id;
        };
    }

    enum LanguageIcon {

        ENGLISH(
                "en_us",
                "English",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q1ZWQ0YzQzMWQxZjA5NWE0MzI0Y2Y2Y2Y1MmRkMTNmNTc2YjFjYjk0M2NmYjQzY2Y4NmVkZTRiIn19fQ=="
        ),

        SIMPLIFIED_CHINESE(
                "zh_cn",
                "简体中文",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M4ZjQzYmFmNjg3NzA0Y2I2Y2Q2NzNkYTZjNmUwNDU4MTRkNmQ3NmE5NGE0Y2ZiNmNmNjk5NjQ4MiJ9fX0="
        ),

        TRADITIONAL_CHINESE(
                "zh_tw",
                "繁體中文",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ5YzY4ZTQ2NGFkN2Y3NzI4NTkzMTE4YzM4N2FjY2M4ZDU5YTM4YWE4NTA4M2I5OTc3OTM5OWFlZCJ9fX0="
        ),

        JAPANESE(
                "ja_jp",
                "日本語",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM4ZDNjYjYxYjE3YmMzY2Y2NDY4ZTM4YzI2NmM5NGFkNmYzYjM3YmNlNmQzOWQ4OGM2YmNlYjIifX19"
        ),

        FRENCH(
                "fr_fr",
                "Français",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmFkODc4YmM2OWY5ZjY5YWEzMTE5NmU4OTM0OTY2MDFkODc0NzQ3Y2JlYjMwNDY2MTJlZWE0In19fQ=="
        ),

        RUSSIAN(
                "ru_ru",
                "Русский",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTZiN2VkYjEzMGE4YjJmZmY4YWE4ZjViYjYxZGE0MWQ3MjNlN2ZmMjQ5YjY5NjRkYmE0NjY0NSJ9fX0="
        );

        private final String locale;
        private final String displayName;
        private final String texture;

        LanguageIcon(
                String locale, String displayName, String texture
        ) {
            this.locale = locale;
            this.displayName = displayName;
            this.texture = texture;
        }

        public static void icon(InventoryUI builder, int pos, LanguageIcon icon) {
            var item = new ItemStack(Material.PLAYER_HEAD);
            var meta = (SkullMeta) item.getItemMeta();
            var profile = Bukkit.createPlayerProfile(UUID.randomUUID());
            var textures = profile.getTextures();

            textures.setSkin(textureUrl(icon.texture()));

            profile.setTextures(textures);
            meta.setOwnerProfile(profile);
            item.setItemMeta(meta);

            UI.buildComponent(builder, pos, (o) -> {
                o.icon(item);
                o.name(TextRenderer.literal(Component.text(icon.displayName())
                                                    .color(NamedTextColor.AQUA)
                                                    .decoration(TextDecoration.ITALIC, false)));
                o.operation(UI.command((p) -> "locale " + icon.locale)).operation(UI.SOUND_CLICK);
            });
        }

        private static URL textureUrl(String base64) {

            try {
                var decoded = new String(Base64.getDecoder().decode(base64));
                var json = JsonParser.parseString(decoded).getAsJsonObject();
                var url = json.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();

                return URI.create(url).toURL();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String locale() {
            return locale;
        }

        public String displayName() {
            return displayName;
        }

        public String texture() {
            return texture;
        }
    }

    @SuppressWarnings("deprecation")//because other server still uses getLocale()
    @BukkitCommand(name = "locale", permission = "+quark.locale", playerOnly = true)
    final class LanguageDecideCommand extends CoreCommand {
        @Override
        public void suggest(CommandSuggestion suggestion) {
            suggestion.suggest(0, List.of(LocaleMapping.MINECRAFT_KNOWN_LANGUAGES));
            suggestion.suggest(0, "gui", "auto");
        }

        @Override
        public void execute(CommandExecution context) {
            var data = context.requireArgumentAt(0);
            if (Objects.equals(data, "gui")) {
                //GUI.open(context.requireSenderAsPlayer());
            }

            setCustomLanguage(context.requireSenderAsPlayer(), data);


            StarlightBukkitCore.lang().item("starlight-core.locale.set").send(QLib.audience(context.getSender()), remapLanguageNames(data));
            BukkitUtil.callEvent(new ClientLocaleChangeEvent((Player) context.getSender(), locale(context.getSender())));
        }
    }

    final class LocaleUI implements GUIProvider<InventoryUI> {
        @Override
        public InventoryUI create() {
            return new InventoryUI(54, TextRenderer.literal(Component.text("语言 / Language")));
        }

        @Override
        public void render(InventoryUI builder, InventoryUIView view, Object... args) {
            LanguageIcon.icon(builder, 11, LanguageIcon.SIMPLIFIED_CHINESE);
            LanguageIcon.icon(builder, 13, LanguageIcon.TRADITIONAL_CHINESE);
            LanguageIcon.icon(builder, 15, LanguageIcon.ENGLISH);
            LanguageIcon.icon(builder, 29, LanguageIcon.FRENCH);
            LanguageIcon.icon(builder, 31, LanguageIcon.JAPANESE);
            LanguageIcon.icon(builder, 33, LanguageIcon.RUSSIAN);

            //close button
            UI.buildComponent(builder, 53, (b) -> {
                b.icon(UI.icon(Material.REDSTONE));
                b.name(TextRenderer.data(StarlightBukkitCore.lang().item("common", "ui", "close")));
                b.operation(UI.SOUND_CLICK);
                b.operation(UI.close());
            });
        }
    }
}
