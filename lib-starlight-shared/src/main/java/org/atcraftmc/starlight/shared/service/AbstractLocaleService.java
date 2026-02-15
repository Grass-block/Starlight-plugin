package org.atcraftmc.starlight.shared.service;

import me.gb2022.gluon.service.Service;
import org.atcraftmc.qlib.language.LocaleMapping;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.qlib.platform.PluginPlatform;
import org.atcraftmc.starlight.SLPluginEnvironment;
import org.atcraftmc.starlight.data.JDBCPlayerData;
import org.atcraftmc.starlight.shared.data.flex.TableColumn;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractLocaleService<A> implements Service {
    public static final TableColumn<String> TESTED_LOCALE = TableColumn.string("lang_tested", 16, "unknown");
    public static final TableColumn<String> CUSTOM_LOCALE = TableColumn.string("lang_custom", 16, "auto");

    protected final Map<UUID, String> localCache = new HashMap<>();

    public abstract boolean isNativeAudience(A pointer);

    public abstract UUID getIdentifier(A pointer);

    public abstract MinecraftLocale getLocaleNatively(A pointer);

    public void onLanguageUpdated(A audience, MinecraftLocale locale) {
    }

    public void announceLanguageUpdated(A audience, MinecraftLocale locale) {
    }

    public abstract String getConfigNamespace();

    public final String getUserCustomLocale(UUID uuid) {
        return CUSTOM_LOCALE.get(JDBCPlayerData.PLAYER_SHARED, uuid);
    }

    public final void setUserCustomLocale(UUID uuid, String locale) {
        CUSTOM_LOCALE.set(JDBCPlayerData.PLAYER_SHARED, uuid, locale);
    }

    public final String getTestedLocale(UUID uuid) {
        return TESTED_LOCALE.get(JDBCPlayerData.PLAYER_SHARED, uuid);
    }

    public final void setTestedLocale(UUID uuid, String locale) {
        TESTED_LOCALE.set(JDBCPlayerData.PLAYER_SHARED, uuid, locale);
    }

    public final String getCachedLocale(UUID uuid) {
        return localCache.get(uuid);
    }

    public final void invalidateCachedLocale(@NotNull UUID uniqueId) {
        this.localCache.remove(uniqueId);
    }

    public final MinecraftLocale locale(A sender) {
        if (isNativeAudience(sender)) {
            return MinecraftLocale.locale(Locale.getDefault());
        }

        var uuid = getIdentifier(sender);

        if (this.localCache.containsKey(uuid)) {
            return MinecraftLocale.minecraft(this.localCache.get(uuid));
        }

        var locale = getUserLocale(sender);
        this.localCache.put(uuid, locale);

        return MinecraftLocale.minecraft(locale);
    }

    public final String getUserLocale(A user) {
        var uuid = getIdentifier(user);

        try {
            var custom = CUSTOM_LOCALE.get(JDBCPlayerData.PLAYER_SHARED, uuid);

            if (!Objects.equals(custom, "auto")) {
                return custom;
            }

            var tested = TESTED_LOCALE.get(JDBCPlayerData.PLAYER_SHARED, uuid);

            if (!Objects.equals(tested, "unknown")) {
                return tested;
            }
        } catch (Exception ignored) {
        }

        if (isNativeAudience(user)) {
            return MinecraftLocale.locale(Locale.getDefault()).minecraft();
        }

        return getLocaleNatively(user).minecraft();
    }


    public final void checkClientLocale(A player, String initial) {
        var uuid = getIdentifier(player);
        var locale = initial;

        var preset = SLPluginEnvironment.getPlugin().language().item(getConfigNamespace() + ".locale.preset");

        try {
            locale = getLocaleNatively(player).minecraft();
        } catch (Exception e) {
            locale = LocaleMapping.minecraft(Locale.getDefault());
        }

        var isValidChange = true;

        var custom = getUserCustomLocale(uuid);
        var cache = getTestedLocale(uuid);

        if (Objects.equals(locale, "en_us")) {
            if (!Objects.equals(cache, "unknown")) {
                locale = cache;
                isValidChange = false;
            }

            if (!Objects.equals(custom, "auto")) {
                locale = custom;
                isValidChange = false;
            }
        }

        if (Objects.equals(custom, "auto")) {
            setTestedLocale(uuid, locale);

            if (isValidChange) {
                var block = preset.component(locale(player), locale);

                PluginPlatform.global().sendMessage(player, block);
                this.localCache.put(uuid, locale);
            }
        } else {
            if (!Objects.equals(cache, "unknown")) {
                locale = cache;
            }
        }

        this.onLanguageUpdated(player, MinecraftLocale.minecraft(locale));
        this.announceLanguageUpdated(player, MinecraftLocale.minecraft(locale));
    }
}
