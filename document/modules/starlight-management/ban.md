# 封禁指令 <Badge>starlight-management:ban</Badge>

高级封禁指令，支持限时封禁、指定日期封禁和永久封禁，覆盖原版 `/ban` 命令。

## 基本信息

- 命名空间id: `starlight-management:ban`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

Ban 模块增强了原版 Minecraft 的 `/ban` 命令，提供三种封禁时长模式：`time`（从现在起经过指定年月日时分秒后解封）、`until`（在指定日期时间解封）、`forever`（永久封禁）。支持自定义封禁原因，可选择是否广播封禁信息到全服。此模块覆盖原版 `minecraft:ban` 命令。

## 可配置项目

| 配置项 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| `advanced-ban-command.broadcast` | Boolean | `true` | 是否向全服广播封禁信息 |

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/ban <player> <reason> <forever\|time\|until> [参数...]` | `minecraft.command.ban` | 封禁玩家。`forever`永久封禁；`time <y> <M> <d> <h> <m> <s>`相对时长；`until <y> <M> <d> <h> <m> <s>`指定解封时间 |
