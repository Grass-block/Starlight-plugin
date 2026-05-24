# 简易权限控制 <Badge>starlight-security:permission-manager</Badge>

基于数据库的轻量权限管理系统，支持权限组、权限标签、单玩家权限覆写。

## 基本信息

- 命名空间id: `starlight-security:permission-manager`
- 版本: `1.0.3`
- 是否为内部模块: `否`
- 是否默认开启: `否`
- 是否为未完成[beta]阶段: `否`

## 描述

通过 JDBC 数据库存储玩家权限数据，支持：

- **权限组**：通过 `permission-groups.yml` 定义不同组的权限列表，默认为 `--player` 组
- **权限标签**：通过 `permission-tags.yml` 定义标签对应的权限集合，可附加到玩家身上
- **单玩家权限**：直接在玩家上设置允许/拒绝的权限节点，覆盖组和标签中的定义

玩家数据存储在 `SL_PERMISSION` 表中，包含所属组、标签列表、允许权限列表、拒绝权限列表。

## 可配置项目

| id              | 描述        | 可接受的输入值 |
|-----------------|-----------|---------|
| `default-group` | 新玩家的默认权限组 | 字符串（组名） |

## 外部配置

| 文件                      | 描述                                               |
|-------------------------|--------------------------------------------------|
| `permission-tags.yml`   | 定义权限标签，key 为标签名，value 为权限列表                      |
| `permission-groups.yml` | 定义权限组，key 为组名，value 包含 `permissions` 和 `tags` 字段 |

## 命令

| 命令                                             | 权限                         | 描述         |
|------------------------------------------------|----------------------------|------------|
| `/permission set <玩家> <权限节点> true/false/unset` | `quark.permission.command` | 设置/取消单玩家权限 |
| `/permission add-tag <玩家> <标签名>`               | `quark.permission.command` | 为玩家添加权限标签  |
| `/permission remove-tag <玩家> <标签名>`            | `quark.permission.command` | 移除玩家的权限标签  |
| `/permission group <玩家> <组名>`                  | `quark.permission.command` | 修改玩家的权限组   |
