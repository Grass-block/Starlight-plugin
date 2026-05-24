# 环境方块刷新 <Badge>starlight-utilities:surrounding-refresh</Badge>

强制刷新玩家周围一定范围内的方块数据包，用于修复因网络延迟或区块更新异常导致的视觉不同步问题。

## 基本信息

- 命名空间id: `starlight-utilities:surrounding-refresh`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块允许玩家或管理员强制刷新指定周围一定半径内的方块变化，使客户端重新加载这些方块的状态。在爆炸事件发生后也可自动触发刷新，以确保范围内所有玩家正确看到爆炸后的地形变化。通过 `/refresh-area` 命令指定半径与目标玩家，快速修正视觉不同步问题。

## 可配置项目

| 配置路径 | 类型 | 默认值 | 描述 |
|---------|------|--------|------|
| `config.surrounding-refresh.update-on-explode` | boolean | `false` | 爆炸事件后是否自动刷新周围玩家方块 |

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/refresh-area <半径> [玩家] [silent]` | `+starlight.world.refresh` | 刷新指定玩家周围方块（OP可指定其他玩家） |
