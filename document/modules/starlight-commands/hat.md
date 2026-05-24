# 戴帽子 <Badge>starlight-commands:hat</Badge>

将手持物品戴到头盔栏。

## 基本信息

- 命名空间id: `starlight-commands:hat`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

允许玩家将手中的物品放置到头盔槽位作为帽子佩戴。如果目标玩家头盔槽位不为空则操作失败。拥有`starlight.hat.other`权限的玩家可以指定其他玩家来佩戴帽子。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/hat [玩家]` | `starlight.hat`(自身) / `starlight.hat.other`(他人) | 将手持物品戴到头盔栏 |
