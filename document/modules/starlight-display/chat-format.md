# 聊天行格式化 <Badge>starlight-display:chat-format</Badge>

重新格式化聊天消息的显示样式，支持世界观名、时间戳、颜色代码等元素组合。

## 基本信息

- 命名空间id: `starlight-display:chat-format`
- 版本: `1.2.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

通过监听`AsyncChatEvent`/`AsyncPlayerChatEvent`事件，使用可配置模板对聊天消息进行重排版。支持按世界区分格式、显示发送时间、自定义消息前后缀，并集成PlaceHolderService进行变量占位符替换。

## 可配置项目

| 配置项 | 类型 | 说明 |
|--------|------|------|
| `world` | String | 世界观名格式模板（含占位符） |
| `time` | String | 消息时间格式模板 |
| `template` | String | 聊天行整体格式模板 |

## 命令

无独立命令。
