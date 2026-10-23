# 5. 聊天转发与语言 API（26.6.4）

聊天服务位于 `starlight-bukkit-core`。新聊天扩展优先监听 `org.atcraftmc.starlight.api.event.ForwardedChatEvent`；需要修改整行布局时操作事件的 `LazyChatRenderer`，需要修改正文时操作消息或构建器。

## 5.1 事件与渲染流程

Paper 环境从 `AsyncChatEvent` 创建 `ForwardedChatEvent`，保留原渲染器，触发扩展事件后将结果正文、取消状态和渲染器交还 Paper。未修改的 `LazyChatRenderer` 会直接委托原渲染器；仅增加前后缀时也可保留原渲染结果。

没有 Paper 聊天 API 时，从 `AsyncPlayerChatEvent` 创建事件，把原 `String.format` 风格格式转换为 `{0}` / `{1}` 格式，并逐个在线玩家渲染、发送，再输出控制台消息。

当前 Bukkit 兼容分支仍有实现限制：它未将 `ForwardedChatEvent.isCancelled()` 用于阻止转发，读取正文时使用 `getMessage()` 而非 `getOutcomeMessage()`，也未取消原 `AsyncPlayerChatEvent`。因此不能在该分支保证原文构建器、取消或去重行为与 Paper 一致。

## 5.2 ForwardedChatEvent

| 方法 | 用途 |
|----|----|
| `getSender()` | 获取发送玩家 |
| `getMessage()` / `setMessage(Component)` | 读取 / 设置当前组件；设置组件时同步更新 legacy 形式的原始文本 |
| `getRawChatMessage()` / `setRawChatMessage(String)` | 读取 / 更新文本形式；更新后标记需要重建 |
| `setBuilder(Function<String, ComponentLike>)` | 替换正文构建器，并标记需要重建；后设置的构建器覆盖前者 |
| `getOutcomeMessage()` | 无需重建时返回当前组件；需要重建时调用构建器，没有构建器则创建纯文本组件 |
| `getRenderer()` | 获取当前 `LazyChatRenderer` |
| `setCancelled(boolean)` | 设置取消状态；当前 Paper 分支会复制回原聊天事件 |

只需要添加可点击后缀时，不必覆盖正文或整体模板：

```java
@EventHandler
public void onChat(ForwardedChatEvent event) {
	var suffix = QLib.textBuilder().buildComponent(" {#gray}[自定义后缀]");
	event.getRenderer().postFix(suffix);
}
```

监听器所属模块仍须声明 `@AutoRegister(Registrations.SERVER_EVENT)`。聊天来源可能是异步事件，世界和实体操作应提交到适当的调度器。

## 5.3 LazyChatRenderer

| 方法 | 行为 |
|----|----|
| `prefix(...)` | 插入前缀列表最前端 |
| `prefixNearest(...)` | 追加到前缀列表末端，最接近主行 |
| `postFix(...)` | 追加到后缀列表末端 |
| `postFixNearest(...)` | 插入后缀列表最前端，最接近主行 |
| `template(String)` | 设置主行模板，触发自定义主行渲染；参数 `{0}` 为显示名、`{1}` 为正文 |
| `transformer(ChatContentTransformer)` | 注册按接收者处理正文的变换器，触发自定义主行渲染 |

前后缀支持固定组件和 `ChatContentProvider`。调用顺序影响列表顺序；多个模块同时修改模板时，最后设置的模板生效。

`ChatForwardingEvent` 仍存在，作用于面向接收者的成品消息行。更新日志已将其列为弃用方向，但当前类型没有 Java `@Deprecated` 注解。新扩展使用 `ForwardedChatEvent`；旧事件的取消状态在 Paper 渲染器中未被检查，且 Bukkit 兼容分支构造旧事件时传入的 sender / viewer 顺序与 Paper 不同，不宜据此实现跨平台访问控制。

## 5.4 语言服务

玩家语言使用 `BukkitLocaleService.instance().getLocale(player)`，或通过 `QLib.audience(player).pointed().locale()` 获取。`BukkitLocaleService.getInstance()` 已更名为 `instance()`；旧 `LocaleService.locale(Object)` 仍保留但标记为 `@Deprecated`。

语言命令的注册与卸载移到 `BukkitLocaleService`，权限为 `starlight.locale`，别名 `language`、`lang`。服务启用时会检查已有在线玩家；自定义语言通过 `/locale auto` 清除。`gui` 参数仍是未完成的入口。

## 5.5 TemplateExpansion 与领地数据

`TemplateExpansion` 新增预填值，使用 `replacement(name, value)` 注册固定替换。例如聊天格式预填 `{time}` 与 `{world}`，随后只需 `expand(template)`：

```java
var expansion = TemplateExpansion.build(builder -> {
	builder.replacement("time", "12:00:00");
	builder.replacement("world", "主世界");
});
var text = expansion.expand("[{world}] {time}");
```

未预填的 `replacement(name)` 仍读取 `expand` 参数数组中与注册序号相同的位置；混合两种注册方式时，预填项也占用序号，不能假定未预填项会重新从参数 0 开始。

`SharedObjects.WG_REGION_DATA` 已移除。WorldGuard 附加数据使用 `WGPlotInfoService`，领地名称和出生点分别通过 `WGCustomNameAPI`、`WGSpawnAPI` 访问；可检索 ID 格式为 `领地ID@世界名`，数据库 ID 格式为 `世界名::领地ID`。
