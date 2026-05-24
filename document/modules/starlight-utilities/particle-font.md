# 粒子文本绘制 <Badge>starlight-utilities:particle-font</Badge>

使用粒子效果在世界中绘制文字。

## 基本信息

- 命名空间id: `starlight-utilities:particle-font`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块基于粒子系统在三维空间中渲染文字内容。支持 TrueType 字体文件（内置霞鹜文楷 Lite 等预设字体下载），可绘制单行或多行文本。使用 `/particle-font` 指令指定文字内容与位置参数，在服务器世界中生成粒子的文字效果。

## 可配置项目

无独立配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/particle-font <文字> [参数...]` | `-starlight.particlefont` | 在指定位置使用粒子绘制文字 |
