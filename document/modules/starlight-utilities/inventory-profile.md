# 背包快照 <Badge>starlight-utilities:inventory-profile</Badge>

读取并检查玩家背包数据的 NBT 存储文件。

## 基本信息

- 命名空间id: `starlight-utilities:inventory-profile`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块直接从世界存档的 `playerdata` 目录中读取玩家的 `.dat` 文件，解析 NBT 数据。主要作为调试/测试工具使用，注册了 `inv-serialize` 测试用例用于检查背包序列化数据。

## 可配置项目

无独立配置项。

## 命令

无。
