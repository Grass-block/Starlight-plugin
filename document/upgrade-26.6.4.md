# 26.6.4 更新与配置迁移

本文对应当前工作区中的 26.6.4 文档更新。完整版本记录见项目 [changes.md](../changes.md)。模块页中的“版本”是 `@ApplicationModule` 的独立版本字段，不等于分发包版本；未填写时显示 `-`。

## 需要调整的配置

| 模块 | 旧配置 / 行为 | 26.6.4 的配置 / 行为 |
|----|----|----|
| [聊天格式](modules/starlight-display/chat-format.md) | 独立 `world`、`time` 字段 | 使用 `template`、`prefix`、`prefix-near`、`postfix`、`postfix-near`；时间用 `{time}` 引用 |
| [物品拦截](modules/starlight-security/item-blocker.md) | `item-defender`，内嵌 `illegal-list` / `warning-list` | 模块 ID 改为 `item-blocker`，规则独立到 `config/item-blocker-rules.yml`；分别设置匹配 ID、提示等级、取消操作、移除物品 |
| [额外伤害](modules/starlight-tweaks/extra-damage.md) | 数值映射和 `percent-damage` | 使用 `true-damage` / `extra-damage` 下的效果字符串列表；百分比以 `1.5` 表示 1.5%，而不是 150% |
| [聊天翻译](modules/starlight-chat/chat-translator.md) | 无此模块 | 显示配置在 `starlight-chat.yml`；服务另存 `config/translate-service.yml`，默认 `active: NONE`，需手动选择服务 |
| 语言命令 | `quark.locale` 权限 | 改为 `starlight.locale`；`/locale` 新增 `/language`、`/lang` 别名 |

修改现有文件时保留已有的自定义值，并补入需要的新字段。模板文件用于参考，实际值配置为插件数据目录下的 `config/values/<包ID>.yml`。不能依赖模板更新自动完成重命名、规则转换或旧列表迁移。

`chat-filter`、`mute`、`chat-report` 在 26.6.3 已移入 `starlight-chat` 包。仍使用更早版本配置时，先将相应配置和语言内容迁到该包，再进行本页调整。

## 行为变化

- `//outline-box` 的编辑会话现在进入 WorldEdit 历史，可由 `//undo` 撤销。
- 聊天格式、聊天组件、聊天举报和聊天翻译接入 `ForwardedChatEvent`。Paper 优先使用原生聊天事件与渲染流程，其他 Bukkit 环境使用兼容转发；具体 API 限制见 [聊天开发说明](development/05-chat-and-locale.md)。
- 音乐会话使用阻塞队列等待播放请求，并用结束标记退出；播放器命令仍控制全局会话。
- 语言服务启用时重新检查当前在线玩家，覆盖插件热重载场景。
- `starlight-economy` 和 `starlight-mission` 的包提供器目前未启用；`music-game`、`command-variables` 也未在标准分发包注册。

## 语言设置

| 命令 | 权限节点 | 说明 |
|----|----|----|
| `/locale <语言代码>` | `starlight.locale` | 指定自己的 Starlight 语言，例如 `zh_cn`、`en_us`、`ja_jp` |
| `/locale auto` | 同上 | 清除自定义语言，恢复自动检测 |
| `/language ...` / `/lang ...` | 同上 | `/locale` 的别名 |

命令仅供玩家执行。虽然补全中包含 `gui`，当前 GUI 打开分支尚未实现，请使用语言代码或 `auto`。

## 离线世界生成修复工具

当前 bundler 的 JAR 清单增加了 `org.atcraftmc.starlight.SelfBootUtil` 入口。该工具的 `-fix-wg` 参数用于清理已经移除模组留下的世界生成维度与生物群系引用，不是 WorldGuard 修复命令。

从服务器根目录运行带该入口的已有 JAR：

```text
java -jar <带有SelfBootUtil入口的Starlight文件.jar> -fix-wg
```

工具扫描当前工作目录的 `mods/`，从 NeoForge / Forge TOML、Fabric JSON、旧 Forge `mcmod.info` 提取模组 ID；仅处理 `world/level.dat`，并先复制为 `world/level-backup_<时间戳>.dat`。它会移除不在检测集合中且不是 `minecraft` 的命名空间引用，不会修复其他世界目录。

这是会写入存档的离线维护工具，应在服务器停止且已备份存档后使用；没有 `mods/` 或无法读取时得到空集合，不适合用来无差别处理依赖数据包自定义维度的存档。本次文档更新没有执行该工具。
