# 位置对齐指令 <Badge>starlight-utilities:position-align</Badge>

将玩家对齐到最近的方块中心位置，并在重生时自动对齐。

## 基本信息

- 命名空间id: `starlight-utilities:position-align`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块提供 `/align` 指令将玩家精确对齐到所在方块的中心点（x.5, y, z.5），同时保留原有朝向或自动计算面向方向。可配置在玩家重生时自动执行对齐，避免出生位置偏移。

## 可配置项目

| 配置路径 | 类型 | 默认值 | 描述 |
|---------|------|--------|------|
| `config.position-align.fix-spawn-position` | boolean | `true` | 玩家重生时是否自动对齐位置 |

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/align` | `+starlight.command.align` | 将自身对齐到方块中心 |
