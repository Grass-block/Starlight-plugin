package org.atcraftmc.starlight.core.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.atcraftmc.starlight.core.objects.Waypoint;
import org.atcraftmc.starlight.data.jdbc.SQLMapper;
import org.atcraftmc.starlight.data.jdbc.service.ORMDataService;
import org.atcraftmc.starlight.data.jdbc.source.JDBCDataSource;
import org.atcraftmc.starlight.data.jdbc.source.SQLMappedDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class WaypointServiceV2 extends ORMDataService {
    private final String table;

    public WaypointServiceV2(String table) {
        this.table = table;
    }

    @Override
    public void init(JDBCDataSource datasource) {
        super.init(datasource);
        this.datasource = new SQLMappedDataSource(datasource, SQLMapper.single("_waypoint_", this.table));
    }

    @Override
    public PreparedStatement attemptCreateTable(Connection conn) throws SQLException {
        var createTableSQL = """
                CREATE TABLE IF NOT EXISTS _waypoint_ (
                    uuid VARCHAR(36) PRIMARY KEY,
                    name VARCHAR(128) NOT NULL UNIQUE,
                    world VARCHAR(128) NOT NULL,
                    x DOUBLE NOT NULL,
                    y DOUBLE NOT NULL,
                    z DOUBLE NOT NULL,
                    yaw FLOAT NOT NULL,
                    PITCH FLOAT NOT NULL,
                    owner VARCHAR(36) NOT NULL,
                    allowed VARCHAR(1024) NOT NULL
                );
                """;

        return conn.prepareStatement(createTableSQL);
    }

    public boolean add(Waypoint wp) {
        try (var mapper = getDataMapper(WaypointMapper.class)) {
            if (existName(wp.getName())) {
                throw new RuntimeException("名称已存在: " + wp.getName());
            }
            return mapper.insert(wp) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean rename(String origin, String dest) {
        try (var mapper = getDataMapper(WaypointMapper.class)) {
            return mapper.update(null, new UpdateWrapper<Waypoint>().eq("name", origin).set("name", dest)) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(String name) {
        try (var mapper = getDataMapper(WaypointMapper.class)) {
            return mapper.delete(new QueryWrapper<Waypoint>().eq("name", name)) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> listNameOwned(UUID user) {
        try (var mapper = getDataMapper(WaypointMapper.class)) {
            return mapper.selectList(new QueryWrapper<Waypoint>().eq("owner", user.toString()).select("name"))
                    .stream()
                    .map(Waypoint::getName)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existName(String name) {
        try (var mapper = getDataMapper(WaypointMapper.class)) {
            return mapper.selectCount(new QueryWrapper<Waypoint>().eq("name", name)) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> listNameAccessible(UUID user) {
        try (var mapper = getDataMapper(WaypointMapper.class)) {
            String u = user.toString();

            return mapper.selectList(new QueryWrapper<Waypoint>().eq("owner", u)
                                             .or()
                                             .like("allowed", "all")
                                             .or()
                                             .like("allowed", u)
                                             .select("name")).stream().map(Waypoint::getName).collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasAccess(UUID user, String name) {
        try (var mapper = getDataMapper(WaypointMapper.class)) {
            Waypoint wp = mapper.selectOne(new QueryWrapper<Waypoint>().eq("name", name));

            if (wp == null) {
                return false;
            }

            String u = user.toString();

            return wp.getOwner().equals(u) || wp.getAllowed().contains("all") || wp.getAllowed().contains(u);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasControl(UUID user, String name) {
        try (var mapper = getDataMapper(WaypointMapper.class)) {
            Waypoint wp = mapper.selectOne(new QueryWrapper<Waypoint>().eq("name", name));

            return wp != null && wp.getOwner().equals(user.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Waypoint> listOwned(UUID user) {
        try (var mapper = getDataMapper(WaypointMapper.class)) {
            return new HashSet<>(mapper.selectList(new QueryWrapper<Waypoint>().eq("owner", user.toString())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Waypoint> listAccessible(UUID user) {
        try (var mapper = getDataMapper(WaypointMapper.class)) {
            String u = user.toString();

            return new HashSet<>(mapper.selectList(new QueryWrapper<Waypoint>().eq("owner", u)
                                                           .or()
                                                           .like("allowed", "all")
                                                           .or()
                                                           .like("allowed", u)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(Waypoint wp) {
        try (var mapper = getDataMapper(WaypointMapper.class)) {
            return mapper.updateById(wp) > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Waypoint> byName(String name) {
        try (var mapper = getDataMapper(WaypointMapper.class)) {
            return Optional.ofNullable(mapper.selectOne(new QueryWrapper<Waypoint>().eq("name", name)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    interface WaypointMapper extends BaseMapper<Waypoint> {
    }
}
