# 领地HUD信息显示 <Badge>starlight-worldguard:wg-region-hud</Badge>

在快捷栏/ActionBar位置实时显示当前WorldGuard领地信息。

## 基本信息

- 命名空间id: `starlight-worldguard:wg-region-hud`
- 版本: `26.5.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

通过Pipeline机制定时轮询玩家所在位置的WorldGuard领地，将领地名称、所有者、ID等信息渲染到玩家的ActionBar中。

支持通过`WGRegionHUD.PIPELINE`注册自定义格式化器（Formatter），允许其他模块扩展模板变量。内置默认格式化器支持`{name}`、`{owner}`、`{id}`占位符替换。

## 可配置项目

| 配置路径 | 类型 | 默认值 | 描述 |
|---------|------|--------|------|
| `config.wg-region-hud.template` | `String` | `"{#white}{msg#ui-name} {#green}{name} {#yellow}\| {#dark-aqua}{msg#ui-owner} {#aqua}{owner} {#yellow}\| {#dark-purple}{msg#ui-id} {#purple}{id}"` | HUD显示模板，支持MiniMessage颜色格式和语言占位符 |

## 命令

无。
