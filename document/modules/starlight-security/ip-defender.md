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
- **属地查询结果未变化**：不做处理
- **属地查询结果变化**：向玩家发送警告、广播 `ip:change` 消息、可选记录到数据库和/或自动封禁

比较的是英文属地查询结果的哈希，不是原始 IP 地址；同属地的不同 IP 不一定触发变化提示。`real-ip` 提供器当前返回空字符串，尚无完整结果解析实现。

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

模板字段位于 `config/values/starlight-security.yml` 的 `config.ip-defender`。当前实现的封禁分支读取 `auto_ban`、`auto_ban_day_time`、`auto_ban_hour_time`、`auto_ban_minute_time`、`auto_ban_second_time`（下划线），与模板中的连字符字段不一致；不能依赖仅设置模板的 `auto-ban` 就启用封禁。

## 命令

| 命令          | 权限                         | 描述          |
|-------------|----------------------------|-------------|
| `/check-ip` | `+starlight.ip.query`（仅玩家） | 查询自己的 IP 属地 |
