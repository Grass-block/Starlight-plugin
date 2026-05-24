# 自定义欢迎信息 <Badge>starlight-display:welcome-message</Badge>

玩家首次进入服务器时显示自定义欢迎消息。

## 基本信息

- 命名空间id: `starlight-display:welcome-message`
- 版本: `0.1.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

监听`PlayerFirstJoinEvent`事件，在玩家首次加入服务器时延迟数刻发送自定义欢迎消息。支持多行UI排版、占位符（`{player}`）替换和国际化语言支持。提供`/welcome-message`命令手动触发欢迎消息预览。

## 可配置项目

| 配置项 | 类型 | 说明 |
|--------|------|------|
| `ui` | List\<String\> | 欢迎消息UI模板 |

ui默认值：
- `{#yellow}一一一一一一一一一一一一一一一一一一一一一一一一一一一`
- `{msg#title}`
- `{msg#content}`
- ` `
- `{msg#callback}`
- `{#yellow}一一一一一一一一一一一一一一一一一一一一一一一一一一一`

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/welcome-message` | `-` | 手动发送欢迎消息 |
