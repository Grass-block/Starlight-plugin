# 方块更新锁定 <Badge>starlight-utilities:block-update-locker</Badge>

锁定世界中的方块更新，暂停活塞、红石、物理、生长等机制。

## 基本信息

- 命名空间id: `starlight-utilities:block-update-locker`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块允许管理员通过指令锁定或解锁全局方块更新，锁定后所有活塞伸缩、红石变化、方块物理、方块生长、方块扩散、投掷器触发等事件均被取消，使世界处于静止状态。适用于建筑维护、服务器调试等场景。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/block-update-locker lock` | OP | 锁定方块更新 |
| `/block-update-locker unlock` | OP | 解锁方块更新 |
