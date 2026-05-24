# Bungeecord集群保护 <Badge>starlight-proxy:legacy-forwarding-protect</Badge>

通过远程消息验证机制保护 Bungeecord 集群模式下子服务器的连接安全。

## 基本信息

- 命名空间id: `starlight-proxy:legacy-forwarding-protect`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `否`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块在子服务器端对 Bungeecord 集群中的玩家连接进行二次验证。当玩家通过 Bungeecord 转发连接时，模块会通过远程消息服务与代理端进行会话校验，确保连接来源可信。未通过验证的连接将被拒绝，有效防止非代理直连攻击。

## 可配置项目

| 配置路径 | 类型 | 默认值 | 描述 |
|----------|------|--------|------|
| `config.bungee-connection-protect.accept-delay` | integer | `350` | 接受连接前等待验证的延迟时间(ms) |
