package me.gb2022.commons.jdbc;

import javax.sql.DataSource;

public interface GenericQueryDatasourceProvider {
    DataSource getGenerateQuerySource();
}
