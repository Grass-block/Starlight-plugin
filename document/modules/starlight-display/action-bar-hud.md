# 快捷栏HUD信息显示 <Badge>starlight-display:action-bar-hud</Badge>

在玩家快捷栏上方（ActionBar）持续显示生物群系、坐标、时间等信息。

## 基本信息

- 命名空间id: `starlight-display:action-bar-hud`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

通过PlayerUIService向玩家ActionBar持续渲染HUD信息，支持生物群系名称、玩家坐标（XYZ）、当前游戏时间等动态数据。玩家可通过设置界面切换HUD显示开关，模板支持自定义排版和颜色代码。

## 可配置项目

| 配置项 | 类型 | 说明 |
|--------|------|------|
| `template` | String | ActionBar显示模板，支持`{biome}`、`{position}`、`{time}`等占位符 |

## 命令

无独立命令。
