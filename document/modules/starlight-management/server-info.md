# 服务器信息指令 <Badge>starlight-management:server-info</Badge>

以格式化文本展示服务器运行状态，包括玩家数、TPS、MSPT、内存和世界信息。

## 基本信息

- 命名空间id: `starlight-management:server-info`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

ServerInfo 模块通过 `/system` 命令向玩家展示详细的服务器运行状态面板，包括在线玩家数、TPS/MSPT、JVM内存使用情况（已用/最大），以及每个世界的区块数、实体数、TileEntity 数等详细信息。世界名根据所处维度（主世界/地狱/末地）显示不同颜色。

## 可配置项目

无配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/system` | `quark.management.system` | 显示服务器详细信息面板 |
