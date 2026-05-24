# 自定义配方加载 <Badge>starlight:recipe-loader</Badge>

YAML 驱动的自定义合成配方

## 基本信息

- 命名空间id: `starlight:recipe-loader`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

通过 YAML 配置文件定义和加载自定义合成配方。支持有序合成（shaped）、无序合成（shapeless）和切石机（stone-cutter）三种配方类型，并可设置带附魔、属性的物品作为输入/输出。配方文件存放在 `recipes/` 目录下。

## 可配置项目

无独立配置项。配方文件通过 `recipes/` 目录下的 YAML 配置定义。
