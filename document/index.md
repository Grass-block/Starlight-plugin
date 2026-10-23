# Starlight 文档

当前文档版本：**26.6.4**

Starlight 是可按包与模块组合启用的服务端插件。本页作为仓库内文档主页，模块说明按当前工作区的注册代码、配置模板和实现整理。

## 26.6.4

新增 [聊天翻译](modules/starlight-chat/chat-translator.md)，更新 [聊天格式配置](modules/starlight-display/chat-format.md)、[物品行为拦截](modules/starlight-security/item-blocker.md)、[额外伤害](modules/starlight-tweaks/extra-damage.md) 和 [WorldEdit 撤销支持](modules/starlight-commands/world-edit-commands.md)。

升级前请阅读 [26.6.4 更新与配置迁移](upgrade-26.6.4.md)。项目历史记录见 [changes.md](../changes.md)，安装与总体介绍见 [中文 README](../readme.zh-cn.md)。

## 阅读与配置约定

- 完整模块 ID 为 `<包ID>:<模块ID>`，例如 `starlight-chat:chat-translator`。
- 常规模块配置通常位于插件数据目录的 `config/values/<包ID>.yml`，文件内使用 `config.<模块ID>` 节点。页面仅列短字段名时，均相对于该节点；仍使用旧配置名的模块以具体页面为准。
- 独立配置（如 `config/translate-service.yml`）保留对应的根节点；分组配置（如 `config/recipes/`）使用各目录中模板所示的根节点。生成的 `*.template.yml` / `template.yml` 用于参考，应修改实际值文件或创建实际条目。
- “默认开启”来自模块声明，不代表兼容性检查一定通过。涉及 WorldEdit、WorldGuard、ProtocolLib 或特定服务端 API 的模块，请同时查看页面中的依赖与限制。
- 模块页“版本”是模块自身的声明值；`-` 表示没有单独声明版本，不等于文档版本未知。
- 命令权限表中的节点用于权限管理；部分旧页保留注解中的 `+` / `-` 前缀，前缀不是实际权限节点的一部分。旧 `quark.*` 权限若仍在代码中使用，文档保留实际名称。

## 模块目录

以下索引覆盖 BaseBundler、ExtensionBundler、LobbyBundler 当前注册的 **110 个 Bukkit 扩展模块**。核心内部服务和 Velocity 插件不计入该数字。

### 聊天（starlight-chat）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [聊天提及](modules/starlight-chat/chat-at.md) | `chat-at` | 是 |
| [聊天组件](modules/starlight-chat/chat-component.md) | `chat-component` | 是 |
| [聊天禁言](modules/starlight-chat/mute.md) | `mute` | 是 |
| [屏蔽词](modules/starlight-chat/chat-filter.md) | `chat-filter` | 是 |
| [聊天举报](modules/starlight-chat/chat-report.md) | `chat-report` | 是 |
| [聊天翻译](modules/starlight-chat/chat-translator.md) | `chat-translator` | 是 |
| [AI 玩家兼容](modules/starlight-chat/ai-player-support.md) | `ai-player-support` | 是 |

### 服务器管理（starlight-management）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [封禁指令](modules/starlight-management/ban.md) | `ban` | 是 |
| [维护模式](modules/starlight-management/maintenance.md) | `maintenance` | 是 |
| [TPS可视化条](modules/starlight-management/tps-bar.md) | `tps-bar` | 是 |
| [服务器信息指令](modules/starlight-management/server-info.md) | `server-info` | 是 |
| [重载自动踢出](modules/starlight-management/kick-on-reload.md) | `kick-on-reload` | 是 |
| [命令行插件管理器](modules/starlight-management/plugin-manager-command.md) | `plugin-manager-command` | 是 |
| [自动化垃圾清理](modules/starlight-management/vm-garbage-cleaner.md) | `vm-garbage-cleaner` | 是 |
| [世界对象过滤](modules/starlight-management/world-object-filter.md) | `world-object-filter` | 否 |

### 控制台（starlight-console）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [控制台清理工具](modules/starlight-console/clear-console.md) | `clear-console` | 是 |
| [控制台代理执行](modules/starlight-console/console-execute.md) | `console-execute` | 是 |
| [[BETA]自定义日志格式](modules/starlight-console/custom-log-format.md) | `custom-log-format` | 否 |
| [[BETA]日志颜色修复](modules/starlight-console/log-color-patch.md) | `log-color-patch` | 否 |
| [停服确认](modules/starlight-console/stop-confirm.md) | `stop-confirm` | 否 |

### 实用工具（starlight-utilities）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [方块更新锁定](modules/starlight-utilities/block-update-locker.md) | `block-update-locker` | 是 |
| [计算器](modules/starlight-utilities/calculator.md) | `calculator` | 是 |
| [自定义相机路径](modules/starlight-utilities/camera-movement.md) | `camera-movement` | 是 |
| [自定义渲染距离](modules/starlight-utilities/dynamic-view-distance.md) | `dynamic-view-distance` | 是 |
| [「一言」](modules/starlight-utilities/hitokoto.md) | `hitokoto` | 是 |
| [玩家延迟查询](modules/starlight-utilities/player-ping-command.md) | `player-ping-command` | 是 |
| [位置锁定指令](modules/starlight-utilities/position-lock.md) | `position-lock` | 是 |
| [位置对齐指令](modules/starlight-utilities/position-align.md) | `position-align` | 是 |
| [环境方块刷新](modules/starlight-utilities/surrounding-refresh.md) | `surrounding-refresh` | 是 |
| [服务器刻控制](modules/starlight-utilities/tick-manager.md) | `tick-manager` | 是 |
| [自由视角](modules/starlight-utilities/freecam.md) | `freecam` | 是 |
| [飞行控制指令](modules/starlight-utilities/fly-command.md) | `fly-command` | 是 |
| [粒子文本绘制](modules/starlight-utilities/particle-font.md) | `particle-font` | 是 |
| [客户端环境设置](modules/starlight-utilities/client-environment-setting.md) | `client-environment-setting` | 是 |
| [菜单物品触发器](modules/starlight-utilities/menu-item.md) | `menu-item` | 是 |
| [背包快照](modules/starlight-utilities/inventory-profile.md) | `inventory-profile` | 是 |
| [新版矿车移动兼容](modules/starlight-utilities/modern-minecart-sync.md) | `modern-minecart-sync` | 是 |

### 代理与跨服（starlight-proxy）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [集群聊天同步](modules/starlight-proxy/chat-sync.md) | `chat-sync` | 是 |
| [Geyser皮肤重映射](modules/starlight-proxy/geyser-skin-redirect.md) | `geyser-skin-redirect` | 是 |
| [Bungeecord集群保护](modules/starlight-proxy/legacy-forwarding-protect.md) | `legacy-forwarding-protect` | 否 |
| [集群Ping指标修正](modules/starlight-proxy/proxy-ping.md) | `proxy-ping` | 否 |
| [客户端跳转mod支持](modules/starlight-proxy/client-transfer-support.md) | `client-transfer-support` | 是 |
| [代理外信息同步](modules/starlight-proxy/out-proxy-info-sync.md) | `out-proxy-info-sync` | 否 |

### 命令扩展（starlight-commands）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [方块动画](modules/starlight-commands/animate-block-command.md) | `animate-block-command` | 是 |
| [指令补全过滤修复](modules/starlight-commands/command-tab-fix.md) | `command-tab-fix` | 是 |
| [实体动量指令](modules/starlight-commands/entity-motion.md) | `entity-motion` | 是 |
| [实体代理执行指令](modules/starlight-commands/command-exec.md) | `command-exec` | 是 |
| [戴帽子](modules/starlight-commands/hat.md) | `hat` | 是 |
| [物品指令触发器](modules/starlight-commands/item-command.md) | `item-command` | 是 |
| [发送信息](modules/starlight-commands/self-message.md) | `self-message` | 是 |
| [WorldEdit指令补充](modules/starlight-commands/world-edit-commands.md) | `world-edit-commands` | 是 |

### 安全防护（starlight-security）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [额外权限控制](modules/starlight-security/advanced-permission-control.md) | `advanced-permission-control` | 是 |
| [爆炸防护](modules/starlight-security/explosion-defender.md) | `explosion-defender` | 是 |
| [广电联合封禁同步](modules/starlight-security/img-regulation-sync.md) | `img-regulation-sync` | 否 |
| [IP 属地检测](modules/starlight-security/ip-defender.md) | `ip-defender` | 是 |
| [简易权限控制](modules/starlight-security/permission-manager.md) | `permission-manager` | 否 |
| [WorldEdit 操作监测](modules/starlight-security/we-operation-defender.md) | `we-operation-defender` | 是 |
| [访客模式](modules/starlight-security/guest-mode.md) | `guest-mode` | 是 |
| [终末水晶保护](modules/starlight-security/end-protect.md) | `end-protect` | 否 |
| [插件字节码扫描](modules/starlight-security/plugin-backdoor-scanner.md) | `plugin-backdoor-scanner` | 是 |
| [物品行为拦截](modules/starlight-security/item-blocker.md) | `item-blocker` | 是 |

### 显示与界面（starlight-display）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [快捷栏HUD信息显示](modules/starlight-display/action-bar-hud.md) | `action-bar-hud` | 是 |
| [挂机检测](modules/starlight-display/afk.md) | `afk` | 是 |
| [聊天行格式化](modules/starlight-display/chat-format.md) | `chat-format` | 是 |
| [自定义死亡信息格式](modules/starlight-display/custom-death-message.md) | `custom-death-message` | 是 |
| [自定义MOTD信息](modules/starlight-display/custom-motd.md) | `custom-motd` | 是 |
| [自定义计分板提示信息](modules/starlight-display/custom-scoreboard.md) | `custom-scoreboard` | 是 |
| [掉落物高亮信息](modules/starlight-display/drop-item-info.md) | `drop-item-info` | 是 |
| [玩家头衔](modules/starlight-display/player-name-header.md) | `player-name-header` | 是 |
| [TAB栏提示信息](modules/starlight-display/tab-menu.md) | `tab-menu` | 是 |
| [自定义欢迎信息](modules/starlight-display/welcome-message.md) | `welcome-message` | 是 |
| [WorldEdit选区渲染器](modules/starlight-display/we-session-renderer.md) | `we-session-renderer` | 是 |
| [自定义踢出信息](modules/starlight-display/custom-kick-message.md) | `custom-kick-message` | 是 |
| [玩家加入提示](modules/starlight-display/player-join-message.md) | `player-join-message` | 是 |
| [聊天栏公告](modules/starlight-display/chat-announce.md) | `chat-announce` | 是 |
| [悬浮文本](modules/starlight-display/hover-display.md) | `hover-display` | 是 |

### 配置内容加载（starlight-sideload）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [自定义配方加载](modules/starlight-sideload/recipe-loader.md) | `recipe-loader` | 是 |
| [物品菜单](modules/starlight-sideload/inventory-menu.md) | `inventory-menu` | 是 |
| [资源包加载](modules/starlight-sideload/resource-pack-loader.md) | `resource-pack-loader` | 是 |

### 传送（starlight-warps）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [导航点](modules/starlight-warps/waypoint.md) | `waypoint` | 是 |
| [随机传送](modules/starlight-warps/rtp.md) | `rtp` | 是 |
| [传送请求](modules/starlight-warps/tpa.md) | `tpa` | 是 |
| [回到死亡点](modules/starlight-warps/back-to-death.md) | `back-to-death` | 是 |

### 游戏机制调整（starlight-tweaks）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [作物采集](modules/starlight-tweaks/crop-click-harvest.md) | `crop-click-harvest` | 是 |
| [发射器交互修改](modules/starlight-tweaks/dispenser-interaction.md) | `dispenser-interaction` | 是 |
| [双开门同步](modules/starlight-tweaks/double-door-sync.md) | `double-door-sync` | 是 |
| [真实睡眠](modules/starlight-tweaks/realistic-sleep.md) | `realistic-sleep` | 是 |
| [连锁采集](modules/starlight-tweaks/vein-miner.md) | `vein-miner` | 是 |
| [手持工作方块](modules/starlight-tweaks/portable-functional-blocks.md) | `portable-functional-blocks` | 是 |
| [快速打开潜影盒](modules/starlight-tweaks/portable-shulker-box.md) | `portable-shulker-box` | 是 |
| [物品防丢](modules/starlight-tweaks/item-drop-secure.md) | `item-drop-secure` | 是 |
| [楼梯椅子](modules/starlight-tweaks/stair-seat.md) | `stair-seat` | 是 |
| [真实矿车](modules/starlight-tweaks/realistic-minecart.md) | `realistic-minecart` | 是 |
| [骑你头上](modules/starlight-tweaks/sit-on-player.md) | `sit-on-player` | 是 |
| [快速存入](modules/starlight-tweaks/quick-deposit.md) | `quick-deposit` | 是 |
| [额外伤害](modules/starlight-tweaks/extra-damage.md) | `extra-damage` | 否 |

### 趣味与实验功能（starlight-oddities）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [电梯方块](modules/starlight-oddities/elevator.md) | `elevator` | 是 |
| [中国人能飞](modules/starlight-oddities/chinese-can-fly.md) | `chinese-can-fly` | 否 |
| [自定义载具实验](modules/starlight-oddities/custom-vehicle.md) | `custom-vehicle` | 是 |

### 音乐（starlight-music）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [音乐播放器](modules/starlight-music/music-player.md) | `music-player` | 是 |

### AI 聊天（starlight-ai）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [AI 命令聊天](modules/starlight-ai/ai-command-chat.md) | `ai-command-chat` | 是 |

### WorldGuard 领地（starlight-worldguard）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [领地HUD信息显示](modules/starlight-worldguard/wg-region-hud.md) | `wg-region-hud` | 是 |
| [WorldEdit领地检查](modules/starlight-worldguard/wg-we-check.md) | `wg-we-check` | 是 |
| [领地认领](modules/starlight-worldguard/wg-claim.md) | `wg-claim` | 是 |
| [领地自定义名称](modules/starlight-worldguard/wg-custom-name.md) | `wg-custom-name` | 是 |
| [领地出生点与传送](modules/starlight-worldguard/wg-spawn-tp.md) | `wg-spawn-tp` | 是 |

### 大厅（starlight-lobby）

| 模块 | 模块 ID | 默认开启 |
|----|----|----|
| [自动回到重生点](modules/starlight-lobby/lobby-back-to-spawn.md) | `lobby-back-to-spawn` | 是 |
| [默认物品栏](modules/starlight-lobby/lobby-default-inventory.md) | `lobby-default-inventory` | 是 |
| [地图保护](modules/starlight-lobby/lobby-map-protect.md) | `lobby-map-protect` | 是 |
| [玩家保护](modules/starlight-lobby/lobby-player-protect.md) | `lobby-player-protect` | 是 |

## 保留但未注册的功能

- [音乐游戏](modules/starlight-music/music-game.md)：标准分发包的注册调用被注释。
- [命令变量](modules/starlight-commands/command-variables.md)：当前命令包未注册该类。
- `starlight-economy`、`starlight-mission`：包提供器注解被注释，不能仅凭源码文件存在就认为可用。

## 开发文档

1. [扩展包开发](development/01-extension-pack.md)
2. [内容注册](development/02-content-registration.md)
3. [模块开发](development/03-module-development.md)
4. [服务组件开发](development/04-service-development.md)
5. [聊天转发与语言 API（26.6.4）](development/05-chat-and-locale.md)
6. [展示模型实验代码](development/06-display-models.md)

另见 [数据库说明](development/database.md) 与 [模块文档模板](module-template.md)。
