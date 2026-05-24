# 物品防丢 <Badge>starlight-tweaks:item-drop-secure</Badge>

保护贵重物品不被误丢——丢弃指定物品时会被拦截并提示。

## 基本信息

- 命名空间id: `starlight-tweaks:item-drop-secure`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块阻止玩家丢弃配置中指定的贵重物品。当玩家尝试丢弃钻石工具/装备、下界合金制品、鞘翅、金质物品等高价值物品时，丢弃事件将被取消并发送提示消息。如果背包已满，则允许丢弃。玩家可以通过 `/unlock-drop` 命令临时解除丢弃限制（持续一段时间，时长由配置控制）。

## 可配置项目

| 配置项 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| `unlock-time` | `int` | `120` | 临时解锁丢弃的时间（单位：tick） |
| `list` | `str[]` | 见下方 | 受保护的物品 ID 前缀/关键字列表 |

默认保护列表：`diamond_`, `netherite_`, `nether_star`, `iron_boots`, `iron_leggings`, `iron_chestplate`, `iron_helmet`, `iron_pickaxe`, `elytra`, `iron_axe`, `iron_shovel`, `iron_sword`, `iron_hoe`, `golden_`, `gold_`

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/unlock-drop` | `quark.dropunlock` | 临时解锁物品丢弃限制，持续 `unlock-time` tick 后自动恢复 |
