package org.atcgroup.starlight.bundle.task;

import com.google.gson.JsonParser;
import me.gb2022.commons.jdbc.TableNamedDataService;
import me.gb2022.gluon.service.ApplicationService;
import me.gb2022.gluon.service.Service;
import me.gb2022.gluon.service.ServiceHolder;
import me.gb2022.gluon.service.ServiceInject;
import org.apache.logging.log4j.Logger;
import org.atcgroup.starlight.bundle.task.reward.RewardInstance;
import org.atcraftmc.starlight.SLPluginEnvironment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ApplicationService(id = "reward", impl = RewardService.RewardServiceImpl.class)
public interface RewardService extends Service {
    Logger LOGGER = SLPluginEnvironment.createLogger("RewardService");

    @ServiceInject
    ServiceHolder<RewardService> INSTANCE = new ServiceHolder<>();

    final class RewardServiceImpl extends TableNamedDataService implements RewardService {
        public RewardServiceImpl() {
            super("sl_rewards");
        }

        @Override
        public PreparedStatement createTable(Connection conn) throws SQLException {
            var sql = """
                    CREATE TABLE IF NOT EXISTS sl_rewards (
                        uuid CHAR(36) PRIMARY KEY,
                        reward_type VARCHAR(128) NOT NULL,
                        receiver CHAR(36) NOT NULL,
                        metadata VARCHAR(16384) NOT NULL,
                        claimed BOOLEAN NOT NULL,
                        created TIMESTAMP NOT NULL
                    );
                    """;

            return conn.prepareStatement(sql);
        }

        public boolean add(RewardInstance instance) {
            try (var c = this.datasource.getConnection(); var ps = c.prepareStatement(
                    "INSERT INTO sl_rewards (uuid, reward_type, receiver, metadata, claimed, created) VALUES (?,?,?,?,?,?)")) {
                ps.setString(1, instance.getUuid().toString());
                ps.setString(2, instance.getType());
                ps.setString(3, instance.getReceiver().toString());
                ps.setString(4, instance.getMetadata().toString());
                ps.setBoolean(5, instance.isClaimed());
                ps.setTimestamp(6, Timestamp.from(instance.getCreated()));
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean update(RewardInstance instance) {
            try (var c = this.datasource.getConnection(); var ps = c.prepareStatement(
                    "UPDATE sl_rewards SET reward_type=?, receiver=?, metadata=?, claimed=?, created=? WHERE uuid=?")) {
                ps.setString(1, instance.getType());
                ps.setString(2, instance.getReceiver().toString());
                ps.setString(3, instance.getMetadata().toString());
                ps.setBoolean(4, instance.isClaimed());
                ps.setTimestamp(5, Timestamp.from(instance.getCreated()));
                ps.setString(6, instance.getUuid().toString());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean delete(UUID uuid) {
            try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("DELETE FROM sl_rewards WHERE uuid=?")) {
                ps.setString(1, uuid.toString());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public int purge() {
            try (var c = this.datasource.getConnection(); var ps = c.prepareStatement(
                    "DELETE FROM sl_rewards WHERE claimed=? AND created<?")) {
                ps.setBoolean(1, true);
                ps.setTimestamp(2, Timestamp.from(ZonedDateTime.now().minusMonths(6).toInstant()));
                return ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public Optional<RewardInstance> byUUID(UUID uuid) {
            try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("SELECT * FROM sl_rewards WHERE uuid=?")) {
                ps.setString(1, uuid.toString());

                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(decode(rs));
                    }
                    return Optional.empty();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public Set<RewardInstance> byReceiver(UUID receiver) {
            try (var c = this.datasource.getConnection(); var ps = c.prepareStatement("SELECT * FROM sl_rewards WHERE receiver=?")) {
                ps.setString(1, receiver.toString());

                var result = new HashSet<RewardInstance>();

                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(decode(rs));
                    }
                }

                return result;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public RewardInstance decode(ResultSet rs) throws SQLException {
            return new RewardInstance(
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("reward_type"),
                    UUID.fromString(rs.getString("receiver")),
                    JsonParser.parseString(rs.getString("metadata")).getAsJsonObject(),
                    rs.getBoolean("claimed"),
                    rs.getTimestamp("created").toInstant()
            );
        }
    }
}
