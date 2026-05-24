# 玩家加入提示 <Badge>starlight-display:player-join-message</Badge>

玩家加入/离开服务器时广播自定义提示消息。

## 基本信息

- 命名空间id: `starlight-display:player-join-message`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

监听`PlayerJoinEvent`和`PlayerQuitEvent`事件，向服务器在线玩家广播自定义的加入/离开提示消息。支持代理端（proxy）消息转发、加入音效播放及音量调节。通过语言文件管理消息模板，实现多语言支持。

## 可配置项目

| 配置项 | 类型 | 说明 |
|--------|------|------|
| `proxy` | String | 代理端消息格式模板 |
| `sound` | Boolean | 玩家加入时是否播放音效 |
| `volume` | Double | 音效播放音量 |

## 命令

无独立命令。
