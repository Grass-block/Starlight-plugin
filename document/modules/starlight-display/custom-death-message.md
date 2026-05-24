# 自定义死亡信息格式 <Badge>starlight-display:custom-death-message</Badge>

为玩家的死亡消息添加自定义前缀和后缀，改变默认死亡提示样式。

## 基本信息

- 命名空间id: `starlight-display:custom-death-message`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

监听`PlayerDeathEvent`，在死亡消息前后追加可配置的前缀和后缀文本。支持颜色代码和TextBuilder格式化，默认前缀为灰色`[X]`标记，简洁标识玩家死亡事件。

## 可配置项目

| 配置项 | 类型 | 说明 |
|--------|------|------|
| `prefix` | String | 死亡消息前缀 |
| `suffix` | String | 死亡消息后缀 |

## 命令

无独立命令。
