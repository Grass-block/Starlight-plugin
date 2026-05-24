# 传送请求 <Badge>starlight:tpa</Badge>

玩家间传送请求系统

## 基本信息

- 命名空间id: `starlight:tpa`
- 版本: `1.0.2`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

允许玩家向其他玩家发送传送请求，对方可选择接受或拒绝。提供 `/tpa`（请求目标传送至对方位置）和 `/tpahere`（请求对方传送至自己位置）两种模式。当玩家退出时自动清除与其相关的请求。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/tpa request <player>` | `quark.tpa` | 请求传送到目标玩家位置 |
| `/tpa accept <player>` | `quark.tpa` | 接受目标玩家的传送请求 |
| `/tpa deny <player>` | `quark.tpa` | 拒绝目标玩家的传送请求 |
| `/tpahere request <player>` | `quark.tpahere` | 请求目标玩家传送到自己位置 |
| `/tpahere accept <player>` | `quark.tpahere` | 接受对方的 tpahere 请求 |
| `/tpahere deny <player>` | `quark.tpahere` | 拒绝对方的 tpahere 请求 |
