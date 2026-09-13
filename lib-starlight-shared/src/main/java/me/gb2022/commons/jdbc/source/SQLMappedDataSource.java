package me.gb2022.commons.jdbc.source;

import javax.sql.DataSource;
import java.sql.Connection;

public final class SQLMappedDataSource extends WrappedDataSource {
    private final SQLMapper mapper;

    public SQLMappedDataSource(DataSource dataSource, SQLMapper mapper) {
        super(dataSource);
        this.mapper = mapper;
    }

    @Override
    protected Connection wrapConnection(Connection connection) {
        return new WrappedConnection(connection,this.mapper);
    }

    public SQLMapper getMapper() {
        return mapper;
    }
}
