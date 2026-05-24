# Geyser皮肤重映射 <Badge>starlight-proxy:geyser-skin-redirect</Badge>

为 Geyser 基岩版玩家手动重定向皮肤，解决基岩版玩家皮肤显示异常问题。

## 基本信息

- 命名空间id: `starlight-proxy:geyser-skin-redirect`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块允许管理员手动为 Geyser 连接的基岩版玩家设置皮肤重定向。通过 `/geyser-skin-redirect` 命令可将指定基岩版玩家的皮肤替换为
Java 版玩家的皮肤。模块在玩家加入时自动检测 Geyser 玩家，管理员可通过命令指定皮肤源玩家。

## 可配置项目

| 配置路径                                 | 类型     | 默认值 | 描述             |
|--------------------------------------|--------|-----|----------------|
| `config.geyser-skin-redirect.prefix` | string | `.` | Geyser 玩家名前缀标识 |
