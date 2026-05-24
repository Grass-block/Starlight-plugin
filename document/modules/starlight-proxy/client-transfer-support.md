# 客户端跳转mod支持 <Badge>starlight-proxy:client-transfer-support</Badge>

通过 Plugin Messaging 支持客户端 Mod 的跨服跳转功能。

## 基本信息

- 命名空间id: `starlight-proxy:client-transfer-support`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块通过 `client_transfer:main` 通道与安装了对应客户端 Mod 的玩家通信，支持玩家在服务器之间跳转。模块记录玩家的原始服务器信息，并在玩家加入时检查来源服务器。管理可使用 `/connect` 命令将指定玩家转移到其他服务器节点。

## 可配置项目

无独立配置项。
