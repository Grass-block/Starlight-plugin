# WorldEdit指令补充 <Badge>starlight-commands:world-edit-commands</Badge>

为WorldEdit提供便捷辅助指令。

## 基本信息

- 命名空间id: `starlight-commands:world-edit-commands`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

为WorldEdit玩家补充一系列便捷指令：选区边框绘制(`//outline-box`)、选区镜像(`//mirror`)、镜像旋转(`//mirror-rotate`)、快速排水(`//drain-water`)
、快速树种画笔(`//fast-brash`)。所有指令均基于WorldEdit API实现，需要服务端安装WorldEdit插件。其中`//fast-brash`
预设了30余种树木和草方块画笔模板，方便快速植林。

这些命令的注册名本身带一个 `/`，因此在游戏聊天框输入时使用双斜杠。26.6.4 起，`//outline-box` 的编辑会话加入 WorldEdit 历史记录，可使用 `//undo` 撤销。排水先将水替换为空气，再清除含水状态。

## 可配置项目

无独立配置项。

## 命令

| 命令                    | 权限                         | 说明                              |
|-----------------------|----------------------------|---------------------------------|
| `//outline-box <方块图案>` | `worldedit.region.outline` | 在世界编辑选区的边缘绘制空心框，可撤销 |
| `//mirror` | - | 镜像复制选区（执行复制→翻转→粘贴） |
| `//mirror-rotate` | - | 复制后连续执行四次“旋转 90°→粘贴”，围绕复制参照点生成旋转副本 |
| `//drain-water` | - | 快速清除选区内水源和含水状态 |
| `//fast-brash <树种类型>` | - | 快速选择预设的树木/草方块画笔；拼写以当前注册名 `brash` 为准 |
