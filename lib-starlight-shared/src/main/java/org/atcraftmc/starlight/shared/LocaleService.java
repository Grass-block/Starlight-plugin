package org.atcraftmc.starlight.shared;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.atcraftmc.qlib.language.MinecraftLocale;
import org.atcraftmc.starlight.shared.jdbc.JDBCData;
import org.atcraftmc.starlight.shared.jdbc.document.DocumentField;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public abstract class LocaleService<A> {
    protected final Cache<UUID, MinecraftLocale> cache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5)).build();

    public final MinecraftLocale getLocale(A audience) {
        var uuid = getIdentifier(audience);

        var cached = this.cache.getIfPresent(uuid);

        if (cached != null) {
            return cached;
        }

        var tested = Storage.getTested(uuid);
        if (tested.isPresent()) {
            this.cache.put(uuid, tested.get());
            return tested.get();
        }

        return testLocale(uuid, getLocaleNatively(audience),false);
    }

    public MinecraftLocale testLocale(UUID uuid, MinecraftLocale fetched,boolean enforceChange) {
        var custom = Storage.getCustom(uuid);

        if (custom.isPresent()) {
            this.cache.put(uuid, custom.get());
            return custom.get();
        }

        if(enforceChange){
            Storage.setTested(uuid, fetched);
        }
        this.cache.put(uuid, fetched);

        return fetched;
    }

    public final Optional<MinecraftLocale> getCustomLocale(A audience) {
        var uuid = getIdentifier(audience);

        return Storage.getCustom(uuid);
    }

    public final void setCustomLocale(A audience, MinecraftLocale locale) {
        var uuid = getIdentifier(audience);

        Storage.setCustom(uuid, locale);

        if (locale != null) {
            //custom locale has the highest priority, use value here directly.
            this.cache.put(uuid, locale);
            return;
        }

        //whatever we update tested result here.
        var fetched = getLocaleNatively(audience);
        Storage.setTested(uuid, fetched);
        this.cache.put(uuid, fetched);
    }

    public abstract UUID getIdentifier(A audience);

    public abstract MinecraftLocale getLocaleNatively(A audience);


    interface Storage {
        //this sucks, but it is initially defined as it. Frick.
        String TESTED_UNKNOWN = "unknown";
        String CUSTOM_AUTO = "auto";

        DocumentField<String> TESTED_LOCALE = DocumentField.string("locale-tested", "unknown");
        DocumentField<String> CUSTOM_LOCALE = DocumentField.string("locale-custom", "auto");

        static Optional<MinecraftLocale> getCustom(UUID uuid) {
            var data = CUSTOM_LOCALE.get(JDBCData.PLAYER_SHARED, uuid);

            if (data == null || CUSTOM_AUTO.equals(data)) {
                return Optional.empty();
            }

            return Optional.of(MinecraftLocale.minecraft(data));
        }

        static Optional<MinecraftLocale> getTested(UUID uuid) {
            var data = TESTED_LOCALE.get(JDBCData.PLAYER_SHARED, uuid);

            if (data == null || TESTED_UNKNOWN.equals(data)) {
                return Optional.empty();
            }

            return Optional.of(org.atcraftmc.qlib.language.MinecraftLocale.minecraft(data));
        }

        static void setCustom(UUID uuid, MinecraftLocale locale) {
            if (locale == null) {
                CUSTOM_LOCALE.set(JDBCData.PLAYER_SHARED, uuid, CUSTOM_AUTO);
                return;
            }

            CUSTOM_LOCALE.set(JDBCData.PLAYER_SHARED, uuid, locale.minecraft());
        }

        static void setTested(UUID uuid, MinecraftLocale locale) {
            if (locale == null) {
                TESTED_LOCALE.set(JDBCData.PLAYER_SHARED, uuid, TESTED_UNKNOWN);
                return;
            }

            TESTED_LOCALE.set(JDBCData.PLAYER_SHARED, uuid, locale.minecraft());
        }
    }
}
