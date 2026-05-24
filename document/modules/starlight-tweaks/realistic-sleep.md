# 真实睡眠 <Badge>starlight-tweaks:realistic-sleep</Badge>

更真实的睡眠机制——睡觉不再跳过夜晚，而是在床上休息并随时间恢复生命值。

## 基本信息

- 命名空间id: `starlight-tweaks:realistic-sleep`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `是`

## 描述

该模块改变了原版的睡眠机制：玩家在床上睡觉时不会跳过夜晚，而是持续恢复生命值（按配置的时间间隔恢复指定血量）。白天也可以躺床休息，但下床时需要执行 `/leave-bed` 命令。支持记分板跟踪玩家连续睡眠天数。不兼容 Arclight 和纯 Bukkit 服务端。

## 可配置项目

| 配置项 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| `scale-per-player` | `double` | `3.5` | 每个玩家对夜晚跳过进度的缩放系数 |
| `health-interval` | `int` | `20` | 生命恢复的间隔时间（单位：tick） |
| `health-amount` | `int` | `1` | 每次恢复的生命值点数 |

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/leave-bed` | `starlight.bed.leave` | 白天在床上休息时，使用该命令起身下床（别名：`/wakeup`, `/leave`） |
