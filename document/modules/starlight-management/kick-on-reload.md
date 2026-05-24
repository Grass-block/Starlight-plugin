# 重载自动踢出 <Badge>starlight-management:kick-on-reload</Badge>

检测到 `reload` 或 `stop` 命令时自动踢出所有在线玩家，防止数据丢失。

## 基本信息

- 命名空间id: `starlight-management:kick-on-reload`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

KickOnReload 模块监听玩家和服务器控制台的命令执行，当检测到 `reload` 或 `stop` 命令时自动将全部在线玩家踢出服务器并给出提示。可配置是否忽略OP玩家（OP不踢出），避免管理员在重载时被误踢。

## 可配置项目

| 配置项 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| `kick-on-reload.op-ignore` | Boolean | `false` | 是否不踢出OP玩家 |

## 命令

本模块无独立命令。
