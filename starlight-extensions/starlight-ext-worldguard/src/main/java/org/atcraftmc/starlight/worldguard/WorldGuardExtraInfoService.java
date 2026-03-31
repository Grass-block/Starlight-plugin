package org.atcraftmc.starlight.worldguard;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.gb2022.gluon.service.ApplicationService;
import org.atcraftmc.starlight.core.ui.TextRenderer;
import org.atcraftmc.starlight.core.ui.UI;
import org.atcraftmc.starlight.shared.data.JDBCBasedDataService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationService(id = "wg-extra-info")
public interface WorldGuardExtraInfoService {


    final class Storage extends JDBCBasedDataService<PlotDisplayInfo> {
        public Storage() {
            super("plot_info");
        }

        @Override
        public PreparedStatement attemptCreateTable(Connection connection) throws SQLException {
            var sql = """
                    CREATE TABLE IF NOT EXISTS `plot_info` (
                        uuid char(36) primary key,
                        id varchar(128) not null,
                        name varchar(128) not null,
                        icon varchar(64) not null,
                        note varchar(512) not null,
                        spawn_x int,
                        spawn_y int,
                        spawn_z int,
                        tag varchar(1024) not null,
                        hidden bool
                    );
                    """;

            return connection.prepareStatement(sql);
        }
    }


    record PlotDisplayInfo(String icon, String name, String desc, Set<String> tags) {

        public void icon(ProtectedRegion rg, UI.ElementBuilder element, String template) {
            var icon = Objects.requireNonNullElse(Material.matchMaterial(this.icon), Material.GRASS_BLOCK);
            var players = rg.getOwners().getUniqueIds()
                    .stream()
                    .map(Bukkit::getOfflinePlayer)
                    .map(OfflinePlayer::getName).collect(Collectors.toSet());
            var owners = "[empty]";

            if (!players.isEmpty()) {
                owners = "[" + String.join(", ", players) + "]";
            }
            var ui = template
                    .replace("{name}", this.name)
                    .replace("{id}", rg.getId())
                    .replace("{owner}", owners);


            element.icon(new ItemStack(icon));
            element.lore(TextRenderer.literal(ui));
            element.name(TextRenderer.literal(this.name));
        }

    }


    final class Impl implements WorldGuardExtraInfoService{
        public Set<PlotDisplayInfo> list(UUID uuid) {
            return Set.of();
        }
    }
}
