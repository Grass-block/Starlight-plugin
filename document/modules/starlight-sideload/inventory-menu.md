# 物品菜单 <Badge>starlight:inventory-menu</Badge>

可配置的 GUI 物品菜单系统

## 基本信息

- 命名空间id: `starlight:inventory-menu`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

通过 YAML 配置文件驱动的高度可自定义图形界面菜单系统。支持自定义图标、名称、描述、点击事件（左键/右键/任意）以及音效、指令执行、关闭、连接服务器等操作行为。菜单定义支持组件复用、权限控制与动态注册命令打开入口。

## 可配置项目

无独立配置项。菜单和组件通过 `inventory-ui/` 与 `inventory-ui-component/` 目录下的 YAML 配置定义。

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/gui open <id>` | `starlight.gui.admin` | 打开指定 ID 的菜单 |
| `/gui reload` | `starlight.gui.admin` | 重新加载所有菜单配置 |
