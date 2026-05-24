# 自定义计分板提示信息 <Badge>starlight-display:custom-scoreboard</Badge>

自定义玩家右侧计分板（Scoreboard）显示内容。

## 基本信息

- 命名空间id: `starlight-display:custom-scoreboard`
- 版本: `0.2`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

通过PlayerUIService向玩家渲染自定义计分板UI，支持多行文本、占位符变量（日期、时间、在线玩家、Ping、TPS等）和国际化。玩家可通过设置界面切换计分板显示开关。

## 可配置项目

| 配置项 | 类型 | 说明 |
|--------|------|------|
| `ui` | List\<String\> | 计分板各行显示文本模板 |

ui默认值：
- ` `, `{msg#date}`, `{msg#time}`, ` `, `{msg#player}`, `{msg#rank}`, `{msg#play-time}`, `{msg#world-time}`, ` `, `{msg#player-count}`, `{msg#ping}`, ` `, `{msg#qq-group}`, `{msg#website}`, ` `

## 命令

无独立命令。
