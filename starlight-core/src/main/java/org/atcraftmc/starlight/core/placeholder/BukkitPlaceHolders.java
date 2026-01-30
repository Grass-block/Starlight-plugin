package org.atcraftmc.starlight.core.placeholder;

import me.gb2022.commons.Formating;
import me.gb2022.commons.TriState;
import org.atcraftmc.qlib.texts.placeholder.*;
import org.atcraftmc.starlight.ProductInfo;
import org.atcraftmc.starlight.SharedObjects;
import org.atcraftmc.starlight.Starlight;
import org.atcraftmc.starlight.core.LocaleService;
import org.atcraftmc.starlight.core.data.ModuleDataService;
import org.atcraftmc.starlight.core.data.PlayerDataService;
import org.atcraftmc.starlight.foundation.platform.BukkitUtil;
import org.atcraftmc.starlight.foundation.platform.Players;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.Objects;

public interface BukkitPlaceHolders {
    static GloballyPlaceHolder server() {
        GloballyPlaceHolder holder = new GloballyPlaceHolder();

        holder.register("version", GlobalPlaceHolder.value(Bukkit.getServer().getVersion()), "server-version");
        holder.register("bukkit-version", GlobalPlaceHolder.value(Bukkit.getServer().getBukkitVersion()));
        holder.register("name", Bukkit.getServer().getName());
        holder.register("max-player", Bukkit.getServer().getMaxPlayers());
        holder.register("player", GlobalPlaceHolder.object(() -> Bukkit.getOnlinePlayers().size()));
        holder.register("tps", (StringPlaceHolder) () -> BukkitUtil.formatTPS(BukkitUtil.getTPS()));
        holder.register("mspt", (StringPlaceHolder) () -> BukkitUtil.formatMSPT(BukkitUtil.getMSPT()));

        holder.register("date", (StringPlaceHolder) () -> SharedObjects.DATE_FORMAT.format(new Date()), "date-full");
        holder.register("date-day", (StringPlaceHolder) () -> SharedObjects.DAY_FORMAT.format(new Date()), "day");
        holder.register("date-time", (StringPlaceHolder) () -> SharedObjects.TIME_FORMAT.format(new Date()), "time");

        return holder;
    }

    static ObjectivePlaceHolder<Player> player() {
        ObjectivePlaceHolder<Player> holder = new ObjectivePlaceHolder<>();
        holder.register("name", (StringObjectPlaceHolder<Player>) Player::getName);
        holder.register("display-name", (StringObjectPlaceHolder<Player>) Player::getDisplayName);
        holder.register("custom-name", (StringObjectPlaceHolder<Player>) Player::getName);
        holder.register(
                "address",
                (StringObjectPlaceHolder<Player>) (p) -> Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress()
        );
        holder.register("locale", (StringObjectPlaceHolder<Player>) (p) -> LocaleService.locale(p).minecraft());
        holder.register("ping", (StringObjectPlaceHolder<Player>) (p) -> BukkitUtil.formatPing(Players.getPing(p)));
        holder.register("play-time", (StringObjectPlaceHolder<Player>) (p) -> Formating.formatDuringFull(Players.getPlayTime(p)));
        holder.register("world-time", (StringObjectPlaceHolder<Player>) (p) -> {
            int time = (int) p.getWorld().getTime() - 18000;

            if (time < 0) {
                time = 24000 + time;
            }
            int hour = time / 1000;
            int min = (int) ((time + 19000) % 1000 * (60 / 1000f));
            return String.format("%02d:%02d", hour, min);
        });

        holder.register("ping-value", (StringObjectPlaceHolder<Player>) p -> String.valueOf(Players.getPing(p)));


        return holder;
    }

    static GloballyPlaceHolder quarkStats() {
        GloballyPlaceHolder holder = new GloballyPlaceHolder();

        holder.register(
                "module-installed",
                GlobalPlaceHolder.object(() -> Starlight.instance().context().getModuleManager().getModules().size())
        );
        holder.register(
                "module-enabled",
                GlobalPlaceHolder.object(() -> Starlight.instance()
                        .context()
                        .getModuleManager()
                        .getIdsByStatus(TriState.FALSE)
                        .size())
        );
        holder.register("player-data-count", GlobalPlaceHolder.object(PlayerDataService::entryCount));
        holder.register("module-data-count", GlobalPlaceHolder.object(ModuleDataService::getEntryCount));
        holder.register("quark-version", (StringPlaceHolder) ProductInfo::version);
        holder.register("quark-framework_version", GlobalPlaceHolder.object(ProductInfo::apiVersion));
        holder.register("build-time", GlobalPlaceHolder.object(() -> ProductInfo.METADATA.getProperty("build-time")));

        return holder;
    }
}
