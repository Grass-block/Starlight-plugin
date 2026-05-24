# 自定义MOTD信息 <Badge>starlight-display:custom-motd</Badge>

自定义服务器列表MOTD显示信息，包括第一行/第二行文本、在线人数、玩家列表、服务器图标等。

## 基本信息

- 命名空间id: `starlight-display:custom-motd`
- 版本: `1.0.2`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

拦截服务器列表Ping事件并替换MOTD文本与图标显示。支持ProtocolLib协议发送自定义ServerPing，可配置多行MOTD、修改最大/在线玩家数量以及自定义服务器图标（favicon）。提供`/motd`命令手动刷新或重新加载MOTD配置。

## 可配置项目

| 配置项 | 类型 | 说明 |
|--------|------|------|
| `line1` | String | MOTD第一行文本 |
| `line2` | String | MOTD第二行文本 |
| `max-player` | Integer | 最大玩家数显示 |
| `online-player` | Boolean | 是否显示真实在线玩家数 |
| `icon` | String | 服务器图标文件路径 |

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/motd` | `-quark.motd.command` | 管理MOTD显示配置 |
