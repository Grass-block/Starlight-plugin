# 集群聊天同步 <Badge>starlight-proxy:chat-sync</Badge>

在 Bungeecord 集群环境下跨子服务器同步玩家聊天消息。

## 基本信息

- 命名空间id: `starlight-proxy:chat-sync`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块通过 Plugin Messaging Channel 在 Bungeecord 集群的各个子服务器之间广播和接收玩家聊天消息。支持 Bukkit 原生 `AsyncPlayerChatEvent` 和 Paper 的 `AsyncChatEvent`，消息通过 `quark_plugin:msg` 和 `starlight:msg` 通道传输。可配置聊天消息的显示格式。

## 可配置项目

| 配置路径 | 类型 | 默认值 | 描述 |
|----------|------|--------|------|
| `config.chat-sync.format` | string | `"{}{#gold} \| {#white} {} {#gold}> {;}{#white}{}"` | 聊天消息显示格式 |
