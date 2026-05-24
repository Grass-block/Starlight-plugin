# [BETA]自定义日志格式 <Badge>starlight-console:custom-log-format</Badge>

允许加载外部的 log4j XML 配置文件以替换默认日志格式。

## 基本信息

- 命名空间id: `starlight-console:custom-log-format`
- 版本: `1.0.0`
- 是否为内部模块: `否`
- 是否默认开启: `否`
- 是否为未完成[beta]阶段: `是`

## 描述

该模块可将服务端的 log4j 日志输出格式替换为自定义 XML 配置文件中定义的格式。模块启动时会读取插件数据目录下的 `log.xml` 文件并应用到 log4j 上下文。不兼容纯 Bukkit/Spigot/Arclight/Banner 服务端。提供 `/log-format` 命令用于手动重载日志格式配置。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/log-format` | `-starlight.console.format` | 重新加载日志格式配置 |
