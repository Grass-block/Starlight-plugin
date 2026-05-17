package org.atcraftmc.starlight.data.jdbc.source;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

@SuppressWarnings("SqlSourceToSinkFlow")
public final class WrappedConnection extends SQLMapper implements Connection {
    private final Connection delegate;

    public WrappedConnection(Connection delegate) {
        this.delegate = delegate;
    }

    public WrappedConnection(Connection connection, SQLMapper mapper) {
        this.delegate = connection;
        this.addAll(mapper);
    }

    @Override
    public void setReadOnly(boolean b) throws SQLException {
        delegate.setReadOnly(b);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return delegate.isReadOnly();
    }

    @Override
    public void setCatalog(String s) throws SQLException {
        delegate.setCatalog(s);
    }

    @Override
    public String getCatalog() throws SQLException {
        return delegate.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int i) throws SQLException {
        delegate.setTransactionIsolation(i);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return delegate.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return delegate.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        delegate.clearWarnings();
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return delegate.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        delegate.setTypeMap(map);
    }

    @Override
    public void setHoldability(int i) throws SQLException {
        delegate.setHoldability(i);
    }

    @Override
    public int getHoldability() throws SQLException {
        return delegate.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return delegate.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String s) throws SQLException {
        return delegate.setSavepoint(s);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        delegate.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        delegate.releaseSavepoint(savepoint);
    }

    @Override
    public Clob createClob() throws SQLException {
        return delegate.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return delegate.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return delegate.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return delegate.createSQLXML();
    }

    @Override
    public boolean isValid(int i) throws SQLException {
        return delegate.isValid(i);
    }

    @Override
    public void setClientInfo(String s, String s1) throws SQLClientInfoException {
        delegate.setClientInfo(s, s1);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        delegate.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String s) throws SQLException {
        return delegate.getClientInfo(s);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return delegate.getClientInfo();
    }

    @Override
    public Array createArrayOf(String s, Object[] objects) throws SQLException {
        return delegate.createArrayOf(s, objects);
    }

    @Override
    public Struct createStruct(String s, Object[] objects) throws SQLException {
        return delegate.createStruct(s, objects);
    }

    @Override
    public void setSchema(String s) throws SQLException {
        delegate.setSchema(s);
    }

    @Override
    public String getSchema() throws SQLException {
        return delegate.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        delegate.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int i) throws SQLException {
        delegate.setNetworkTimeout(executor, i);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return delegate.getNetworkTimeout();
    }

    @Override
    public void beginRequest() throws SQLException {
        delegate.beginRequest();
    }

    @Override
    public void endRequest() throws SQLException {
        delegate.endRequest();
    }

    @Override
    public boolean setShardingKeyIfValid(ShardingKey shardingKey, ShardingKey superShardingKey, int timeout) throws SQLException {
        return delegate.setShardingKeyIfValid(shardingKey, superShardingKey, timeout);
    }

    @Override
    public boolean setShardingKeyIfValid(ShardingKey shardingKey, int timeout) throws SQLException {
        return delegate.setShardingKeyIfValid(shardingKey, timeout);
    }

    @Override
    public void setShardingKey(ShardingKey shardingKey, ShardingKey superShardingKey) throws SQLException {
        delegate.setShardingKey(shardingKey, superShardingKey);
    }

    @Override
    public void setShardingKey(ShardingKey shardingKey) throws SQLException {
        delegate.setShardingKey(shardingKey);
    }

    // ==================== Statement ====================

    @Override
    public Statement createStatement() throws SQLException {
        return new WrappedStatement(this, delegate.createStatement());
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return new WrappedStatement(this, delegate.createStatement(resultSetType, resultSetConcurrency));
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new WrappedStatement(this, delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    // ==================== PreparedStatement ====================

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return new WrappedStatement(this, delegate.prepareStatement(handleSQL(sql)));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return new WrappedStatement(this, delegate.prepareStatement(handleSQL(sql), autoGeneratedKeys));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return new WrappedStatement(this, delegate.prepareStatement(handleSQL(sql), columnIndexes));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return new WrappedStatement(this, delegate.prepareStatement(handleSQL(sql), columnNames));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return new WrappedStatement(this, delegate.prepareStatement(handleSQL(sql), resultSetType, resultSetConcurrency));
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new WrappedStatement(
                this,
                delegate.prepareStatement(handleSQL(sql), resultSetType, resultSetConcurrency, resultSetHoldability)
        );
    }

    // ==================== Callable ====================

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return new WrappedStatement(this, delegate.prepareCall(handleSQL(sql)));
    }

    @Override
    public String nativeSQL(String s) {
        return this.handleSQL(s);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return new WrappedStatement(this, delegate.prepareCall(handleSQL(sql), resultSetType, resultSetConcurrency));
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return new WrappedStatement(this, delegate.prepareCall(handleSQL(sql), resultSetType, resultSetConcurrency, resultSetHoldability));
    }

    // ==================== 透传（关键最小集） ====================

    @Override
    public void close() throws SQLException {
        delegate.close();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return delegate.isClosed();
    }

    @Override
    public void commit() throws SQLException {
        delegate.commit();
    }

    @Override
    public void rollback() throws SQLException {
        delegate.rollback();
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return delegate.getAutoCommit();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        delegate.setAutoCommit(autoCommit);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return delegate.getMetaData();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return delegate.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return delegate.isWrapperFor(iface);
    }


    @SuppressWarnings("DataFlowIssue")
    public static final class WrappedStatement implements PreparedStatement, CallableStatement {
        private final Statement delegate;
        private final PreparedStatement ps;
        private final CallableStatement cs;
        private final WrappedConnection connection;

        public WrappedStatement(WrappedConnection connection, Statement delegate) {
            this.connection = connection;
            this.delegate = delegate;
            if (delegate instanceof PreparedStatement s) {
                this.ps = s;
            } else {
                this.ps = null;
            }
            if (delegate instanceof CallableStatement s) {
                this.cs = s;
            } else {
                this.cs = null;
            }
        }

        @Override
        public void registerOutParameter(int i, int i1) throws SQLException {
            cs.registerOutParameter(i, i1);
        }

        @Override
        public void registerOutParameter(int i, int i1, int i2) throws SQLException {
            cs.registerOutParameter(i, i1, i2);
        }

        @Override
        public boolean wasNull() throws SQLException {
            return cs.wasNull();
        }

        @Override
        public String getString(int i) throws SQLException {
            return cs.getString(i);
        }

        @Override
        public boolean getBoolean(int i) throws SQLException {
            return cs.getBoolean(i);
        }

        @Override
        public byte getByte(int i) throws SQLException {
            return cs.getByte(i);
        }

        @Override
        public short getShort(int i) throws SQLException {
            return cs.getShort(i);
        }

        @Override
        public int getInt(int i) throws SQLException {
            return cs.getInt(i);
        }

        @Override
        public long getLong(int i) throws SQLException {
            return cs.getLong(i);
        }

        @Override
        public float getFloat(int i) throws SQLException {
            return cs.getFloat(i);
        }

        @Override
        public double getDouble(int i) throws SQLException {
            return cs.getDouble(i);
        }

        @Deprecated(since = "1.2")
        @Override
        public BigDecimal getBigDecimal(int i, int i1) throws SQLException {
            return cs.getBigDecimal(i, i1);
        }

        @Override
        public byte[] getBytes(int i) throws SQLException {
            return cs.getBytes(i);
        }

        @Override
        public Date getDate(int i) throws SQLException {
            return cs.getDate(i);
        }

        @Override
        public Time getTime(int i) throws SQLException {
            return cs.getTime(i);
        }

        @Override
        public Timestamp getTimestamp(int i) throws SQLException {
            return cs.getTimestamp(i);
        }

        @Override
        public Object getObject(int i) throws SQLException {
            return cs.getObject(i);
        }

        @Override
        public BigDecimal getBigDecimal(int i) throws SQLException {
            return cs.getBigDecimal(i);
        }

        @Override
        public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
            return cs.getObject(i, map);
        }

        @Override
        public Ref getRef(int i) throws SQLException {
            return cs.getRef(i);
        }

        @Override
        public Blob getBlob(int i) throws SQLException {
            return cs.getBlob(i);
        }

        @Override
        public Clob getClob(int i) throws SQLException {
            return cs.getClob(i);
        }

        @Override
        public Array getArray(int i) throws SQLException {
            return cs.getArray(i);
        }

        @Override
        public Date getDate(int i, Calendar calendar) throws SQLException {
            return cs.getDate(i, calendar);
        }

        @Override
        public Time getTime(int i, Calendar calendar) throws SQLException {
            return cs.getTime(i, calendar);
        }

        @Override
        public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
            return cs.getTimestamp(i, calendar);
        }

        @Override
        public void registerOutParameter(int i, int i1, String s) throws SQLException {
            cs.registerOutParameter(i, i1, s);
        }

        @Override
        public void registerOutParameter(String s, int i) throws SQLException {
            cs.registerOutParameter(s, i);
        }

        @Override
        public void registerOutParameter(String s, int i, int i1) throws SQLException {
            cs.registerOutParameter(s, i, i1);
        }

        @Override
        public void registerOutParameter(String s, int i, String s1) throws SQLException {
            cs.registerOutParameter(s, i, s1);
        }

        @Override
        public URL getURL(int i) throws SQLException {
            return cs.getURL(i);
        }

        @Override
        public void setURL(String s, URL url) throws SQLException {
            cs.setURL(s, url);
        }

        @Override
        public void setNull(String s, int i) throws SQLException {
            cs.setNull(s, i);
        }

        @Override
        public void setBoolean(String s, boolean b) throws SQLException {
            cs.setBoolean(s, b);
        }

        @Override
        public void setByte(String s, byte b) throws SQLException {
            cs.setByte(s, b);
        }

        @Override
        public void setShort(String s, short i) throws SQLException {
            cs.setShort(s, i);
        }

        @Override
        public void setInt(String s, int i) throws SQLException {
            cs.setInt(s, i);
        }

        @Override
        public void setLong(String s, long l) throws SQLException {
            cs.setLong(s, l);
        }

        @Override
        public void setFloat(String s, float v) throws SQLException {
            cs.setFloat(s, v);
        }

        @Override
        public void setDouble(String s, double v) throws SQLException {
            cs.setDouble(s, v);
        }

        @Override
        public void setBigDecimal(String s, BigDecimal bigDecimal) throws SQLException {
            cs.setBigDecimal(s, bigDecimal);
        }

        @Override
        public void setString(String s, String s1) throws SQLException {
            cs.setString(s, s1);
        }

        @Override
        public void setBytes(String s, byte[] bytes) throws SQLException {
            cs.setBytes(s, bytes);
        }

        @Override
        public void setDate(String s, Date date) throws SQLException {
            cs.setDate(s, date);
        }

        @Override
        public void setTime(String s, Time time) throws SQLException {
            cs.setTime(s, time);
        }

        @Override
        public void setTimestamp(String s, Timestamp timestamp) throws SQLException {
            cs.setTimestamp(s, timestamp);
        }

        @Override
        public void setAsciiStream(String s, InputStream inputStream, int i) throws SQLException {
            cs.setAsciiStream(s, inputStream, i);
        }

        @Override
        public void setBinaryStream(String s, InputStream inputStream, int i) throws SQLException {
            cs.setBinaryStream(s, inputStream, i);
        }

        @Override
        public void setObject(String s, Object o, int i, int i1) throws SQLException {
            cs.setObject(s, o, i, i1);
        }

        @Override
        public void setObject(String s, Object o, int i) throws SQLException {
            cs.setObject(s, o, i);
        }

        @Override
        public void setObject(String s, Object o) throws SQLException {
            cs.setObject(s, o);
        }

        @Override
        public void setCharacterStream(String s, Reader reader, int i) throws SQLException {
            cs.setCharacterStream(s, reader, i);
        }

        @Override
        public void setDate(String s, Date date, Calendar calendar) throws SQLException {
            cs.setDate(s, date, calendar);
        }

        @Override
        public void setTime(String s, Time time, Calendar calendar) throws SQLException {
            cs.setTime(s, time, calendar);
        }

        @Override
        public void setTimestamp(String s, Timestamp timestamp, Calendar calendar) throws SQLException {
            cs.setTimestamp(s, timestamp, calendar);
        }

        @Override
        public void setNull(String s, int i, String s1) throws SQLException {
            cs.setNull(s, i, s1);
        }

        @Override
        public String getString(String s) throws SQLException {
            return cs.getString(s);
        }

        @Override
        public boolean getBoolean(String s) throws SQLException {
            return cs.getBoolean(s);
        }

        @Override
        public byte getByte(String s) throws SQLException {
            return cs.getByte(s);
        }

        @Override
        public short getShort(String s) throws SQLException {
            return cs.getShort(s);
        }

        @Override
        public int getInt(String s) throws SQLException {
            return cs.getInt(s);
        }

        @Override
        public long getLong(String s) throws SQLException {
            return cs.getLong(s);
        }

        @Override
        public float getFloat(String s) throws SQLException {
            return cs.getFloat(s);
        }

        @Override
        public double getDouble(String s) throws SQLException {
            return cs.getDouble(s);
        }

        @Override
        public byte[] getBytes(String s) throws SQLException {
            return cs.getBytes(s);
        }

        @Override
        public Date getDate(String s) throws SQLException {
            return cs.getDate(s);
        }

        @Override
        public Time getTime(String s) throws SQLException {
            return cs.getTime(s);
        }

        @Override
        public Timestamp getTimestamp(String s) throws SQLException {
            return cs.getTimestamp(s);
        }

        @Override
        public Object getObject(String s) throws SQLException {
            return cs.getObject(s);
        }

        @Override
        public BigDecimal getBigDecimal(String s) throws SQLException {
            return cs.getBigDecimal(s);
        }

        @Override
        public Object getObject(String s, Map<String, Class<?>> map) throws SQLException {
            return cs.getObject(s, map);
        }

        @Override
        public Ref getRef(String s) throws SQLException {
            return cs.getRef(s);
        }

        @Override
        public Blob getBlob(String s) throws SQLException {
            return cs.getBlob(s);
        }

        @Override
        public Clob getClob(String s) throws SQLException {
            return cs.getClob(s);
        }

        @Override
        public Array getArray(String s) throws SQLException {
            return cs.getArray(s);
        }

        @Override
        public Date getDate(String s, Calendar calendar) throws SQLException {
            return cs.getDate(s, calendar);
        }

        @Override
        public Time getTime(String s, Calendar calendar) throws SQLException {
            return cs.getTime(s, calendar);
        }

        @Override
        public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {
            return cs.getTimestamp(s, calendar);
        }

        @Override
        public URL getURL(String s) throws SQLException {
            return cs.getURL(s);
        }

        @Override
        public RowId getRowId(int i) throws SQLException {
            return cs.getRowId(i);
        }

        @Override
        public RowId getRowId(String s) throws SQLException {
            return cs.getRowId(s);
        }

        @Override
        public void setRowId(String s, RowId rowId) throws SQLException {
            cs.setRowId(s, rowId);
        }

        @Override
        public void setNString(String s, String s1) throws SQLException {
            cs.setNString(s, s1);
        }

        @Override
        public void setNCharacterStream(String s, Reader reader, long l) throws SQLException {
            cs.setNCharacterStream(s, reader, l);
        }

        @Override
        public void setNClob(String s, NClob nClob) throws SQLException {
            cs.setNClob(s, nClob);
        }

        @Override
        public void setClob(String s, Reader reader, long l) throws SQLException {
            cs.setClob(s, reader, l);
        }

        @Override
        public void setBlob(String s, InputStream inputStream, long l) throws SQLException {
            cs.setBlob(s, inputStream, l);
        }

        @Override
        public void setNClob(String s, Reader reader, long l) throws SQLException {
            cs.setNClob(s, reader, l);
        }

        @Override
        public NClob getNClob(int i) throws SQLException {
            return cs.getNClob(i);
        }

        @Override
        public NClob getNClob(String s) throws SQLException {
            return cs.getNClob(s);
        }

        @Override
        public void setSQLXML(String s, SQLXML sqlxml) throws SQLException {
            cs.setSQLXML(s, sqlxml);
        }

        @Override
        public SQLXML getSQLXML(int i) throws SQLException {
            return cs.getSQLXML(i);
        }

        @Override
        public SQLXML getSQLXML(String s) throws SQLException {
            return cs.getSQLXML(s);
        }

        @Override
        public String getNString(int i) throws SQLException {
            return cs.getNString(i);
        }

        @Override
        public String getNString(String s) throws SQLException {
            return cs.getNString(s);
        }

        @Override
        public Reader getNCharacterStream(int i) throws SQLException {
            return cs.getNCharacterStream(i);
        }

        @Override
        public Reader getNCharacterStream(String s) throws SQLException {
            return cs.getNCharacterStream(s);
        }

        @Override
        public Reader getCharacterStream(int i) throws SQLException {
            return cs.getCharacterStream(i);
        }

        @Override
        public Reader getCharacterStream(String s) throws SQLException {
            return cs.getCharacterStream(s);
        }

        @Override
        public void setBlob(String s, Blob blob) throws SQLException {
            cs.setBlob(s, blob);
        }

        @Override
        public void setClob(String s, Clob clob) throws SQLException {
            cs.setClob(s, clob);
        }

        @Override
        public void setAsciiStream(String s, InputStream inputStream, long l) throws SQLException {
            cs.setAsciiStream(s, inputStream, l);
        }

        @Override
        public void setBinaryStream(String s, InputStream inputStream, long l) throws SQLException {
            cs.setBinaryStream(s, inputStream, l);
        }

        @Override
        public void setCharacterStream(String s, Reader reader, long l) throws SQLException {
            cs.setCharacterStream(s, reader, l);
        }

        @Override
        public void setAsciiStream(String s, InputStream inputStream) throws SQLException {
            cs.setAsciiStream(s, inputStream);
        }

        @Override
        public void setBinaryStream(String s, InputStream inputStream) throws SQLException {
            cs.setBinaryStream(s, inputStream);
        }

        @Override
        public void setCharacterStream(String s, Reader reader) throws SQLException {
            cs.setCharacterStream(s, reader);
        }

        @Override
        public void setNCharacterStream(String s, Reader reader) throws SQLException {
            cs.setNCharacterStream(s, reader);
        }

        @Override
        public void setClob(String s, Reader reader) throws SQLException {
            cs.setClob(s, reader);
        }

        @Override
        public void setBlob(String s, InputStream inputStream) throws SQLException {
            cs.setBlob(s, inputStream);
        }

        @Override
        public void setNClob(String s, Reader reader) throws SQLException {
            cs.setNClob(s, reader);
        }

        @Override
        public <T> T getObject(int i, Class<T> aClass) throws SQLException {
            return cs.getObject(i, aClass);
        }

        @Override
        public <T> T getObject(String s, Class<T> aClass) throws SQLException {
            return cs.getObject(s, aClass);
        }

        @Override
        public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
            cs.setObject(parameterName, x, targetSqlType, scaleOrLength);
        }

        @Override
        public void setObject(String parameterName, Object x, SQLType targetSqlType) throws SQLException {
            cs.setObject(parameterName, x, targetSqlType);
        }

        @Override
        public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
            cs.registerOutParameter(parameterIndex, sqlType);
        }

        @Override
        public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
            cs.registerOutParameter(parameterIndex, sqlType, scale);
        }

        @Override
        public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
            cs.registerOutParameter(parameterIndex, sqlType, typeName);
        }

        @Override
        public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
            cs.registerOutParameter(parameterName, sqlType);
        }

        @Override
        public void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
            cs.registerOutParameter(parameterName, sqlType, scale);
        }

        @Override
        public void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
            cs.registerOutParameter(parameterName, sqlType, typeName);
        }

        @Override
        public void setNull(int i, int i1) throws SQLException {
            ps.setNull(i, i1);
        }

        @Override
        public void setBoolean(int i, boolean b) throws SQLException {
            ps.setBoolean(i, b);
        }

        @Override
        public void setByte(int i, byte b) throws SQLException {
            ps.setByte(i, b);
        }

        @Override
        public void setShort(int i, short i1) throws SQLException {
            ps.setShort(i, i1);
        }

        @Override
        public void setFloat(int i, float v) throws SQLException {
            ps.setFloat(i, v);
        }

        @Override
        public void setBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
            ps.setBigDecimal(i, bigDecimal);
        }

        @Override
        public void setBytes(int i, byte[] bytes) throws SQLException {
            ps.setBytes(i, bytes);
        }

        @Override
        public void setDate(int i, Date date) throws SQLException {
            ps.setDate(i, date);
        }

        @Override
        public void setTime(int i, Time time) throws SQLException {
            ps.setTime(i, time);
        }

        @Override
        public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
            ps.setTimestamp(i, timestamp);
        }

        @Override
        public void setAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
            ps.setAsciiStream(i, inputStream, i1);
        }

        @Deprecated(since = "1.2")
        @Override
        public void setUnicodeStream(int i, InputStream inputStream, int i1) throws SQLException {
            ps.setUnicodeStream(i, inputStream, i1);
        }

        @Override
        public void setBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
            ps.setBinaryStream(i, inputStream, i1);
        }

        @Override
        public void clearParameters() throws SQLException {
            ps.clearParameters();
        }

        @Override
        public void setObject(int i, Object o, int i1) throws SQLException {
            ps.setObject(i, o, i1);
        }

        @Override
        public void setObject(int i, Object o) throws SQLException {
            ps.setObject(i, o);
        }

        @Override
        public void setCharacterStream(int i, Reader reader, int i1) throws SQLException {
            ps.setCharacterStream(i, reader, i1);
        }

        @Override
        public void setRef(int i, Ref ref) throws SQLException {
            ps.setRef(i, ref);
        }

        @Override
        public void setBlob(int i, Blob blob) throws SQLException {
            ps.setBlob(i, blob);
        }

        @Override
        public void setClob(int i, Clob clob) throws SQLException {
            ps.setClob(i, clob);
        }

        @Override
        public void setArray(int i, Array array) throws SQLException {
            ps.setArray(i, array);
        }

        @Override
        public ResultSetMetaData getMetaData() throws SQLException {
            return ps.getMetaData();
        }

        @Override
        public void setDate(int i, Date date, Calendar calendar) throws SQLException {
            ps.setDate(i, date, calendar);
        }

        @Override
        public void setTime(int i, Time time, Calendar calendar) throws SQLException {
            ps.setTime(i, time, calendar);
        }

        @Override
        public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
            ps.setTimestamp(i, timestamp, calendar);
        }

        @Override
        public void setNull(int i, int i1, String s) throws SQLException {
            ps.setNull(i, i1, s);
        }

        @Override
        public void setURL(int i, URL url) throws SQLException {
            ps.setURL(i, url);
        }

        @Override
        public ParameterMetaData getParameterMetaData() throws SQLException {
            return ps.getParameterMetaData();
        }

        @Override
        public void setRowId(int i, RowId rowId) throws SQLException {
            ps.setRowId(i, rowId);
        }

        @Override
        public void setNString(int i, String s) throws SQLException {
            ps.setNString(i, s);
        }

        @Override
        public void setNCharacterStream(int i, Reader reader, long l) throws SQLException {
            ps.setNCharacterStream(i, reader, l);
        }

        @Override
        public void setNClob(int i, NClob nClob) throws SQLException {
            ps.setNClob(i, nClob);
        }

        @Override
        public void setClob(int i, Reader reader, long l) throws SQLException {
            ps.setClob(i, reader, l);
        }

        @Override
        public void setBlob(int i, InputStream inputStream, long l) throws SQLException {
            ps.setBlob(i, inputStream, l);
        }

        @Override
        public void setNClob(int i, Reader reader, long l) throws SQLException {
            ps.setNClob(i, reader, l);
        }

        @Override
        public void setSQLXML(int i, SQLXML sqlxml) throws SQLException {
            ps.setSQLXML(i, sqlxml);
        }

        @Override
        public void setObject(int i, Object o, int i1, int i2) throws SQLException {
            ps.setObject(i, o, i1, i2);
        }

        @Override
        public void setAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
            ps.setAsciiStream(i, inputStream, l);
        }

        @Override
        public void setBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
            ps.setBinaryStream(i, inputStream, l);
        }

        @Override
        public void setCharacterStream(int i, Reader reader, long l) throws SQLException {
            ps.setCharacterStream(i, reader, l);
        }

        @Override
        public void setAsciiStream(int i, InputStream inputStream) throws SQLException {
            ps.setAsciiStream(i, inputStream);
        }

        @Override
        public void setBinaryStream(int i, InputStream inputStream) throws SQLException {
            ps.setBinaryStream(i, inputStream);
        }

        @Override
        public void setCharacterStream(int i, Reader reader) throws SQLException {
            ps.setCharacterStream(i, reader);
        }

        @Override
        public void setNCharacterStream(int i, Reader reader) throws SQLException {
            ps.setNCharacterStream(i, reader);
        }

        @Override
        public void setClob(int i, Reader reader) throws SQLException {
            ps.setClob(i, reader);
        }

        @Override
        public void setBlob(int i, InputStream inputStream) throws SQLException {
            ps.setBlob(i, inputStream);
        }

        @Override
        public void setNClob(int i, Reader reader) throws SQLException {
            ps.setNClob(i, reader);
        }

        @Override
        public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
            ps.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
        }

        @Override
        public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
            ps.setObject(parameterIndex, x, targetSqlType);
        }

        @Override
        public long executeLargeUpdate() throws SQLException {
            return ps.executeLargeUpdate();
        }

        @Override
        public int getMaxFieldSize() throws SQLException {
            return delegate.getMaxFieldSize();
        }

        @Override
        public void setMaxFieldSize(int i) throws SQLException {
            delegate.setMaxFieldSize(i);
        }

        @Override
        public int getMaxRows() throws SQLException {
            return delegate.getMaxRows();
        }

        @Override
        public void setMaxRows(int i) throws SQLException {
            delegate.setMaxRows(i);
        }

        @Override
        public void setEscapeProcessing(boolean b) throws SQLException {
            delegate.setEscapeProcessing(b);
        }

        @Override
        public int getQueryTimeout() throws SQLException {
            return delegate.getQueryTimeout();
        }

        @Override
        public void setQueryTimeout(int i) throws SQLException {
            delegate.setQueryTimeout(i);
        }

        @Override
        public void cancel() throws SQLException {
            delegate.cancel();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return delegate.getWarnings();
        }

        @Override
        public void clearWarnings() throws SQLException {
            delegate.clearWarnings();
        }

        @Override
        public void setCursorName(String s) throws SQLException {
            delegate.setCursorName(s);
        }

        @Override
        public ResultSet getResultSet() throws SQLException {
            return delegate.getResultSet();
        }

        @Override
        public int getUpdateCount() throws SQLException {
            return delegate.getUpdateCount();
        }

        @Override
        public boolean getMoreResults() throws SQLException {
            return delegate.getMoreResults();
        }

        @Override
        public void setFetchDirection(int i) throws SQLException {
            delegate.setFetchDirection(i);
        }

        @Override
        public int getFetchDirection() throws SQLException {
            return delegate.getFetchDirection();
        }

        @Override
        public void setFetchSize(int i) throws SQLException {
            delegate.setFetchSize(i);
        }

        @Override
        public int getFetchSize() throws SQLException {
            return delegate.getFetchSize();
        }

        @Override
        public int getResultSetConcurrency() throws SQLException {
            return delegate.getResultSetConcurrency();
        }

        @Override
        public int getResultSetType() throws SQLException {
            return delegate.getResultSetType();
        }

        @Override
        public void clearBatch() throws SQLException {
            delegate.clearBatch();
        }

        @Override
        public int[] executeBatch() throws SQLException {
            return delegate.executeBatch();
        }

        @Override
        public boolean getMoreResults(int i) throws SQLException {
            return delegate.getMoreResults(i);
        }

        @Override
        public ResultSet getGeneratedKeys() throws SQLException {
            return delegate.getGeneratedKeys();
        }

        @Override
        public int getResultSetHoldability() throws SQLException {
            return delegate.getResultSetHoldability();
        }

        @Override
        public boolean isClosed() throws SQLException {
            return delegate.isClosed();
        }

        @Override
        public void setPoolable(boolean b) throws SQLException {
            delegate.setPoolable(b);
        }

        @Override
        public boolean isPoolable() throws SQLException {
            return delegate.isPoolable();
        }

        @Override
        public void closeOnCompletion() throws SQLException {
            delegate.closeOnCompletion();
        }

        @Override
        public boolean isCloseOnCompletion() throws SQLException {
            return delegate.isCloseOnCompletion();
        }

        @Override
        public long getLargeUpdateCount() throws SQLException {
            return delegate.getLargeUpdateCount();
        }

        @Override
        public void setLargeMaxRows(long max) throws SQLException {
            delegate.setLargeMaxRows(max);
        }

        @Override
        public long getLargeMaxRows() throws SQLException {
            return delegate.getLargeMaxRows();
        }

        @Override
        public long[] executeLargeBatch() throws SQLException {
            return delegate.executeLargeBatch();
        }

        @Override
        public String enquoteLiteral(String val) throws SQLException {
            return delegate.enquoteLiteral(val);
        }

        @Override
        public String enquoteIdentifier(String identifier, boolean alwaysQuote) throws SQLException {
            return delegate.enquoteIdentifier(identifier, alwaysQuote);
        }

        @Override
        public boolean isSimpleIdentifier(String identifier) throws SQLException {
            return delegate.isSimpleIdentifier(identifier);
        }

        @Override
        public String enquoteNCharLiteral(String val) throws SQLException {
            return delegate.enquoteNCharLiteral(val);
        }

        // ==================== Statement（必须走 connection） ====================

        @Override
        public ResultSet executeQuery(String sql) throws SQLException {
            return (delegate).executeQuery(connection.handleSQL(sql));
        }

        @Override
        public int executeUpdate(String sql) throws SQLException {
            return (delegate).executeUpdate(connection.handleSQL(sql));
        }

        @Override
        public boolean execute(String sql) throws SQLException {
            return (delegate).execute(connection.handleSQL(sql));
        }

        // --- 全部重载（不能漏） ---

        @Override
        public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
            return (delegate).execute(connection.handleSQL(sql), autoGeneratedKeys);
        }

        @Override
        public boolean execute(String sql, int[] columnIndexes) throws SQLException {
            return (delegate).execute(connection.handleSQL(sql), columnIndexes);
        }

        @Override
        public boolean execute(String sql, String[] columnNames) throws SQLException {
            return (delegate).execute(connection.handleSQL(sql), columnNames);
        }

        @Override
        public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
            return (delegate).executeUpdate(connection.handleSQL(sql), autoGeneratedKeys);
        }

        @Override
        public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
            return (delegate).executeUpdate(connection.handleSQL(sql), columnIndexes);
        }

        @Override
        public int executeUpdate(String sql, String[] columnNames) throws SQLException {
            return (delegate).executeUpdate(connection.handleSQL(sql), columnNames);
        }

        @Override
        public void addBatch(String sql) throws SQLException {
            (delegate).addBatch(connection.handleSQL(sql));
        }

        // ==================== PreparedStatement（不再处理 SQL） ====================

        @Override
        public ResultSet executeQuery() throws SQLException {
            return ((PreparedStatement) delegate).executeQuery();
        }

        @Override
        public int executeUpdate() throws SQLException {
            return ((PreparedStatement) delegate).executeUpdate();
        }

        @Override
        public boolean execute() throws SQLException {
            return ((PreparedStatement) delegate).execute();
        }

        @Override
        public void addBatch() throws SQLException {
            ((PreparedStatement) delegate).addBatch();
        }

        // ==================== 参数透传 ====================

        @Override
        public void setString(int parameterIndex, String x) throws SQLException {
            ((PreparedStatement) delegate).setString(parameterIndex, x);
        }

        @Override
        public void setInt(int parameterIndex, int x) throws SQLException {
            ((PreparedStatement) delegate).setInt(parameterIndex, x);
        }

        @Override
        public void setLong(int parameterIndex, long x) throws SQLException {
            ((PreparedStatement) delegate).setLong(parameterIndex, x);
        }

        @Override
        public void setDouble(int parameterIndex, double x) throws SQLException {
            ((PreparedStatement) delegate).setDouble(parameterIndex, x);
        }

        // ==================== 基础透传 ====================

        @Override
        public void close() throws SQLException {
            delegate.close();
        }

        @Override
        public Connection getConnection() {
            return connection;
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return delegate.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return delegate.isWrapperFor(iface);
        }

        @Override
        public long executeLargeUpdate(String sql) throws SQLException {
            return (delegate).executeLargeUpdate(connection.handleSQL(sql));
        }

        @Override
        public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
            return (delegate).executeLargeUpdate(connection.handleSQL(sql), autoGeneratedKeys);
        }

        @Override
        public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
            return (delegate).executeLargeUpdate(connection.handleSQL(sql), columnIndexes);
        }

        @Override
        public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
            return (delegate).executeLargeUpdate(connection.handleSQL(sql), columnNames);
        }
    }
}
