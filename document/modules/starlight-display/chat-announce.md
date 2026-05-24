# 聊天栏公告 <Badge>starlight-display:chat-announce</Badge>

在聊天栏定期轮播公告消息，并支持玩家加入时显示公告提示。

## 基本信息

- 命名空间id: `starlight-display:chat-announce`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

管理一组公告消息，定时在聊天栏轮播显示。玩家加入服务器时可以选择性弹出公告提示（tip）。支持多语言本地化公告和自定义公告内容。提供`/announce`命令用于公告的管理和操作。

## 可配置项目

| 配置项 | 类型 | 说明 |
|--------|------|------|
| `send-tip-on-join` | Boolean | 玩家加入时是否发送公告提示 |
| `template-tips` | List\<String\> | 公告提示UI模板 |
| `interval` | Integer | 公告轮播间隔（秒） |

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/announce` | `+starlight.display.announce` | 管理聊天栏公告 |
