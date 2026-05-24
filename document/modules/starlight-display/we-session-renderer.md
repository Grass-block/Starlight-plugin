# WorldEdit选区渲染器 <Badge>starlight-display:we-session-renderer</Badge>

在游戏世界中可视化渲染WorldEdit的选区范围。

## 基本信息

- 命名空间id: `starlight-display:we-session-renderer`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

依赖WorldEdit插件，监听选区选择事件，通过粒子效果或可视边框在三维空间中渲染玩家的WorldEdit选区范围。支持实时更新模式和持久显示模式，玩家可通过`/we-selection`命令切换渲染模式和显示开关。选区渲染数据持久化存储。

## 可配置项目

| 配置项 | 类型 | 说明 |
|--------|------|------|
| `particle` | String | 渲染使用的粒子类型 |
| `interval` | Integer | 渲染更新间隔（tick） |

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/we-selection` | `-` | 管理选区渲染器（切换模式/开关） |
