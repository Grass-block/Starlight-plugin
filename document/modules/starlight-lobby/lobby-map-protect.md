# 地图保护 <Badge>starlight-lobby:lobby-map-protect</Badge>

保护大厅地图不被破坏——限制方块破坏、交互和实体伤害。

## 基本信息

- 命名空间id: `starlight-lobby:lobby-map-protect`
- 版本: `1.0.3`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块保护大厅世界中的方块与实体免遭玩家破坏。普通玩家无法破坏方块、攻击非玩家实体、交互方块（含耕地踩踏）以及与实体交互。创造模式玩家或拥有 `starlight.lobby.break` / `starlight.lobby.interact` 权限的玩家可绕过限制。

## 可配置项目

无独立配置项；通过权限控制：

| 权限节点 | 默认值 | 描述 |
|---------|--------|------|
| `starlight.lobby.break` | `false`（默认否定） | 允许破坏方块和攻击非玩家实体 |
| `starlight.lobby.interact` | `false`（默认否定） | 允许与方块和实体交互 |

## 命令

无。
