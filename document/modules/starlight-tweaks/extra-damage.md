# 额外伤害 <Badge>starlight-tweaks:extra-damage</Badge>

为指定武器或伤害实体添加额外伤害与直接扣血效果。

## 基本信息

- 命名空间id: `starlight-tweaks:extra-damage`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `否`
- 是否为未完成[beta]阶段: `否`

## 描述

模块处理 `EntityDamageByEntityEvent`，要求目标是 `LivingEntity`，并要求服务端存在 `org.bukkit.damage.DamageSource` API。玩家直接造成伤害时，按主手物品的材质 ID 匹配；其他情况按直接伤害实体的类型 ID 匹配。例如箭矢应按箭实体 ID 配置，不能据此认定它会匹配射手手中的弓。

`extra-damage` 在 1 tick 后以 `GENERIC_KILL` 伤害源追加独立伤害，不会将此追加事件再次递归处理。`true-damage` 则立即修改目标生命值，当前实现的下限为 **5 点生命值**，因此不能依赖这一部分直接击杀目标；生命值低于 5 时该逻辑甚至可能将其回升到 5。原始伤害仍由原事件处理。

## 可配置项目

位于 `config/values/starlight-tweaks.yml` 的 `config.extra-damage` 下。两组映射的键均为完整物品 / 实体 ID，值为效果字符串列表。

```yaml
config:
  extra-damage:
    true-damage:
      "minecraft:diamond_sword": ["percent:1.5", "fixed:2"]
    extra-damage:
      "minecraft:arrow": ["append:50"]
```

| 效果 | 含义 | 示例 |
|----|----|----|
| `percent:N` | 目标最大生命值的 N% | `percent:1.5` = 最大生命值 × 0.015 |
| `append:N` | 当前事件基础伤害的 N%（`event.getDamage()`） | `append:150` = 基础伤害 × 1.5 |
| `fixed:N` | 固定 N 点生命值 | `fixed:2` = 2 点伤害 |

同一 ID 的效果分别执行，不是覆盖关系；不要依赖集合中的执行顺序。旧的 `percent-damage` 数值映射已不再使用，需要迁移为上述字符串列表。模板中 `append:500` 表示基础伤害的 500%，不是固定 500 点。

## 命令

本模块无独立命令。
