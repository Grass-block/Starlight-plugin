# 维护模式 <Badge>starlight-management:maintenance</Badge>

开启维护模式阻止玩家登录，仅允许白名单内的管理员进入服务器。

## 基本信息

- 命名空间id: `starlight-management:maintenance`
- 版本: `2.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

Maintenance 模块允许管理员通过指令开启/关闭维护模式。进入维护模式后，非白名单玩家将被拒之门外（收到踢出提示），已在线的非管理员玩家也会被踢出。管理员可动态添加/移除允许进入的玩家UUID白名单。拥有 `starlight.maintenance.bypass` 权限的玩家加入服务器时自动加入白名单。

## 可配置项目

无配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/maintenance start` | `quark.maintenance.command` | 开启维护模式，踢出所有非白名单玩家 |
| `/maintenance end` | `quark.maintenance.command` | 关闭维护模式 |
| `/maintenance allow <player>` | `quark.maintenance.command` | 将指定玩家加入维护白名单 |
| `/maintenance disallow <uuid>` | `quark.maintenance.command` | 从维护白名单中移除指定UUID |
