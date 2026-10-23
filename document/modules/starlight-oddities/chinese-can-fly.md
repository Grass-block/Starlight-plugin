# 中国人能飞 <Badge>starlight-oddities:chinese-can-fly</Badge>

检测到中文客户端时临时开启飞行，属于彩蛋功能。

## 基本信息

- 命名空间id: `starlight-oddities:chinese-can-fly`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `否`
- 是否为未完成[beta]阶段: `否`

## 描述

当玩家客户端语言为中文（`zh_cn` / `zh_hk` / `zh_tw` / `lzh`
）时，事件处理代码会记录玩家原有的允许飞行状态、开启飞行并发送提示。退出时仅移除内存记录。属于彩蛋性质的功能，默认关闭。

当前类未声明 `@AutoRegister(Registrations.SERVER_EVENT)`，而恢复逻辑对记录是否存在的判断也有问题；不能将“启用后必定自动生效”或“切换语言、退出后自动恢复飞行”视为已实现的保证。

## 可配置项目

无额外配置项。

## 命令

本模块无独立命令。
