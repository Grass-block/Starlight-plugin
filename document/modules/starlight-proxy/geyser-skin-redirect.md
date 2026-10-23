# Geyser皮肤重映射 <Badge>starlight-proxy:geyser-skin-redirect</Badge>

按玩家名前缀为基岩版玩家重定向同名 Java 玩家皮肤。

## 基本信息

- 命名空间id: `starlight-proxy:geyser-skin-redirect`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

模块在玩家加入时检查名字前缀，移除该前缀后取得同名离线 Java 玩家档案，将其纹理复制到目标玩家档案。需要服务端提供 `OfflinePlayer.getPlayerProfile` 等玩家档案 API。

管理员可通过 `/redirect-be-skin <在线玩家>` 手动重新应用该规则；命令只接受目标玩家，不能指定任意皮肤来源玩家。当前没有 `/geyser-skin-redirect` 命令。

## 可配置项目

| 配置路径                                 | 类型     | 默认值 | 描述             |
|--------------------------------------|--------|-----|----------------|
| `config.geyser-skin-redirect.prefix` | string | `.` | Geyser 玩家名前缀标识 |

## 命令

| 命令 | 权限节点 | 说明 |
|----|----|----|
| `/redirect-be-skin <在线玩家>` | `quark.be.redirectskin` | 为带配置前缀的玩家重新应用去掉前缀后的同名皮肤 |
