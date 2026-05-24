# 指令补全过滤修复 <Badge>starlight-commands:command-tab-fix</Badge>

修复指令补全时的过滤行为，并提供WorldEdit等插件的补全增强。

## 基本信息

- 命名空间id: `starlight-commands:command-tab-fix`
- 版本: `1.2.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

修正Minecraft客户端Tab补全时过滤逻辑不正确的问题，确保输入字符能正确过滤补全列表。同时为`/reload`命令补充`confirm`参数提示。附带WorldEdit补全增强组件，为`schematic load/delete`提供schematics文件夹内的文件名补全，为`/set`和`/replace`补充`hand`提示。

## 可配置项目

无独立配置项。
