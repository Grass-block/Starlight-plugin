package me.gb2022.commons.jdbc.trait;

import me.gb2022.commons.jdbc.GenericQueryDatasourceProvider;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DataServiceAttachment<V> extends GenericQueryDatasourceProvider {
    default void onUpdate() {
    }

    V decode(ResultSet rs) throws SQLException;
}
