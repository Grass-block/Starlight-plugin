# WorldEdit指令补充 <Badge>starlight-commands:world-edit-commands</Badge>

为WorldEdit提供便捷辅助指令。

## 基本信息

- 命名空间id: `starlight-commands:world-edit-commands`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

为WorldEdit玩家补充一系列便捷指令：选区边框绘制(`/outline-box`)、选区镜像(`/mirror`)、快速排水(`/drain-water`)、快速树种画笔(`/fast-brash`)。所有指令均基于WorldEdit API实现，需要服务端安装WorldEdit插件。其中`/fast-brash`预设了30余种树木和草方块画笔模板，方便快速植林。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/outline-box <方块图案>` | `worldedit.region.outline` | 在世界编辑选区的边缘绘制空心框 |
| `/mirror` | - | 镜像复制选区（执行复制→翻转→粘贴） |
| `/drain-water` | - | 快速清除选区内的水源（去水logged方块 + 替换水为空气） |
| `/fast-brash <树种类型>` | - | 快速选择预设的树木/草方块画笔 |
