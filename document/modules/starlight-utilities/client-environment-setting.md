# 客户端环境设置 <Badge>starlight-utilities:client-environment-setting</Badge>

为玩家单独设置客户端侧的天气与时间。

## 基本信息

- 命名空间id: `starlight-utilities:client-environment-setting`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块允许玩家独立修改自身客户端的天气（晴天/雨天/重置）与时间（偏移/固定/重置），不影响其他玩家。时间管理支持两种模式：通过 ProtocolLib 拦截时间数据包实现更精确的控制，或回退到 Bukkit API 原生接口。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/local-weather <none\|rain\|off>` | `+starlight.client.weather` | 设置本地天气（none=晴天/rain=雨天/off=重置） |
| `/local-time <offset\|fixed\|off> [值]` | `+starlight.client.time` | 设置本地时间（offset=偏移/fixed=固定值/off=重置） |
