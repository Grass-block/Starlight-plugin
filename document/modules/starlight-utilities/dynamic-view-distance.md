# 自定义渲染距离 <Badge>starlight-utilities:dynamic-view-distance</Badge>

动态管理每位玩家的服务端渲染距离。

## 基本信息

- 命名空间id: `starlight-utilities:dynamic-view-distance`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块通过流水线化的策略（玩家数量、自定义设置等）动态计算并设置每位玩家的渲染距离。支持通过指令为玩家单独设定渲染距离，并在玩家加入或退出时自动调整。适用于大服优化性能或为特定玩家提供更高渲染距离。

## 可配置项目

| 配置路径 | 类型 | 默认值 | 描述 |
|---------|------|--------|------|
| `config.dynamic-view-distance.max` | int | `32` | 最大渲染距离 |
| `config.dynamic-view-distance.calc-period` | int | `100000` | 渲染距离计算周期（刻） |

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/view-distance [值\|reset] [玩家]` | `+quark.viewdistance` | 查看/设置渲染距离（指定玩家需 `-quark.viewdistance.other`） |
