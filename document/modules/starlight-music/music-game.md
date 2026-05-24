# 音乐游戏 <Badge>starlight-music:music-game</Badge>

提供基于物品交互的音乐节奏游戏玩法，玩家使用特殊武器击打方块以演奏音乐。

## 基本信息

- 命名空间id: `starlight-music:music-game`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

音乐游戏模块将音乐播放与游戏玩法结合，玩家使用钻石剑/金剑/铁剑等特殊自定义物品击打方块或空气来触发音符。模块支持创建音乐游戏会话，实时计算击打判定与评分。该模块仍处于早期阶段，命令仅提供 `create` 子命令。

## 可配置项目

| 配置项 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| `edit-mount` | `string` | `"world:50,0,0"` | 编辑模式挂载点坐标 |
| `ui` | `string` | `"{#white}{msg#playing}: {#aqua}{name} {#yellow}\| {#white}{time}{#gray} / {#white}{total} {#yellow}\| {#white}{msg#notes}: {#purple}{notes}{#white}/{#dark-purple}{total-notes}"` | 游戏进行时的 ActionBar UI 模板 |

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/music-game create <曲目>` | `starlight.music.game` | 创建并开始一个音乐游戏会话 |
