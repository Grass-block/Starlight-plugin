# 新版矿车移动兼容 <Badge>starlight-utilities:modern-minecart-sync</Badge>

为旧版客户端提供新版协议下的矿车移动同步信息。

## 基本信息

- 命名空间id: `starlight-utilities:modern-minecart-sync`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块利用 ProtocolLib 与 ViaVersion 检测客户端协议版本，当矿车实体生成或移动时，向后兼容的低版本客户端发送适配的更新数据包，确保新版本服务器上的矿车在旧版客户端中也能正确显示位置与移动轨迹。

## 可配置项目

无独立配置项。

## 命令

无。
