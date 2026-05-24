# 玩家延迟查询 <Badge>starlight-utilities:player-ping-command</Badge>

查询玩家当前网络延迟。

## 基本信息

- 命名空间id: `starlight-utilities:player-ping-command`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块通过 Bukkit API 获取玩家的 ping 值并在聊天栏显示。同时注册占位符服务，供其他插件或聊天格式调用玩家延迟数据。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/ping` | 无 | 显示自身网络延迟 |
