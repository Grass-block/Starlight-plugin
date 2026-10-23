# 屏蔽词 <Badge>starlight-chat:chat-filter</Badge>

过滤玩家聊天、指定命令和告示牌中的敏感词，支持举报联动惩罚。

## 基本信息

- 命名空间id: `starlight-chat:chat-filter`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

ChatFilter 模块使用 Aho-Corasick 算法和正则表达式对玩家消息进行敏感词匹配与替换。支持过滤聊天消息、特定命令（如`/say`、
`/tell`、`/mail`）和告示牌文本。可配置是否遮盖敏感词、排除玩家名、是否在举报命中后惩罚违规玩家。与 ChatReport
联动，被举报且命中过滤的玩家将自动执行惩罚命令。

## 可配置项目

| 配置项                            | 类型      | 默认值                      | 描述                        |
|--------------------------------|---------|--------------------------|---------------------------|
| `chat-filter.filter-sign`      | Boolean | `true`                   | 是否过滤告示牌内容                 |
| `chat-filter.filter-anvil`     | Boolean | `true`                   | 模板保留项；当前 ChatFilter 没有铁砧事件处理器，此开关不生效 |
| `chat-filter.except-player`    | Boolean | `true`                   | 是否排除玩家名（不检测玩家名中的敏感词）      |
| `chat-filter.cover`            | Boolean | `true`                   | 是否用替代字符遮盖敏感词，关闭则仅检测不替换    |
| `chat-filter.cover-char`       | String  | `*`                      | 用于遮盖敏感词的字符                |
| `chat-filter.handled-commands` | List    | `[say, tell, mail]`      | 需要过滤内容的命令列表               |
| `chat-filter.punish`           | Boolean | `true`                   | 是否对违规玩家执行惩罚               |
| `chat-filter.punish-command`   | String  | `mute {player} 3600 ...` | 惩罚命令模板，`{player}`将被替换为玩家名 |

以上字段位于 `config/values/starlight-chat.yml` 的 `config` 下。词库来自插件数据目录的 `config/chat-filter-rules/`，支持分组 JSON 规则和 TXT 关键词文件；各组 `template.*` 是参考模板，不作为实际规则读取。

当前聊天过滤仍监听 `AsyncPlayerChatEvent`，并非改为监听 `ForwardedChatEvent`。惩罚由 `ChatReportedEvent` 联动触发，不是每次关键词匹配就立即执行。

## 命令

本模块无独立命令。
