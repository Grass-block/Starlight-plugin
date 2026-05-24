# 菜单物品触发器 <Badge>starlight-utilities:menu-item</Badge>

为玩家提供一个可交互的菜单物品，用于快速执行绑定指令。

## 基本信息

- 命名空间id: `starlight-utilities:menu-item`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块在玩家背包中放置一个自定义物品（默认时钟），玩家手持该物品点击空气或方块时自动执行绑定的菜单指令。支持在玩家加入、重生、丢弃物品时自动检测并补发物品。同时提供 `/menu-item` 指令用于手动获取该物品。

## 可配置项目

| 配置路径 | 类型 | 默认值 | 描述 |
|---------|------|--------|------|
| `config.menu-item.item` | string | `minecraft:clock` | 菜单物品的材质 |
| `config.menu-item.bind-command` | string | `menu` | 点击物品时执行的指令 |
| `config.menu-item.detect-join` | boolean | `true` | 玩家加入时自动检测并补发物品 |
| `config.menu-item.detect-respawn` | boolean | `false` | 玩家重生时自动检测并补发物品 |
| `config.menu-item.detect-drop` | boolean | `true` | 玩家丢弃时自动检测并补发物品 |

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/menu-item` | `+starlight.menu.item` | 获取菜单物品 |
