# 位置锁定指令 <Badge>starlight-utilities:position-lock</Badge>

锁定玩家位置，禁止移动。

## 基本信息

- 命名空间id: `starlight-utilities:position-lock`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块允许玩家通过指令锁定自身位置，锁定时 `PlayerMoveEvent` 被取消从而禁止移动。管理员可通过指令锁定/解锁其他在线玩家，并支持 Tab 补全玩家名称。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/lock-position [玩家]` | `+starlight.command.lock` | 锁定/解锁自身或指定玩家的位置（操作用户可锁定其他玩家） |
