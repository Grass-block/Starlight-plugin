# 领地认领 <Badge>starlight-worldguard:wg-claim</Badge>

允许玩家认领（claim）和放弃（unclaim）WorldGuard领地所有权。

## 基本信息

- 命名空间id: `starlight-worldguard:wg-claim`
- 版本: `26.5.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

通过`/plot claim`和`/plot unclaim`指令，让玩家可以认领当前所在位置的无人领地，或放弃对自己领地的所有权。认领时自动将玩家添加为领地的拥有者（owner），放弃时则移除。

认领前会校验领地是否已被他人认领、是否为`__global__`全局区域等条件。所有操作结果通过语言文件反馈。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/plot claim` | `+starlight.worldguard.claim` | 认领当前所在位置的无人领地 |
| `/plot unclaim` | `+starlight.worldguard.claim` | 放弃当前所在位置的领地所有权 |
