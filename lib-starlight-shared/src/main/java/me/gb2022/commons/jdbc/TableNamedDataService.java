package me.gb2022.commons.jdbc;

import me.gb2022.commons.jdbc.source.SQLMappedDataSource;
import me.gb2022.commons.jdbc.source.SQLMapper;
import org.atcraftmc.starlight.shared.JDBCService;

import javax.sql.DataSource;

public abstract class TableNamedDataService extends JDBCDataService implements GenericQueryDatasourceProvider{
    private final SQLMapper mapper = new SQLMapper();
    private final String tableName;

    protected TableNamedDataService(String tableName) {
        this.tableName = tableName;
    }

    public void initMapper(SQLMapper mapper){
        mapper.replaceSQL("_table_",this.tableName);
    }

    @Override
    public void init(DataSource datasource, JDBCService service) {
        initMapper(this.mapper);
        super.init(new SQLMappedDataSource(datasource, this.mapper), service);
    }

    public SQLMapper getSQLMapper() {
        return mapper;
    }

    public String getTableName() {
        return tableName;
    }
}
