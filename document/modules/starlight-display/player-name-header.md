# 玩家头衔 <Badge>starlight-display:player-name-header</Badge>

在玩家名称前显示自定义头衔（Header），并支持下方名称显示。

## 基本信息

- 命名空间id: `starlight-display:player-name-header`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

通过修改计分板`BELOW_NAME`和`PLAYER_LIST`显示内容，为玩家添加可配置的头衔前缀。区分OP（管理员）和普通玩家头衔，支持在玩家名称下方显示额外信息（如游戏时间、Ping）。提供`/header`命令让玩家自行设置头衔。

## 可配置项目

| 配置项 | 类型 | 说明 |
|--------|------|------|
| `template` | String | 玩家列表头衔模板 |
| `op-header` | String | OP玩家头衔文本 |
| `player-header` | String | 普通玩家头衔文本 |
| `below-name` | String | 名称下方显示内容 |
| `below-name-enable` | Boolean | 是否启用下方名称显示 |

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/header` | `-starlight.name.header` | 设置玩家头衔 |
