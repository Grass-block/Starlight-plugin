# 4. 服务组件开发

服务（Service）由核心创建并管理生命周期，供模块或其他服务调用。通过 `@ApplicationService` 声明，并在扩展包中用 `b.service(Class)` 注册。

## 4.1 定义服务

```java
@ApplicationService(id = "my-service", impl = MyService.Impl.class)
public interface MyService extends BukkitService {
    @ServiceInject
    ServiceHolder<MyService> INSTANCE = new ServiceHolder<>();

    static MyService instance() {
        return INSTANCE.get();
    }

    void doSomething();

    final class Impl implements MyService {
        @Override
        public void enable() {
            // 服务启用
        }

        @Override
        public void disable() {
            // 服务停用
        }

        @Override
        public void doSomething() {
            // ...
        }
    }
}
```

## 4.2 @ApplicationService

| 字段 | 说明 |
|------|------|
| `id` | 服务 id |
| `impl` | 实现类（不写则由框架解析） |
| `export` | 是否导出到远程（默认 `false`） |
| `layer` | 服务层级（`ServiceLayer`） |

## 4.3 生命周期

`BukkitService` 同时继承 `Service`（含 `enable()` / `disable()`）与 Bukkit `Listener`，因此服务本身也可以直接写 `@EventHandler`。

```java
@EventHandler
public void onSomeEvent(SomeEvent event) { ... }
```

## 4.4 获取实例

- 接口内提供 `static X instance()`（推荐）。
- 或注入 `@ServiceInject ServiceHolder<X>` 后取 `INSTANCE.get()`。

## 4.5 注册

在扩展包的 `@ApplicationPackageProvider` 方法中注册（见「2. 注册内容」）：

```java
b.service(MyService.class);
```