# 聊天提及 <Badge>starlight-chat:chat-at</Badge>

在聊天中使用 `@玩家名` 或 `@all`，向目标玩家发送副标题和声音提醒。

## 基本信息

- 命名空间id: `starlight-chat:chat-at`
- 版本: `1.2`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

聊天按空格分词，独立的 `@玩家名` 匹配在线玩家，`@all` 匹配所有在线玩家。发送者不会收到自己的提及提醒。模块在启用、加入和退出时更新 `@玩家名` / `@all` 聊天补全；补全是否可用取决于服务端及客户端支持。

## 可配置项目

位于 `config/values/starlight-chat.yml` 的 `config.chat-at` 下。

| 配置项 | 默认值 | 说明 |
|----|----|----|
| `title-fadein` | `10` | 副标题淡入时间，单位 tick |
| `title-stay` | `20` | 副标题停留时间，单位 tick |
| `title-fadeout` | `10` | 副标题淡出时间，单位 tick |
| `at-template` | `{#green}@{player}{#reset}` | 聊天中的提及文本，`{player}` 为目标名 |
| `at-title-template` | `{#green}{player}: {#reset}{message}` | 副标题，`{player}` 为发送者，`{message}` 为不含提及词的正文 |
| `sound` | `true` | 是否播放经验球拾取音效 |

## 命令

| 命令 | 权限节点 | 说明 |
|----|----|----|
| `/at <玩家名或all> <消息...>` | `starlight.chat.at` | 以玩家身份发送带提及的聊天消息 |
