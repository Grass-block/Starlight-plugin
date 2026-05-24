# 资源包加载 <Badge>starlight:resource-pack-loader</Badge>

内置资源包服务器与下发

## 基本信息

- 命名空间id: `starlight:resource-pack-loader`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

集成内嵌的 HTTP 文件服务器，在插件目录的 `assets/resource-packs/` 下托管资源包文件。玩家加入服务器时自动下发并应用资源包，也支持通过命令手动重新获取。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/get-resource` | - | 手动获取并应用资源包 |
