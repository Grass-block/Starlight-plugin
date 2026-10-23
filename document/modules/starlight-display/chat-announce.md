# 聊天栏公告 <Badge>starlight-display:chat-announce</Badge>

在玩家就绪时或执行命令时，从提示池随机显示一条提示。

## 基本信息

- 命名空间id: `starlight-display:chat-announce`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

从已启用模块的语言条目（键以 `--tip` 结尾）和自定义提示文件收集内容。玩家就绪时可随机显示一条提示，也可通过 `/announce tips` 主动查看。支持多语言提示。

当前实现没有定时轮播任务；`create`、`view` 子命令为空实现，不能用来创建或管理公告。

## 可配置项目

| 配置项                | 类型             | 说明            |
|--------------------|----------------|---------------|
| `send-tip-on-join` | Boolean        | 玩家加入时是否发送公告提示 |
| `template-tips`    | List\<String\> | 公告提示UI模板      |

以上字段位于 `config/values/starlight-display.yml` 的 `config.chat-announce` 下。当前没有可用的 `interval` 字段。自定义提示位于 `config/tips.yml`，示例：

```yaml
tips:
  welcome:
    zh-cn: "欢迎来到服务器！"
    en-us: "Welcome to the server!"
```

自定义提示的语言键使用连字符，如 `zh-cn`；缺少目标语言时先回退到 `en-us`，再回退到 `zh-cn`。

## 命令

| 命令          | 权限                            | 说明      |
|-------------|-------------------------------|---------|
| `/announce tips` | `+starlight.display.announce` | 显示一条随机提示 |
| `/announce create` / `/announce view` | 同上 | 保留的子命令，当前无操作 |
