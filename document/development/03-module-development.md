# 3. 模块开发

模块是最小的功能单元，继承 `BukkitAbstractModule`，用 `@ApplicationModule` 声明。

```java

@AutoRegister(Registrations.SERVER_EVENT)
@ApplicationModule(id = "my-module", version = "1.0.0", description = "Does something.")
public final class MyModule extends BukkitAbstractModule {
    @Override
    public void enable() {
        // 模块启用
    }

    @Override
    public void disable() {
        // 模块禁用 / 卸载
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        language().item("welcome").send(QLib.audience(event.getPlayer()), event.getPlayer().getName());
    }
}
```

## 3.1 注解

| 注解                                          | 说明                                                                                                       |
|---------------------------------------------|----------------------------------------------------------------------------------------------------------|
| `@ApplicationModule`                        | 声明模块：`id`（必填）、`version`、`description`、`defaultEnable`                                                    |
| `@AutoRegister(Registrations.SERVER_EVENT)` | 自动注册 Bukkit 事件监听器；可组合多个注册表，如 `@AutoRegister({Registrations.SERVER_EVENT, Registrations.PLUGIN_MESSAGE})` |

## 3.2 生命周期

- `enable()`：模块启用时调用。
- `disable()`：模块禁用 / 卸载时调用。
- `initialize()`：初始化；父类已在此注入 `language()` 与 `config()`，一般无需覆写。

## 3.3 配置与语言

- `config()`：返回 `ConfigEntry`。读取配置项：
  ```java
  var name = config().value("name").string();
  var count = config().value("count").intValue();
  var enabled = config().value("enabled").bool();
  var ratio = config().value("ratio").floatValue();
  var section = config().value("group").section();
  ```
- `language()`：返回 `LanguageEntry`。
  ```java
  language().item("hello").send(QLib.audience(player), player.getName());
  var text = language().inline("some-template", LocaleService.locale(player));
  ```
- 也可用 `@Inject` 注入：
  ```java
  @Inject private LanguageEntry language;
  @Inject("-starlight.my.permission;false") private Permission myPermission;
  ```

## 3.4 命令

### 模块即命令（推荐）

继承 `SLCommandModule`（其本身即 `PluginCommandExecutor`），在类上标注 `@BukkitCommand`：

```java

@BukkitCommand(name = "mycmd", permission = "+starlight.mycmd")
public final class MyCommand extends SLCommandModule {
    @Override
    public void execute(CommandExecution context) {
        var sender = context.requireSenderAsPlayer();
        // ...
    }

    @Override
    public void suggest(CommandSuggestion suggestion) {
        suggestion.suggest(0, "list", "info");
    }
}
```

### 独立命令类

用 `@CommandProvider(X.class)` 挂载嵌套命令类：

```java

@CommandProvider(MyModule.MyCommand.class)
public final class MyModule extends BukkitAbstractModule {
    @BukkitCommand(name = "mycmd",permission="-my.command")
    public static final class MyCommand extends ModuleCommand<MyModule> {
        @Override
        public void execute(CommandExecution context) { /* ... */ }

        @Override
        public void suggest(CommandSuggestion suggestion) { /* ... */ }
    }
}
```

### 你可能会需要的东西

**CommandExecution**（读取参数 / 校验）

- `Player requireSenderAsPlayer()` — 取执行者并要求必须是玩家。
- `CommandSender getSender()` — 取执行者（不限类型）。
- `String[] getArgs()` — 取原始参数数组。
- `boolean hasArgumentAt(int index)` — 指定位置是否有参数。
- `String requireArgumentAt(int index)` — 取指定位置参数（缺失报错）。
- `String requireEnum(int index, String... options)` — 取参数并要求属于给定选项之一。
- `int requireArgumentInteger(int index, NumberLimitation... limit)` — 取整数参数（可加范围限制）。
- `double requireArgumentDouble(int index, NumberLimitation... limit)` / `float requireArgumentFloat(...)` — 取浮点参数。
- `Player requirePlayer(int index)` — 取在线玩家参数。
- `OfflinePlayer requireOfflinePlayer(int index)` — 取离线玩家参数。
- `String requireRemainAsParagraph(int index, boolean color)` — 取 index 起剩余内容为一段文本。
- `void requirePermission(String permission)` — 校验权限（不足则提示）。
- `void matchArgument(int index, String value, Runnable action)` — 指定位置等于 value 时执行。

**CommandSuggestion**（Tab 补全）

- `void suggest(int index, String... options)` — 为指定位置补全候选。
- `void suggest(int index, Collection<String> options)` — 同上（集合形式）。
- `void suggestPlayers(int index)` — 补全在线玩家名。
- `void matchArgument(int index, String value, Consumer<CommandSuggestion> action)` — 指定位置等于 value 时追加子补全。
- `List<String> getBuffer()` — 取当前已输入的参数。
- `Player getSenderAsPlayer()` — 取补全发起者（玩家）。

## 3.5 子组件系统（Sub-Component）

子组件用于把一个模块中**平台/版本相关**的逻辑拆成可选单元：当运行环境不支持时，只跳过该子组件，而不影响模块其余部分。

子组件继承 `SLModuleComponent<E extends BukkitModule>`（其本身是 `SubComponent` 且实现了 Bukkit `Listener`），并在模块类上用
`@ComponentProvider` 注册：

```java

@ComponentProvider(Mute.PaperListener.class)          // 多个用 @ComponentProvider({A.class, B.class})
@ApplicationModule(id = "mute", version = "1.0.2", description = "...")
public final class Mute extends BukkitAbstractModule {
    void checkEvent(Player player, Cancellable event, boolean async) { /* ... */ }

    @AutoRegister(Registrations.SERVER_EVENT)
    public static final class PaperListener extends SLModuleComponent<Mute> {
        @Override
        public void checkCompatibility() throws APIIncompatibleException {
            Compatibility.requireClass(() -> Class.forName("io.papermc.paper.event.player.AsyncChatEvent"));
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onAsyncChat(AsyncPlayerChatEvent event) {
            this.parent.checkEvent(event.getPlayer(), event, true);   // 通过 parent 访问所属模块
        }
    }
}
```

要点：

- 子组件是模块的**静态嵌套类**，泛型参数为所属模块类型。
- 通过 `this.parent` 访问所属模块实例。
- 子组件上同样可以用 `@AutoRegister` + `@EventHandler`。
- 兼容性不满足时（`checkCompatibility` 抛 `APIIncompatibleException`），该子组件被跳过，模块其余功能照常。

## 3.6 @Inject 可注入内容

模块（及子组件、命令）的字段可用 `@Inject` 注入（`me.gb2022.commons.reflect.Inject`）。参数用 `;` 分隔（`/` 会被自动规范化为
`;`）。

| 类型                         | 参数                 | 说明                                                      |
|----------------------------|--------------------|---------------------------------------------------------|
| `Logger`                   | -                  | 模块日志                                                    |
| `LanguageEntry`            | -                  | 模块语言入口                                                  |
| `LanguageItem`             | `key`              | 等价于 `language().item(key)`                              |
| `ApplicationPackage`       | -                  | 所属 package                                              |
| `Asset`                    | `path[;useCache]`  | 单个资源文件                                                  |
| `AssetGroup`               | `path[;useCache]`  | 资源目录                                                    |
| `Permission`               | `name[;default]`   | 权限对象，如 `@Inject("-starlight.maintenance.bypass;false")` |
| `org.bukkit.plugin.Plugin` | -                  | 所属插件实例                                                  |
| `SimpleRegionService`      | `datasource;table` | JDBC 数据服务                                               |
| `WaypointService`          | `datasource;table` | JDBC 数据服务                                               |
| `BanEntryService`          | `datasource;table` | JDBC 数据服务                                               |
| `FlexibleMapService`       | `datasource;table` | JDBC 数据服务                                               |

示例：

```java

@Inject
private LanguageEntry language;
@Inject("-starlight.maintenance.bypass;false")
private Permission bypass;
@Inject("starlight:shared/sl_mute")
private BanEntryService muteData;   // datasource=starlight:shared, table=sl_mute
```

## 3.7 包的创建（@ApplicationPackageProvider）

模块必须归属到一个 **package**。在扩展包中声明静态 `@ApplicationPackageProvider` 方法，用 `ContentBuilder` 把模块注册进去：

```java

@ApplicationPackageProvider(id = "my-pack", description = "My extension pack.")
static void pack(ContentBuilder b) {
    b.module(MyModule.class);
    b.module(MyOtherModule.class);
}
```

- 模块的 fullId = `<package-id>:<module-id>`，例如 `my-pack:my-module`。
- 同一个方法里还可注册服务、配置文件、语言文件（`b.service(...)`、`PluginPackageAttachment.config/language`），详见「2. 注册内容」。

## 3.8 兼容性检测（Compatibility）

模块或子组件可覆写 `checkCompatibility()`，在不满足条件时抛出 `me.gb2022.commons.compatibility.APIIncompatibleException`
。框架在加载阶段调用该方法；抛出异常时对应模块/子组件会被跳过。

```java

@Override
public void checkCompatibility() throws APIIncompatibleException {
    Compatibility.requireClass(() -> Class.forName("org.bukkit.Nameable"));
    Compatibility.requireMethod(() -> Nameable.class.getDeclaredMethod("customName"));
}
```

`Compatibility` 提供的断言：

| 方法                                 | 说明                                                             |
|------------------------------------|----------------------------------------------------------------|
| `requireClass(ClassAssertion)`     | 要求类存在，如 `requireClass(() -> Class.forName("..."))`             |
| `requireMethod(MethodAssertion)`   | 要求方法存在，如 `requireMethod(() -> X.class.getDeclaredMethod("y"))` |
| `requirePlugin(String)`            | 要求指定插件已安装                                                      |
| `requirePDC()`                     | 要求支持 PersistentDataContainer                                   |
| `blackListPlatform(APIProfile...)` | 黑名单平台                                                          |
| `assertion(boolean)`               | 自定义布尔断言                                                        |
| `reversed(String, Runnable)`       | 要求给定操作**失败**，否则抛异常                                             |

