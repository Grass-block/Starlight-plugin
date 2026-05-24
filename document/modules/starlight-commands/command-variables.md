# 命令变量 <Badge>starlight-commands:command-variables</Badge>

在命令中插入可复用的变量占位符。

## 基本信息

- 命名空间id: `starlight-commands:command-variables`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

允许玩家在执行的命令中使用`${变量名}`占位符，模块会自动将其替换为预设的变量值。变量支持两种存储模式：`plugin`（插件生命周期内有效，重启后丢失）和`persistent`（持久化存储至数据库）。支持递归替换，变量值中仍可包含`${...}`占位符。拦截玩家命令和控制台命令进行预处理替换。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/variable set <plugin\|persistent> <名称> <值...>` | `quark.commands.variable` | 设置一个命令变量 |
| `/variable get <plugin\|persistent> <名称>` | `quark.commands.variable` | 获取命令变量的值 |
| `/variable delete <plugin\|persistent> <名称>` | `quark.commands.variable` | 删除一个命令变量 |
