# TPS可视化条 <Badge>starlight-management:tps-bar</Badge>

在BossBar上显示服务器实时TPS和MSPT数值，帮助管理员监控服务器性能。

## 基本信息

- 命名空间id: `starlight-management:tps-bar`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

TPSBar 模块会在玩家屏幕顶部的BossBar上持续显示当前服务器的TPS（Tick Per Second）和MSPT（Milliseconds Per Tick）信息。根据MSPT数值高低，BossBar颜色会在绿色（<15ms）、黄色（<35ms）和红色（>=35ms）之间切换，进度条反映MSPT占50ms的比例。玩家可通过指令切换显示或隐藏。

## 可配置项目

| 配置项 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| `tps-bar.line` | String | `{#white}TPS: {tps}     {#white}MSPT: {mspt}` | BossBar显示的内容模板，支持`{tps}`和`{mspt}`占位符 |

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/tpsbar` | `starlight.monitor.tpsbar` | 切换显示/隐藏TPS信息BossBar |
