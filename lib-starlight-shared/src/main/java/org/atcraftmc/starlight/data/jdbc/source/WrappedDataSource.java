package org.atcraftmc.starlight.data.jdbc.source;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Logger;

public class WrappedDataSource implements DataSource {
    protected final DataSource dataSource;

    public WrappedDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Connection wrapConnection(Connection connection) {
        return connection;
    }

    @Override
    public final Connection getConnection() throws SQLException {
        return wrapConnection(this.dataSource.getConnection());
    }

    @Override
    public final Connection getConnection(String s, String s1) throws SQLException {
        return wrapConnection(dataSource.getConnection(s, s1));
    }

    @Override
    public final PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    @Override
    public final void setLogWriter(PrintWriter printWriter) throws SQLException {
        dataSource.setLogWriter(printWriter);
    }

    @Override
    public final void setLoginTimeout(int i) throws SQLException {
        dataSource.setLoginTimeout(i);
    }

    @Override
    public final int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override
    public final ConnectionBuilder createConnectionBuilder() throws SQLException {
        return new WrappedConnectionBuilder(this, this.dataSource.createConnectionBuilder());
    }

    @Override
    public final Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }

    @Override
    public final ShardingKeyBuilder createShardingKeyBuilder() throws SQLException {
        return dataSource.createShardingKeyBuilder();
    }

    @Override
    public final <T> T unwrap(Class<T> aClass) throws SQLException {
        return dataSource.unwrap(aClass);
    }

    @Override
    public final boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return dataSource.isWrapperFor(aClass);
    }

    private record WrappedConnectionBuilder(WrappedDataSource s, ConnectionBuilder b) implements ConnectionBuilder {

        @Override
        public ConnectionBuilder password(String s) {
            return b.password(s);
        }

        @Override
        public ConnectionBuilder shardingKey(ShardingKey shardingKey) {
            return b.shardingKey(shardingKey);
        }

        @Override
        public ConnectionBuilder superShardingKey(ShardingKey shardingKey) {
            return b.superShardingKey(shardingKey);
        }

        @Override
        public Connection build() throws SQLException {
            return this.s.wrapConnection(this.b.build());
        }

        @Override
        public ConnectionBuilder user(String s) {
            return b.user(s);
        }
    }
}
