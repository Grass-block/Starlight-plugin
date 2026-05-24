# 领地自定义名称 <Badge>starlight-worldguard:wg-custom-name</Badge>

允许玩家为领地设置自定义显示名称，并在领地HUD中展示。

## 基本信息

- 命名空间id: `starlight-worldguard:wg-custom-name`
- 版本: `26.5.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

通过`/plot rename <名称>`指令，玩家可以为自己的领地设置一个自定义显示名称。该名称通过`WorldGuardExtraInfoService`持久化存储在额外数据中。

在启用`wg-region-hud`模块时，通过向`WGRegionHUD.PIPELINE`注册自定义格式化器，将自定义名称注入到HUD模板的`{name}`占位符中（格式为`{自定义名称};`）。若未设置自定义名称则使用领地原始ID。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/plot rename <名称>` | `+starlight.worldguard.rename` | 为当前所在位置的领地设置自定义名称 |
