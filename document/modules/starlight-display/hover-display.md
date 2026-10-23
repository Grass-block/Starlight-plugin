# 悬浮文本 <Badge>starlight-display:hover-display</Badge>

在指定位置创建悬浮显示的文本或全息文字。

## 基本信息

- 命名空间id: `starlight-display:hover-display`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

允许管理员在游戏世界中创建持久化的悬浮文本实体（基于盔甲架），支持自定义文本内容和位置。每行使用一个无重力、不可见的标记盔甲架，文本在创建或编辑时构建，并通过本地 JDBC 数据表 `sl_hover_display` 保存。提供
`/hover-display`命令进行创建、删除、移动和管理操作。

## 可配置项目

无独立配置项。当前代码不读取 `range` 或 `line-space`；行间距固定为 `0.244` 格，创建和移动的锚点为玩家位置上方 `1.37` 格。不要依赖持续动态占位符刷新。

## 命令

| 命令                                    | 权限                        | 说明                             |
|---------------------------------------|---------------------------|--------------------------------|
| `/hover-display create <name> <text>` | `-starlight.hoverdisplay` | 在当前位置创建悬浮文本，多行以 `{#return}` 分隔 |
| `/hover-display delete <name>`        | `-starlight.hoverdisplay` | 删除指定悬浮文本                       |
| `/hover-display delete-all`           | `-starlight.hoverdisplay` | 清理当前世界中带本模块标记的盔甲架，不删除数据库记录，后续可能重新生成 |
| `/hover-display edit <name> <text>`   | `-starlight.hoverdisplay` | 编辑指定悬浮文本内容                     |
| `/hover-display tp <name>`            | `-starlight.hoverdisplay` | 将指定悬浮文本移动到当前位置                 |
