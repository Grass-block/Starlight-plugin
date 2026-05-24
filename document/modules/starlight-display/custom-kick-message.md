# 自定义踢出信息 <Badge>starlight-display:custom-kick-message</Badge>

自定义玩家被踢出或被封禁时显示的提示信息界面。

## 基本信息

- 命名空间id: `starlight-display:custom-kick-message`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

监听`KickMessageFetchEvent`和`BanMessageFetchEvent`事件，拦截玩家踢出和封禁界面消息，替换为可配置的多行UI模板。支持区分普通踢出和封禁两种场景分别配置显示内容，包含原因、操作者、过期时间、服务器网站提示等信息。

## 可配置项目

| 配置项 | 类型 | 说明 |
|--------|------|------|
| `ui` | List\<String\> | 踢出消息UI模板 |
| `ban-ui` | List\<String\> | 封禁消息UI模板 |

## 命令

无独立命令。
