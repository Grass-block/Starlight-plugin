# 集群Ping指标修正 <Badge>starlight-proxy:proxy-ping</Badge>

在集群环境下同步并修正各子服务器的玩家 Ping 值指标。

## 基本信息

- 命名空间id: `starlight-proxy:proxy-ping`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `否`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块在 Bungeecord 集群环境中通过远程消息服务同步代理端获取的玩家 Ping 值。子服务器上的占位符系统可据此获取真实的客户端 Ping 值，而非子服务器到代理端的延迟。支持配置同步间隔和查询目标。

## 可配置项目

| 配置路径 | 类型 | 默认值 | 描述 |
|----------|------|--------|------|
| `config.proxy-ping.interval` | integer | `200` | Ping 值同步间隔(ms) |
| `config.proxy-ping.query-target` | string | `proxy` | Ping 查询目标标识 |
