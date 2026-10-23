# 聊天组件 <Badge>starlight-chat:chat-component</Badge>

处理聊天、告示牌和铁砧中的颜色及文本组件。

## 基本信息

- 命名空间id: `starlight-chat:chat-component`
- 版本: `1.3.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

聊天通过 `ForwardedChatEvent` 安装文本构建器，依次处理颜色字符、全局占位符、玩家占位符和间接字符，再构建消息组件。聊天格式布局由 [chat-format](../starlight-display/chat-format.md) 控制。

模块还提供告示牌文本和铁砧重命名处理，相关子组件按服务端 API 选择兼容实现。26.6.4 的聊天处理统一接入转发事件，原 `PaperChatListener` 已改为负责铁砧的 `PaperAnvilListener`。

## 可配置项目

位于 `config/values/starlight-chat.yml` 的 `config.chat-component` 下。

| 配置项 | 类型 | 默认值 | 说明 |
|----|----|----|----|
| `parse-command` | Boolean | `false` | 是否对玩家命令预处理文本组件；默认关闭 |

## 命令

本模块无独立命令。
