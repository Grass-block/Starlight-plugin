# 音乐播放器 <Badge>starlight-music:music-player</Badge>

提供全服统一的音乐播放功能，支持远程音乐获取、播放控制与图形化选歌界面。

## 基本信息

- 命名空间id: `starlight-music:music-player`
- 版本: `1.0.3`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

音乐播放器是 starlight-music 的核心模块，负责管理全局音乐会话。它通过 APM 远程消息服务实现跨服务器音乐同步，支持播放、暂停、取消以及
legacy 音色控制。模块内置了基于 InventoryUI 的图形选歌界面，玩家可通过 GUI 浏览并点播服务器中的音乐文件。

支持 MIDI（`.mid` / `.midi`）和 NBS。26.6.4 的 `LegacyMusicSession` 改用阻塞队列接收播放请求，销毁会话时通过结束标记唤醒并退出线程。`play` 在已有播放时仍会取消当前曲目，不代表新增了玩家可管理的播放列表。

## 可配置项目

| 配置项     | 类型        | 默认值                                                                                                    | 描述                  |
|---------|-----------|--------------------------------------------------------------------------------------------------------|---------------------|
| `mount` | `boolean` | `false`                                                                                                | 是否将声音绑定到玩家实体；关闭则在玩家位置播放 |
| `ui`    | `string`  | `"{#white}{msg#playing} {#yellow}: {#aqua}{name} {#yellow}\|{#white} {time}{#gray} / {#white}{total}"` | 播放器 ActionBar UI 模板 |

## 命令

| 命令                 | 权限                     | 描述         |
|--------------------|------------------------|------------|
| `/music play <曲目或random> [选项...]` | `starlight.music.play` | 播放指定音乐或随机曲目 |
| `/music pause`     | `starlight.music.play` | 暂停当前播放     |
| `/music resume`    | `starlight.music.play` | 恢复播放       |
| `/music cancel`    | `starlight.music.play` | 停止播放       |
| `/music gui [页码]`  | `starlight.music.play` | 打开音乐选择 GUI |

GUI 页码从 `0` 开始。播放选项包括 `-p:N`（音高偏移）、`-s:N`（速度倍率）、`-legacy`（切换音色分派模式）和 `-i:on` / `-i:smart`（插值模式）。曲目名应使用补全中提供的完整文件名。

上表 `mount` 和 `ui` 位于 `config/values/starlight-music.yml` 的 `config.music-player` 下；本地 / 远程曲库由同文件的 `config.music-service.remote` 与 `cdn-server` 控制。
