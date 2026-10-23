# 代理外信息同步 <Badge>starlight-proxy:out-proxy-info-sync</Badge>

通过 APM 远程消息同步聊天和玩家上下线信息。

## 基本信息

- 命名空间id: `starlight-proxy:out-proxy-info-sync`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `否`
- 是否为未完成[beta]阶段: `否`

## 描述

模块向 APM 网络广播本服玩家加入、离开和聊天消息，并接收远端聊天、加入、切服和离开消息。接收到的聊天发给本服玩家及控制台，远端玩家名和所在服务器保存在内存中。

使用前需配置插件的 APM 远程消息连接和对应服务器标识，并在参与同步的节点部署兼容的消息实现。Paper 和 Bukkit 聊天监听器按平台二选一。此模块与 [chat-sync](chat-sync.md) 是不同的同步途径，部署时应核对拓扑，避免同一条聊天重复转发。

## 可配置项目

位于 `config/values/starlight-proxy.yml` 的 `config.out-proxy-info-sync` 下。

| 配置项 | 类型 | 默认值 / 说明 |
|----|----|----|
| `chat-sync-template` | String | `{#gold}[{#reset}{}{#gold}]{#white}{}: `，参数依次为远端服务器标识、玩家名，消息组件追加在后面 |

## 命令

本模块无独立命令。
