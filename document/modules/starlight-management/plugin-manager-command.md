# 命令行插件管理器 <Badge>starlight-management:plugin-manager-command</Badge>

通过命令行管理服务器插件，支持加载、卸载、启用、禁用、重启和查看插件信息。

## 基本信息

- 命名空间id: `starlight-management:plugin-manager-command`
- 版本: `1.1.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

PluginManagerCommand 模块提供了一套完整的插件管理指令，替代原版 `/plugins` 命令。支持加载新插件jar、卸载已加载插件、重载单个插件、启用/禁用插件以及查看插件详细信息（包含版本、作者、依赖、API版本等）。插件列表使用可交互的文本组件展示，悬停显示插件信息，点击可查看详情。

## 可配置项目

无配置项。

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/plugins list` | `bukkit.plugins` | 列出所有插件及其启用状态 |
| `/plugins load <file>` | `bukkit.plugins` | 从plugins目录加载指定jar文件 |
| `/plugins unload <name>` | `bukkit.plugins` | 卸载指定插件 |
| `/plugins reload <name>` | `bukkit.plugins` | 重载指定插件（卸载后重新加载） |
| `/plugins enable <name>` | `bukkit.plugins` | 启用指定插件 |
| `/plugins disable <name>` | `bukkit.plugins` | 禁用指定插件 |
| `/plugins restart <name>` | `bukkit.plugins` | 重启指定插件 |
| `/plugins info <name>` | `bukkit.plugins` | 查看指定插件的详细信息 |

别名: `/pl`
