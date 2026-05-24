# 停服确认 <Badge>starlight-console:stop-confirm</Badge>

拦截 `/stop` 命令，要求玩家输入确认指令以防止误操作停服。

## 基本信息

- 命名空间id: `starlight-console:stop-confirm`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `否`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块会拦截玩家执行的 `/stop` 命令，当检测到玩家尝试执行停服指令时，会取消该命令并提示玩家需要通过 `/stop confirm` 进行二次确认。控制台直接执行的 `/stop` 不受影响。可有效防止管理员误操作导致服务意外关闭。

## 可配置项目

无独立配置项。
