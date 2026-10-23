# 玩家头衔 <Badge>starlight-display:player-name-header</Badge>

在玩家名称前显示自定义头衔（Header），并支持下方名称显示。

## 基本信息

- 命名空间id: `starlight-display:player-name-header`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

修改玩家显示名、玩家列表名和自定义名称，并在 `VisualScoreboardService` 的名字标签中使用模板生成的前后缀。区分 OP 和普通玩家的默认头衔，管理员可用 `/header set` / `clear` 管理指定玩家头衔。自定义值存入共享玩家数据的 `name-header` 字段。

名称下方信息由可选的 `BelowNameColumns` 子组件提供，要求 Paper 的 `NumberFormat` API。当前兼容性检查读取的开关仍为旧路径 `quark-display:player-name-header:below-name-enable`；不能保证仅修改新模板里的同名字段就能启用该子组件。

## 可配置项目

| 配置项                 | 类型      | 说明         |
|---------------------|---------|------------|
| `template`          | String  | 玩家列表头衔模板   |
| `op-header`         | String  | OP玩家头衔文本   |
| `player-header`     | String  | 普通玩家头衔文本   |
| `below-name`        | String  | 名称下方显示内容   |
| `below-name-enable` | Boolean | 是否启用下方名称显示 |

## 命令

| 命令        | 权限                       | 说明     |
|-----------|--------------------------|--------|
| `/header set <玩家> <头衔>` | `-starlight.name.header` | 设置指定玩家的头衔；当前实现仅取一个参数作为头衔 |
| `/header clear <玩家>` | 同上 | 清除自定义头衔，恢复 OP / 普通玩家默认值 |
