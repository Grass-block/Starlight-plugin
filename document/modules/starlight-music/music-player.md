# 音乐播放器 <Badge>starlight-music:music-player</Badge>

提供全服统一的音乐播放功能，支持远程音乐获取、播放控制与图形化选歌界面。

## 基本信息

- 命名空间id: `starlight-music:music-player`
- 版本: `1.0.3`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

音乐播放器是 starlight-music 的核心模块，负责管理全局音乐会话。它通过 APM 远程消息服务实现跨服务器音乐同步，支持播放、暂停、取消以及 legacy 音色控制。模块内置了基于 InventoryUI 的图形选歌界面，玩家可通过 GUI 浏览并点播服务器中的音乐文件。

## 可配置项目

| 配置项 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| `mount` | `boolean` | `false` | 是否挂载全局音乐会话 |
| `ui` | `string` | `"{#white}{msg#playing} {#yellow}: {#aqua}{name} {#yellow}\|{#white} {time}{#gray} / {#white}{total}"` | 播放器 ActionBar UI 模板 |

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/music play <曲目>` | `starlight.music.play` | 播放指定音乐 |
| `/music pause` | `starlight.music.play` | 暂停当前播放 |
| `/music resume` | `starlight.music.play` | 恢复播放 |
| `/music cancel` | `starlight.music.play` | 停止播放 |
| `/music gui [页码]` | `starlight.music.play` | 打开音乐选择 GUI |
