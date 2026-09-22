# 2. 注册内容

扩展包通过 `@ApplicationPackageProvider` 静态方法声明内容。一个方法对应一个 **package**（一组模块 / 服务 / 配置 / 语言的集合）。

```java
@ApplicationPackageProvider(id = "my-pack", description = "My extension pack.")
static void pack(ContentBuilder b) {
    var p = b.getAttachment(PluginPackageAttachment.class);

    b.service(MyService.class);   // 注册服务
    b.module(MyModule.class);     // 注册模块

    p.config("my-pack");                       // 配置：templates/config/my-pack.yml
    p.language("my-pack", "zh_cn");            // 语言：templates/lang/my-pack.zh_cn.yml
    p.language("/my-pack", "zh_cn");           // 嵌套语言：templates/lang/my-pack/my-pack.zh_cn.yml
}
```

## 2.1 @ApplicationPackageProvider

| 字段 | 说明 |
|------|------|
| `id` | package 命名空间 id，模块/服务 fullId 的前缀 |
| `description` | 描述 |
| `internal` | 是否为内部包（默认 `false`） |

## 2.2 注册服务

`b.service(Class)` 注册一个服务实现（`@ApplicationService`），核心会创建并管理其生命周期。详见「4. 服务组件开发」。

```java
b.service(MyService.class);
```

## 2.3 注册模块

`b.module(Class)` 注册一个模块（`@ApplicationModule`），模块可被单独启用/禁用。详见「3. 模块开发」。

```java
b.module(MyModule.class);
```

## 2.4 注册配置文件

`p.config(id)` 加载 `templates/config/<id>.yml` 作为默认配置，模块内通过 `config()` 访问：

```java
p.config("my-pack");
```

## 2.5 注册语言文件

`p.language(id, locale)` 加载 `templates/lang/<id>.<locale>.yml`：

```java
p.language("my-pack", "zh_cn");
p.language("my-pack", "en_us");
```

若 id 以 `/` 开头，则视为**嵌套语言包**（`NestedLanguagePack`），路径变为 `templates/lang/<id>/<id>.<locale>.yml`：

```java
p.language("/my-pack", "zh_cn");   // templates/lang/my-pack/my-pack.zh_cn.yml
```

语言文件结构（顶层 `language:`，随后按模块 id 分段）：

```yaml
language:
  -module-name:
    my-module: 我的模块
  my-module:
    hello: '{global#info}你好，{#white}{#var}{#gray}！'
```

模块内通过 `language().item("<key>").send(audience, args...)` 使用，`{#var}` / `{}` 为顺序占位符。
