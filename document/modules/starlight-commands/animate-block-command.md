# 方块动画 <Badge>starlight-commands:animate-block-command</Badge>

利用WorldEdit选区创建方块飞行动画。

## 基本信息

- 命名空间id: `starlight-commands:animate-block-command`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

允许玩家将WorldEdit选区内的方块转换为FallingBlock实体，按照指定路径和时间飞行到目标位置。选区最大限制为16格每轴。提供了一个"动画创建法杖"自定义物品，可用左键标记命令方块位置、右键标记目标位置，辅助快速创建动画路径。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 说明 |
|------|------|------|
| `/animate-block <time> <x0> <y0> <z0> <x1> <y1> <z1> <tx> <ty> <tz>` | - | 将选区(`x0,y0,z0`~`x1,y1,z1`)内的方块以`time`刻时长飞行到目标坐标(`tx,ty,tz`) |
