# 自定义载具实验 <Badge>starlight-oddities:custom-vehicle</Badge>

基于船和方块展示实体的载具模型实验。

## 基本信息

- 命名空间id: `starlight-oddities:custom-vehicle`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`（注解未标记 beta，功能仍为实验实现）

## 描述

当前实现每 tick 检查在线玩家乘坐的船，同步其下层展示实体的朝向与插值参数，并注册 `vehicle-test` 内部测试入口。测试入口读取 `display-models` 资源组的 `example-car.json`，创建船、挂载实体和模型。

当前没有面向玩家的载具创建、保存、选型或删除命令，不能视为完整的自定义载具系统。运行环境需要支持 `BlockDisplay` / `Display` API。

新增的 `oddities/model` 数据类型及解码器属于开发中的模型代码；当前 `CustomVehicle` 测试入口仍调用 `CustomDisplayModel.create`，不会自动加载所有 Bedrock 模型。

## 可配置项目

无独立 YAML 配置项。测试模型来自 `display-models` 资源组。

## 命令

本模块无独立命令。
