# 资源包加载 <Badge>starlight-sideload:resource-pack-loader</Badge>

内置资源包服务器与下发

## 基本信息

- 命名空间id: `starlight-sideload:resource-pack-loader`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

集成内嵌的 HTTP 文件服务器，在插件目录的 `assets/resource-packs/` 下托管资源包文件。玩家加入服务器时自动下发并应用资源包，也支持通过命令手动重新获取。

启用时检查源 ZIP 并合并为 `_compiled.zip`，`_index.dat` 用于追踪文件变化；这两个文件不作为源资源包加载。HTTP 处理器注册在核心 `HttpService` 的 `/resource-pack` 路径，服务需要可用。

当前发给客户端的地址固定为 `http://localhost:8080/resource-pack`，强制接受参数固定为 `true`，提示文本为空。因此远程玩家会访问自己电脑的 localhost，当前代码不能直接作为公网资源包分发方案。

## 可配置项目

无独立配置项。

## 命令

| 命令              | 权限 | 说明         |
|-----------------|----|------------|
| `/get-resource` | -  | 手动获取并应用资源包 |
