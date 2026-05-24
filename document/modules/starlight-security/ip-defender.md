# IP 属地检测 <Badge>starlight-security:ip-defender</Badge>

在玩家加入时检测并记录其 IP 属地信息，支持 IP 变化预警、记录与自动封禁。

## 基本信息

- 命名空间id: `starlight-security:ip-defender`
- 版本: `1.3.4`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

玩家每次加入服务器时，通过第三方 IP 查询服务获取其 IP 属地信息，与上次记录进行对比：

- **首次加入**：仅展示 IP 属地信息
- **IP 未变化**：不做处理
- **IP 变化**：向玩家发送警告、广播 `ip:change` 消息、可选记录到数据库和/或自动封禁

## 可配置项目

| id                     | 描述               | 可接受的输入值                                  |
|------------------------|------------------|------------------------------------------|
| `service`              | IP 查询服务提供商       | `baidu`, `ip-api`, `pconline`, `real-ip` |
| `record`               | 是否将 IP 变化记录到数据库  | `true`, `false`                          |
| `auto-ban`             | 是否在 IP 变化时自动封禁玩家 | `true`, `false`                          |
| `auto-ban-day-time`    | 自动封禁天数           | 整数                                       |
| `auto-ban-hour-time`   | 自动封禁小时数          | 整数                                       |
| `auto-ban-minute-time` | 自动封禁分钟数          | 整数                                       |
| `auto-ban-second-time` | 自动封禁秒数           | 整数                                       |

## 命令

| 命令          | 权限                         | 描述          |
|-------------|----------------------------|-------------|
| `/check-ip` | `+starlight.ip.query`（仅玩家） | 查询自己的 IP 属地 |
