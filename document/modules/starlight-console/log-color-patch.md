# [BETA]日志颜色修复 <Badge>starlight-console:log-color-patch</Badge>

通过 log4j RewritePolicy 对日志输出进行着色处理。

## 基本信息

- 命名空间id: `starlight-console:log-color-patch`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `否`
- 是否为未完成[beta]阶段: `是`

## 描述

该模块通过 log4j2 的 RewriteAppender 机制对服务端日志输出进行颜色标记，根据日志级别（INFO、WARN、ERROR 等）自动应用 ANSI 颜色代码，使控制台日志更易于区分。由于不同终端对 ANSI 转义序列支持程度不同，该功能仍处于 Beta 阶段。

## 可配置项目

无独立配置项。
