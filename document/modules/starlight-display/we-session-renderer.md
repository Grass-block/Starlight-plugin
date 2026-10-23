# WorldEdit选区渲染器 <Badge>starlight-display:we-session-renderer</Badge>

在游戏世界中可视化渲染WorldEdit的选区范围。

## 基本信息

- 命名空间id: `starlight-display:we-session-renderer`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

依赖WorldEdit插件，监听选区选择事件，通过粒子效果或可视边框在三维空间中渲染玩家的WorldEdit选区范围。支持实时更新模式和持久显示模式，玩家可通过
`/we-selection`命令切换渲染模式和显示开关。选区渲染数据持久化存储。

## 可配置项目

无独立配置项，当前不读取 `particle` / `interval`。持久模式每 10 tick 重绘，更新模式在选区变化后短暂重绘。玩家模式存入共享玩家数据的 `we-session-render-mode` 字段，默认为 `UPDATE`。

## 命令

| 命令              | 权限  | 说明               |
|-----------------|-----|------------------|
| `/we-selection` 或 `/we-selection render` | 无独立权限声明 | 临时显示当前选区，不修改模式 |
| `/we-selection update` | 同上 | 选区变化时短暂显示（默认模式） |
| `/we-selection persistent` | 同上 | 持续显示选区 |
| `/we-selection off` | 同上 | 关闭自动显示 |
