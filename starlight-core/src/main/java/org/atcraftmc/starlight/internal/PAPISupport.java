package org.atcraftmc.starlight.internal;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.gb2022.commons.compatibility.APIIncompatibleException;
import me.gb2022.gluon.module.ApplicationModule;
import org.atcraftmc.qlib.platform.PluginPlatform;
import org.atcraftmc.qlib.texts.pipe.TextPipelineProcessor;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.placeholder.PlaceHolderService;
import org.atcraftmc.starlight.foundation.platform.Compatibility;
import org.atcraftmc.starlight.framework.module.BukkitAbstractModule;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationModule(id = "papi-support", internal = true, description = "Provide papi support.")
public final class PAPISupport extends BukkitAbstractModule {
    private final PAPILanguageExtension languageExtension = new PAPILanguageExtension();
    private final PAPIVariablesExtension variablesExtension = new PAPIVariablesExtension();

    @Override
    public void checkCompatibility() throws APIIncompatibleException {
        Compatibility.requireClass(() -> Class.forName("me.clip.placeholderapi.PlaceholderAPI"));
    }

    @Override
    public void enable() {
        this.languageExtension.register();
        this.variablesExtension.register();

        PluginPlatform.global().getTextPipeline().addLast("starlight:papi-inject", new PAPIProcessor());
    }

    @Override
    public void disable() {
        this.languageExtension.unregister();
        this.variablesExtension.unregister();

        PluginPlatform.global().getTextPipeline().remove("starlight:papi-inject");
    }

    public static final class PAPIProcessor implements TextPipelineProcessor {
        private static final UUID CONSOLE_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

        @Override
        public String process(String source, @Nullable Object player) {
            if ((player instanceof Player p)) {
                return PlaceholderAPI.setPlaceholders(p, source);
            }

            return PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(CONSOLE_UUID), source);
        }

        @Override
        public boolean enforceSecureMode() {
            return true;
        }
    }

    public static class PAPILanguageExtension extends PlaceholderExpansion {

        @Override
        public @NotNull String getIdentifier() {
            return "starlight-lang";
        }

        @Override
        public @NotNull String getAuthor() {
            return "ATCraftMC/Starlight";
        }

        @Override
        public @NotNull String getVersion() {
            return Starlight.instance().getDescription().getVersion();
        }

        public String examine(OfflinePlayer player, String params) {
            if (player == null || !player.isOnline()) {
                return null;
            }

            var locale = LocaleService.locale(player.getPlayer());
            var args = params.split("_");
            var a = Starlight.instance().language().item(args[0]);
            var fmt = new String[args.length - 1];

            System.arraycopy(args, 1, fmt, 0, fmt.length);

            return a.message(locale, (Object[]) fmt).render(player.getPlayer());
        }

        @Override
        public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
            return examine(player, params);
        }

        @Override
        public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
            return examine(player, params);
        }

        @Override
        public @NotNull List<String> getPlaceholders() {
            return Starlight.instance().language().getItems().keySet().stream().map((s) -> "%starlight-lang_" + s + "%").toList();
        }
    }

    public static class PAPIVariablesExtension extends PlaceholderExpansion {
        @Override
        public @NotNull String getIdentifier() {
            return "starlight";
        }

        @Override
        public @NotNull String getAuthor() {
            return "ATCraftMC/Starlight";
        }

        @Override
        public @NotNull String getVersion() {
            return Starlight.instance().getDescription().getVersion();
        }

        private String examine(String param, OfflinePlayer player) {
            String value;

            if ((value = PlaceHolderService.GLOBAL_VAR.get(param)) != null) {
                return value;
            }
            if ((value = PlaceHolderService.SERVER.get(param)) != null) {
                return value;
            }

            if (player == null || !player.isOnline()) {
                return null;
            }

            if ((value = PlaceHolderService.PLAYER.get(param, player.getPlayer())) != null) {
                return value;
            }

            return null;
        }

        @Override
        public @NotNull List<String> getPlaceholders() {
            var list = new ArrayList<String>();

            list.addAll(PlaceHolderService.GLOBAL_VAR.getRegisterKeys());
            list.addAll(PlaceHolderService.SERVER.getRegisterKeys());
            list.addAll(PlaceHolderService.PLAYER.getRegisterKeys());

            return list.stream().map((s) -> "%starlight_" + s + "%").toList();
        }

        @Override
        public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
            return this.examine(params, null);
        }

        @Override
        public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
            return this.examine(params, player);
        }
    }
}
