package org.atcraftmc.starlight.data.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.atcraftmc.starlight.data.jdbc.source.JDBCDataSource;
import org.atcraftmc.starlight.shared.FilePath;
import org.atcraftmc.starlight.shared.service.JDBCService;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class JDBCDatasourceManager {
    private final Map<String, JDBCDataSource> dataSources = new HashMap<>();

    static HikariDataSource hikariPool(ConfigurationSection cfg) {
        @SuppressWarnings("StringBufferReplaceableByString") var url = new StringBuilder().append("jdbc:")
                .append(Objects.requireNonNull(cfg.getString("driver")).toLowerCase())
                .append(":")
                .append(cfg.getString("url")).toString().replace("{folder}", FilePath.slDataFolder());

        var config = new HikariConfig();

        config.setJdbcUrl(url);
        config.setUsername(cfg.getString("user"));
        config.setPassword(cfg.getString("password"));

        // 核心参数（建议）
        config.setMaximumPoolSize(cfg.getInt("--hikari-max-pool-size", 10));
        config.setMinimumIdle(cfg.getInt("--hikari-min-idle", 2));
        config.setConnectionTimeout(cfg.getInt("--hikari-conn-timeout", 3000));
        config.setIdleTimeout(cfg.getInt("--hikari-idle-timeout", 600000));
        config.setMaxLifetime(cfg.getInt("--hikari-max-life", 1800000));

        // 性能优化
        config.setAutoCommit(true);
        config.setPoolName(cfg.getString("--hikari-pool-name", cfg.getString("id")));

        return new HikariDataSource(config);
    }

    public Optional<JDBCDataSource> getDataSource(String id) {
        return Optional.ofNullable(this.dataSources.get(id));
    }

    public JDBCDataSource create(ConfigurationSection cfg, JDBCService service) {
        var id = cfg.getString("id");

        if (cfg.contains("link")) {
            var link = cfg.getString("link");
            var ds = getDataSource(link).orElseThrow();

            return this.dataSources.put(id, JDBCDataSource.phantom(ds));
        }

        return this.dataSources.put(id, JDBCDataSource.simple(hikariPool(cfg),service));
    }

    public Map<String, JDBCDataSource> getDataSources() {
        return dataSources;
    }
}
