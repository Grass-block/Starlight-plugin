# 领地出生点与传送 <Badge>starlight-worldguard:wg-spawn-tp</Badge>

为 WorldGuard 领地设置出生点，并提供传送与开关命令。

## 基本信息

- 命名空间id: `starlight-worldguard:wg-spawn-tp`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

需要 WorldGuard 和 WorldEdit。模块在 `/plot` 下注册子命令，出生点和 `allow-tp` 状态存储于 `WGPlotInfoService` 的领地附加数据中。出生点必须在领地内，未设置或清除后无法传送。传送使用保存的三维坐标，不保存视角。

管理命令以玩家当前位置优先级最高的非全局领地为目标，并检查玩家 UUID 是否在 owners 中。因此执行设置、清除或切换时需要站在自己拥有的领地内。

`allow-tp` 默认 `true`。当前代码在关闭时仅对命中领地 members 检查的执行者拒绝传送；它并未实现通常意义上的“禁止所有访客传送”，不要将该开关作为严格访问控制。

## 可配置项目

无独立 YAML 配置项。出生点及传送开关通过命令管理。

## 命令

| 命令 | 权限节点 | 说明 |
|----|----|----|
| `/plot tp <领地ID@世界名>` | `starlight.worldguard.tp` | 传送到目标领地出生点，例如 `spawn@world` |
| `/plot set-spawn` | `starlight.worldguard.spawn` | 将当前位置设为当前领地出生点 |
| `/plot clear-spawn` | `starlight.worldguard.spawn` | 清除当前领地出生点 |
| `/plot allow-tp` | `starlight.worldguard.tp.manage` | 切换当前领地的 `allow-tp` 状态 |
