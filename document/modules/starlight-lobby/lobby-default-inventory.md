# 默认物品栏 <Badge>starlight-lobby:lobby-default-inventory</Badge>

为玩家提供固定不可变的默认物品栏，阻止丢弃/拾取物品，并通过独立配置文件持久化编辑内容。

## 基本信息

- 命名空间id: `starlight-lobby:lobby-default-inventory`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块在玩家加入或尝试点击物品栏时，强制覆盖为其预设的默认物品栏内容。OP 以外的玩家无法丢弃、拾取或修改物品栏物品，确保大厅体验一致。管理员可通过命令编辑默认物品栏配置并持久化保存。

## 可配置项目

配置文件 `lobby-default-inventory.yml` 以槽位索引为键、物品堆为值动态生成，无固定配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/lobby-default-inventory cover` | `-` | 将发送者的物品栏覆盖为默认配置 |
| `/lobby-default-inventory edit` | `-` | 打开默认物品栏编辑器进行可视化管理 |
| `/lobby-default-inventory reload` | `-` | 从配置文件重载默认物品栏内容 |
