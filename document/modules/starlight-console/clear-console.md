# 控制台清理工具 <Badge>starlight-console:clear-console</Badge>

提供清除控制台屏幕输出的命令。

## 基本信息

- 命名空间id: `starlight-console:clear-console`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块为服务端控制台提供 `/clear-console`（别名 `/cls`）命令，用于快速清除控制台窗口的所有历史输出内容。适合在长时间运行后需要清理控制台显示时使用。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/clear-console` (`/cls`) | `-starlight.console.clear` | 清除控制台屏幕输出 |
