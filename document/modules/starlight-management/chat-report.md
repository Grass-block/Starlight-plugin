# 聊天举报 <Badge>starlight-management:chat-report</Badge>

允许玩家举报不当聊天内容，支持与 ChatFilter 联动自动处理。

## 基本信息

- 命名空间id: `starlight-management:chat-report`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

ChatReport 模块在每条聊天消息旁附加可点击的举报按钮，其他玩家点击后即可举报该消息。举报信息会记录操作ID、举报者、发送者、时间和内容。被举报的消息如果命中 ChatFilter 的过滤规则，将触发预设的警告或惩罚。此模块仅支持 Paper 服务端。

## 可配置项目

| 配置项 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| `chat-report.append` | String | `{click(command,/chat-report {});color(red)}[!]` | 聊天消息旁附加的举报交互文本模板 |

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/chat-report <uuid>` | 无 | 举报指定UUID的聊天消息 |
