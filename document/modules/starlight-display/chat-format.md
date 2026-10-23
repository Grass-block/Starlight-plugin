# 聊天行格式化 <Badge>starlight-display:chat-format</Badge>

重新格式化聊天消息的显示样式，支持世界名、时间戳、颜色代码和消息前后缀。

## 基本信息

- 命名空间id: `starlight-display:chat-format`
- 版本: `1.2.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

监听统一的 `ForwardedChatEvent`，使用模板对聊天消息进行重排版。Paper 环境由核心接入原生聊天渲染器，其他 Bukkit 环境由核心提供兼容转发。

`template` 中 `{0}` 为发送者显示名，`{1}` 为消息正文，`{world}` 为本地化世界名，`{time}` 为发送时间。世界名按 `common:world:<维度键>` 解析；不再提供独立的 `world` 格式项。

前后缀也支持 `{world}`、`{time}`，并经过发送者的玩家占位符处理。`prefix-near` / `postfix-near` 表示紧邻完整模板行的内侧前后缀，`prefix` / `postfix` 位于外侧；其他模块也可添加前后缀。

## 可配置项目

位于 `config/values/starlight-display.yml` 的 `config.chat-format` 下。

| 配置项 | 类型 | 默认值 / 说明 |
|----|----|----|
| `template` | String | `[{#white}{world}{#white}]{0} {color(gold)}> {color(white)}{1}` |
| `prefix` | String | 空字符串，外侧前缀 |
| `prefix-near` | String | 空字符串，内侧前缀 |
| `postfix` | String | 空字符串，外侧后缀 |
| `postfix-near` | String | `{#gray}{time}   `，内侧后缀 |

从旧配置升级时，将 `time` 字段中的显示文本迁入 `postfix-near` 等字段，并用 `{time}` 引用时间。旧 `time: "<post> ..."` 和 `world` 字段不再被此模块读取。

## 命令

无独立命令。
