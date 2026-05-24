# 聊天禁言 <Badge>starlight-management:mute</Badge>

对玩家进行聊天禁言，支持限时禁言和永久禁言，自动拦截聊天和特定命令。

## 基本信息

- 命名空间id: `starlight-management:mute`
- 版本: `1.0.2`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

Mute 模块提供完整的聊天禁言功能。管理员可通过 `/mute` 命令对玩家进行限时（秒）或永久禁言，被禁言玩家的聊天消息及包含 `say`/`tell` 的命令将被拦截并提示剩余时间。支持通过 `/unmute` 提前解除禁言。禁言数据持久化存储，重启服务器后依然有效。

## 可配置项目

无额外配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/mute <player> <time\|forever> <reason>` | OP | 禁言指定玩家，`time`为禁言秒数，`forever`为永久禁言 |
| `/unmute <player>` | `quark.unmute` | 解除指定玩家的禁言状态 |
