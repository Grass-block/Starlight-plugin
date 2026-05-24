# 飞行控制指令 <Badge>starlight-utilities:fly-command</Badge>

提供飞行模式切换与飞行速度调整指令。

## 基本信息

- 命名空间id: `starlight-utilities:fly-command`
- 版本: `1.2.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块提供两个指令：`/fly` 切换飞行模式开关，`/flyspeed` 调整飞行速度（0.0~1.0）。支持重置速度为默认值。模块启用时自动注册聊天提示功能，方便玩家了解可用指令。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/fly` | `-quark.fly.toggle` | 切换飞行模式 |
| `/flyspeed <速度\|reset>` | `+quark.fly.flyspeed` | 设置或重置飞行速度（0.0~1.0） |
