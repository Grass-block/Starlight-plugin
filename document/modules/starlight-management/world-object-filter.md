# 世界对象过滤 <Badge>starlight-management:world-object-filter</Badge>

按玩家偏好隐藏旗帜图案或告示牌文字，并可禁止放置部分旗帜。

## 基本信息

- 命名空间id: `starlight-management:world-object-filter`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `否`
- 是否为未完成[beta]阶段: `否`

## 描述

需要 ProtocolLib。模块处理区块数据和方块实体更新数据包，按接收玩家的设置清空指定旗帜的图案或告示牌文本，不修改服务器保存的原始内容。当前旗帜处理范围为红色、白色、蓝色旗帜及其墙上变体，并非所有颜色。

玩家偏好存入共享玩家数据：`world-filter:flags` 默认 `true`，`world-filter:signs` 默认 `false`。偏好仅在模块启用后生效。玩家就绪时会收到状态面板；修改偏好后，以后续发送的数据包为准，不会主动刷新所有已加载方块。

## 可配置项目

位于 `config/values/starlight-management.yml` 的 `config.world-object-filter` 下。

| 配置项 | 类型 | 默认值 / 说明 |
|----|----|----|
| `ban-flags` | Boolean | `false`，开启后取消红、白、蓝色立式旗帜的放置事件 |
| `notify-gui` | List&lt;String&gt; | 加入时的聊天状态面板模板，包含旗帜和告示牌过滤状态 |

## 命令

| 命令 | 权限节点 | 说明 |
|----|----|----|
| `/world-filter flags` | `starlight.management.worldfilter` | 切换自己的旗帜图案过滤 |
| `/world-filter signs` | `starlight.management.worldfilter` | 切换自己的告示牌文字过滤 |

命令需要玩家执行。
