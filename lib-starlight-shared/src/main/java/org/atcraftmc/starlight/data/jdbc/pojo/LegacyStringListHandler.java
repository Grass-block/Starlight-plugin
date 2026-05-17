package org.atcraftmc.starlight.data.jdbc.pojo;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public final class LegacyStringListHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(
            PreparedStatement ps, int i,
            List<String> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, String.join(":", parameter));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private List<String> parse(String s) {
        if (s == null || s.isEmpty()) {
            return List.of();
        }
        return Arrays.asList(s.split(":"));
    }
}