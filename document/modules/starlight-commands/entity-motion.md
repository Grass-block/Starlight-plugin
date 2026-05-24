# 实体动量指令 <Badge>starlight-commands:entity-motion</Badge>

为实体设置运动动量。

## 基本信息

- 命名空间id: `starlight-commands:entity-motion`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

允许玩家为选中的实体设置或增加动量（速度向量）。支持两种模式：`set`（设置为指定速度）和`add`（在原速度基础上增加）。运动终止条件支持两种模式：`time`（持续指定刻数后停止）和`direction`（沿指定轴向移动指定距离后停止）。可用于模拟抛射、击飞、传送带等效果。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/motion <实体选择器> <add\|set> <time\|direction> <参数...> <x> <y> <z>` | `starlight.command.motion` | 为选中实体设置/增加动量 |
