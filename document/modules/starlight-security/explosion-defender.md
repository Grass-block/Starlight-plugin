# 爆炸防护 <Badge>starlight-security:explosion-defender</Badge>

监听服务器中的爆炸事件，通过白名单区域机制控制爆炸是否对方块造成破坏，并支持爆炸记录与广播。

## 基本信息

- 命名空间id: `starlight-security:explosion-defender`
- 版本: `1.4.3`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

ExplosionDefender（爆炸防护）拦截所有实体爆炸（`EntityExplodeEvent`）和方块爆炸（`BlockExplodeEvent`，需要 Bukkit 1.17+
API），根据爆炸位置是否命中白名单区域来决定处理方式。

对于不在白名单内的爆炸：

- **smart-cancel** 开启时，仅清除爆炸方块列表（保留爆炸事件本身，不产生破坏但保留声音/粒子效果）
- **smart-cancel** 关闭时，完全取消爆炸事件
- **override-explosion** 开启时，在取消原爆炸后创建一次 4 强度的人造爆炸（仅用于视觉效果）
- 根据配置决定是否向 op 广播爆炸信息和/或记录到数据库

## 可配置项目

| id                   | 描述                          | 可接受的输入值         |
|----------------------|-----------------------------|-----------------|
| `smart-cancel`       | 为 `true` 时仅清除方块破坏列表而非完全取消事件 | `true`, `false` |
| `override-explosion` | 为 `true` 时在取消爆炸后创建一次视觉性爆炸   | `true`, `false` |
| `record`             | 是否将爆炸事件记录到数据库               | `true`, `false` |
| `broadcast`          | 是否向有权限的玩家广播爆炸信息             | `true`, `false` |

## 命令

| 命令                                                                 | 权限                          | 描述          |
|--------------------------------------------------------------------|-----------------------------|-------------|
| `/explosion-whitelist add <名称> <世界> <x1> <y1> <z1> <x2> <y2> <z2>` | `quark.explosion.whitelist` | 添加爆炸白名单区域   |
| `/explosion-whitelist remove <名称>`                                 | `quark.explosion.whitelist` | 移除爆炸白名单区域   |
| `/explosion-whitelist list`                                        | `quark.explosion.whitelist` | 列出所有爆炸白名单区域 |

## 内部组件

- `BlockExplosionListener` — 监听 `BlockExplodeEvent`（1.17+），处理非实体来源的方块爆炸。
