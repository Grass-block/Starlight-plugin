# TAB栏提示信息 <Badge>starlight-display:tab-menu</Badge>

自定义玩家Tab列表（玩家列表界面）的头部和底部显示内容。

## 基本信息

- 命名空间id: `starlight-display:tab-menu`
- 版本: `2.0.3`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

通过ProtocolLib发送数据包，在玩家Tab列表的头部（header）和底部（footer）显示自定义信息。支持占位符变量（TPS、MSPT、Ping、在线人数、日期等）、颜色代码和多行文本。开启`render-ping`后实时渲染玩家延迟信息。

## 可配置项目

| 配置项 | 类型 | 说明 |
|--------|------|------|
| `render-ping` | Boolean | 是否渲染显示Ping值 |
| `header-ui` | List\<String\> | Tab列表头部显示内容 |
| `footer-ui` | List\<String\> | Tab列表底部显示内容 |

## 命令

无独立命令。
