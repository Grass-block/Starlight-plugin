## 使用数据库

继承 `TableNamedDataService` 可获得虚拟表名 mapper 与建表钩子：

```java
public final class MyDataService extends TableNamedDataService {
    public MyDataService() {
        super("my_table");
    }

    @Override
    public void initMapper(SQLMapper mapper) {
        super.initMapper(mapper);
        mapper.replaceSQL("_my_table_", this.getTableName());
    }

    @Override
    public PreparedStatement createTable(Connection conn) throws SQLException {
        return conn.prepareStatement("""
                CREATE TABLE IF NOT EXISTS _my_table_ (
                    uuid CHAR(36) PRIMARY KEY,
                    data VARCHAR(16384) NOT NULL
                );
                """);
    }
}
```

`_table_`（基类）与自定义占位符（如 `_my_table_`）会在语句执行前被替换为真实表名，SQL 里不要写死表名。所有 UUID 按项目约定以 `CHAR(36)` 存储、序列化为字符串。
