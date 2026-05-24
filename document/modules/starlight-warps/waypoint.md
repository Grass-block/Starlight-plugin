# 导航点 <Badge>starlight:waypoint</Badge>

导航点与家系统

## 基本信息

- 命名空间id: `starlight:waypoint`
- 版本: `2.0.4`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

提供可持久化的导航点（waypoint）系统，支持玩家创建、命名、传送至自定义坐标点，并可设置访问权限（允许/禁止特定玩家）。内建家（home）系统，可通过 `/sethome` 设置家、`/home` 传送回家。

## 可配置项目

| 路径 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `config.waypoint.allow-coordinate-add` | boolean | `true` | 是否允许玩家通过坐标参数添加导航点 |
| `config.waypoint.home` | boolean | `true` | 是否启用家系统 |

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/waypoint add [name] [坐标...]` | `starlight.waypoint.command` | 添加导航点 |
| `/waypoint remove <name>` | `starlight.waypoint.command` | 删除导航点 |
| `/waypoint list` | `starlight.waypoint.command` | 列出所有导航点 |
| `/waypoint tp <name>` | `starlight.waypoint.command` | 传送至导航点 |
| `/waypoint allow <name> <player>` | `starlight.waypoint.command` | 允许其他玩家使用导航点 |
| `/waypoint disallow <name> <player>` | `starlight.waypoint.command` | 禁止其他玩家使用导航点 |
| `/waypoint add-public [name] [坐标...]` | `starlight.waypoint.command` + `starlight.waypoint.edit-public` | 添加公共导航点（所有玩家可用） |
| `/sethome` | `starlight.waypoint.sethome` | 设置家位置 |
| `/home` | `starlight.waypoint.home` | 传送回家 |
