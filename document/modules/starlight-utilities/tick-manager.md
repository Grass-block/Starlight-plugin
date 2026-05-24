# 服务器刻控制 <Badge>starlight-utilities:tick-manager</Badge>

控制服务端游戏刻的冻结与步进，用于调试或运维场景。

## 基本信息

- 命名空间id: `starlight-utilities:tick-manager`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块利用 Bukkit 的 `ServerTickManager` API 实现服务器刻的冻结与单步执行。管理员可以通过指令将服务器刻冻结，使所有游戏逻辑暂停，或按需步进指定数量的刻数，便于在特殊维护场景下控制游戏运行节奏。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/server-tick freeze` | `-quark.tick` | 冻结服务器刻 |
| `/server-tick unfreeze` | `-quark.tick` | 解冻服务器刻 |
| `/server-tick step <数量>` | `-quark.tick` | 步进指定数量的游戏刻 |
