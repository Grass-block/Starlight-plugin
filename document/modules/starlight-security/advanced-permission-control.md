# 额外权限控制 <Badge>starlight-security:advanced-permission-control</Badge>

提供更精细的玩家行为权限控制节点，覆盖聊天、交互、挖掘、与实体交互等基础行为。

## 基本信息

- 命名空间id: `starlight-security:advanced-permission-control`
- 版本: `1.1`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

通过注入独立的权限节点，该模块允许服主对玩家的聊天、左键/右键交互、方块破坏、实体交互四种基本行为进行独立的权限控制。

| 权限节点                                | 控制行为          |
|-------------------------------------|---------------|
| `+starlight.player.chat`            | 允许聊天          |
| `+starlight.player.interact`        | 允许交互（点击方块/空气） |
| `+starlight.player.break`           | 允许破坏方块        |
| `+starlight.player.interact.entity` | 允许与实体交互       |

玩家若无对应权限，对应事件将被取消并收到无权限提示。

## 可配置项目

无独立配置项。
