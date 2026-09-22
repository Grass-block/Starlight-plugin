# 悬浮文本 <Badge>starlight-display:hover-display</Badge>

在指定位置创建悬浮显示的文本或全息文字。

## 基本信息

- 命名空间id: `starlight-display:hover-display`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

允许管理员在游戏世界中创建持久化的悬浮文本实体（基于盔甲架），支持自定义文本内容、位置、可见范围等。文本内容支持PlaceHolder占位符动态渲染，并可通过数据库长期存储。提供
`/hover-display`命令进行创建、删除、移动和管理操作。

## 可配置项目

| 配置项          | 类型     | 说明       |
|--------------|--------|----------|
| `range`      | Double | 悬浮文本可见范围 |
| `line-space` | Double | 多行文本行间距  |

## 命令

| 命令                                    | 权限                        | 说明                             |
|---------------------------------------|---------------------------|--------------------------------|
| `/hover-display create <name> <text>` | `-starlight.hoverdisplay` | 在当前位置创建悬浮文本，多行以 `{#return}` 分隔 |
| `/hover-display delete <name>`        | `-starlight.hoverdisplay` | 删除指定悬浮文本                       |
| `/hover-display delete-all`           | `-starlight.hoverdisplay` | 清除全部悬浮文本                       |
| `/hover-display edit <name> <text>`   | `-starlight.hoverdisplay` | 编辑指定悬浮文本内容                     |
| `/hover-display tp <name>`            | `-starlight.hoverdisplay` | 将指定悬浮文本移动到当前位置                 |
