# 控制台代理执行 <Badge>starlight-console:console-execute</Badge>

允许拥有权限的玩家代理控制台执行命令。

## 基本信息

- 命名空间id: `starlight-console:console-execute`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块为拥有 `-starlight.console.execute` 权限的玩家提供 `/console` 命令。玩家可使用此命令让服务端以控制台身份执行任意指令，便于管理员在游戏内进行服务端管理操作。执行结果会通过消息系统反馈给玩家。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/console <command>` | `-starlight.console.execute` | 以控制台身份执行指定命令 |
