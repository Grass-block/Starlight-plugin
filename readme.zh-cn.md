<div align="center" id="readme-top">

<img alt="logo" width="160" style="border-radius: 1rem" src="https://raw.githubusercontent.com/Grass-block/Starlight/refs/heads/main/starlight-logo.png">

<h2 align="center">Starlight -「星辰」</h2>

一个「原子级」设计的服务端插件，旨在覆盖您所需要的一切。

[查阅文档](https://dev.atcraftmc.cn/starlight/) | [报告问题](https://github.com/Grass-block/Quark-Plugin/issues)

![MCVersion](https://img.shields.io/badge/minecraft-1.17.1_--_1.20.4-3366CC?style=for-the-badge&logoColor=blue&labelColor=29355F)
![Java17+](https://img.shields.io/badge/java-17+-009B98?style=for-the-badge&logoColor=blue&labelColor=29355F)
![MohistCompat](https://img.shields.io/badge/Mohist-Compatible-AD3333?style=for-the-badge&logoColor=blue&labelColor=29355F)

</div>

> **本项目是 [quark-plugin](https://modrinth.com/plugin/quark-plugin/) 的延续。<br>**
> 更多信息和旧版下载请前往该页面。数据升级和迁移请参阅下方内容。
> 欢迎加入QQ群交流：1093970869

## Description

这是一个面向 Spigot/Paper/Folia/Mohist 服务器的综合性插件套件，
包含 90 多个模块，为任意规模的服务器提供海量功能！
本插件还包含多项性能优化和修复。
从管理到显示、游戏特性到安全防护，
这些内容将从各个方面提升您的服务器品质。

### Possible Usages

- 搭建原版生存服务器
- 配合 WorldEdit 和 CoreProtect 搭建创造服务器
- 配合 Citizens 和签到插件搭建大厅服务器

所有功能均独立、可组合。
您可以通过启用或禁用模块、
添加或移除扩展包，
甚至通过 SDK 创建自己的功能包来定制功能。

### Goal and reason of this

我们并不追求大而全的内容，也不打算在单一功能上做到极致。
我们所做的是填补服务端未被修改的细节之处。
因此请不要指望我们会做出模组级别的功能——至少在基础包中不会。
Starlight 只提供基础可用的实现，如果您发现有专门的插件可以替代部分功能，
请放心使用它们——它们 100% 更好。

### Core Features

- 多平台支持：支持几乎所有 Bukkit 实现的服务端（见下方）
- 快速启动：初始化耗时低于 600ms，支持命令行热重载。
- 模块化设计：所有功能均可独立开关。

## DISCLAIMER / 免责声明

本项目中的语言代码仅用于标识语言及本地化内容，以兼容 Minecraft 及相关国际化标准。
部分语言代码可能包含地区标识，其用途仅为区分不同语言习惯与翻译分支，不代表任何政治立场或地区主张。

部分外部服务（如 IP 属地、系统 Locale 或第三方
API）返回的信息由其提供方决定，本项目不对相关数据的准确性或地区划分方式作额外定义。<br>

Language codes used in this project are intended solely for localization and compatibility purposes,
following Minecraft and common internationalization conventions where applicable.
Some language tags may include regional identifiers used only to distinguish language variants or localization
differences,
and do not imply any political position or territorial claim.

Certain external information (such as IP geolocation, system locale, or third-party API results) is provided by external
services.
This project does not define or endorse any regional classification returned by such services.

## Installation

### 1. 从发布页面下载整合包。

### 2. 安装。

- 关闭服务器或输入 `/starlight reload prepare`
- 将 jar 文件丢入插件目录。

### 3. 重载。

- 启动服务器或输入 `/starlight reload action`
- 等待完成。

### 4. 卡在库文件下载？

为了更好的性能和更小的体积，Starlight 会使用 OTA 模式
下载所有需要的依赖库。

如果在库文件加载时卡住，可以在 config.yml 中更换 Maven 镜像：

```yaml
config:
  #...
  dependency:
    maven-repo: ALICLOUD
    # 接受任意 Maven 仓库 URL，或：
    #  - 'ALICLOUD'（阿里云 Maven 中央仓库）
    #  - 'HUAWEI'（华为 Maven 仓库）
    #  - 'TENCENT'（腾讯云 Maven 仓库）
    #  - 'TSINGHUA'（清华大学 Maven 仓库）
    #  - 'CENTRAL'（Maven 中央仓库，适合非中国用户）
```

## Contents

> **Basic concepts**
> - Module（模块）：本制品提供功能的最小单元。
> - Package（包）：模块的最小分组，共享配置和国际化文件。
    > 包和模块均可通过命令启用或禁用。<br>
    > 一个 jar 文件包含多个模块。

点击每个标签页展开查看包含的内容。<br>
如果不这样做，这份 README 会变得太太太太太太长了。

<details>
<summary>Content: </summary>

### starlight-commands:
- Entity Proxy Execute Command: 以其他实体或玩家的身份执行命令 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/command-exec.html)
- Send Message: 向发送者发送格式化消息 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/self-message.html)
- Item Command Trigger: 将命令和行为绑定到物品上 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/item-command.html)
- Wear Hat: 将手持物品装备到头部 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/hat.html)
- Block Animation: 使用掉落物方块效果播放方块动画 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/animate-block-command.html)
- WorldEdit Command Supplement: 提供 WorldEdit 实用画笔和编辑命令 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/world-edit-commands.html)
- Entity Motion Command: 通过速度指令控制实体运动 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/entity-motion.html)
- Command Tab Completion Fix: 修复服务器命令的 Tab 补全 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/command-tab-fix.html)

### starlight-warps:
- Waypoint: 创建和管理玩家传送点 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-warps/waypoint.html)
- Back to Death Point: 将玩家传送回死亡位置 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-warps/back-to-death.html)
- Teleport Request: 处理玩家间的传送请求与接受 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-warps/tpa.html)
- Random teleport: 将玩家随机传送到世界中的安全位置 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-warps/rtp.html)

### starlight-security:
- WorldEdit Operation Monitor: 通过确认机制防止未经授权的 WorldEdit 操作 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/we-operation-defender.html)
- IP Address Detection: 检测恶意 IP 连接并提供 IP 查询指令 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/ip-defender.html)
- Explosion Defender: 通过白名单机制防护方块和实体爆炸 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/explosion-defender.html)
- Advanced Permission Control: 控制聊天、交互、破坏等高级玩家权限 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/advanced-permission-control.html)
- plugin-backdoor-scanner: 扫描插件中的恶意后门代码 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/plugin-backdoor-scanner.html)
- Guest Mode: 在指定世界中限制访客玩家的操作 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/guest-mode.html)
- IMG Regulation Sync: 从广电封禁服务同步玩家封禁记录 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/img-regulation-sync.html)
- end-protect: 防止在末地放置末影水晶 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/end-protect.html)
- Simple Permission Control: 通过 JDBC 存储和命令管理玩家权限 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/permission-manager.html)

### starlight-oddities:
- Elevator Block: 创建类似 OpenBlock 风格的电梯方块 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-oddities/elevator.html)

### starlight-tweaks:
- Sit on players: 允许玩家右键坐在他人头上 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/sit-on-player.html)
- Portable Functional Blocks: 从手持物品打开功能方块界面 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/portable-functional-blocks.html)
- Stair Seat: 允许玩家坐在楼梯方块上 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/stair-seat.html)
- Realistic Minecart: 增强矿车行为，提供真实物理和控制 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/realistic-minecart.html)
- Quick Open Shulker Box: 从物品栏直接打开潜影盒 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/portable-shulker-box.html)
- Crop Click Harvest: 右键点击即可收获作物 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/crop-click-harvest.html)
- Double Door Synchronization: 同步相邻木门同时打开 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/double-door-sync.html)
- Dispenser Interaction Imporovement: 增强发射器的自定义交互行为 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/dispenser-interaction.html)
- Realistic Sleep: 需要多数玩家睡觉才能跳过夜晚 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/realistic-sleep.html)
- Vein Miner: 一次破坏整条连接的矿石矿脉 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/vein-miner.html)
- Drop Prevention: 防止意外丢弃受保护的物品 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/item-drop-secure.html)

### starlight-proxy:
- Cluster Chat Sync: 跨代理服务器实例同步聊天消息 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-proxy/chat-sync.html)
- Cluster Ping Metrics Fix: 监控并广播跨代理的玩家延迟 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-proxy/proxy-ping.html)
- Geyser Skin Remapping: 通过 Geyser 集成重定向基岩版玩家皮肤 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-proxy/geyser-skin-redirect.html)
- BungeeCord Cluster Protection: 防止未经授权的旧版转发连接 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-proxy/legacy-forwarding-protect.html)
- Client Transfer Mod Support: 支持在代理服务器间转移玩家 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-proxy/client-transfer-support.html)

### starlight-chat:
- Rich Text Chat Support: 处理聊天组件和告示牌文本格式化 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-chat/chat-component.html)
- Chat Mention: 处理聊天中的 @提及和玩家补全 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-chat/chat-at.html)

### starlight-sideload:
- inventory-menu: 为玩家提供自定义物品栏 GUI 菜单 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-sideload/inventory-menu.html)
- recipe-loader: 从配置文件加载自定义合成配方 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-sideload/recipe-loader.html)
- resource-pack-loader: 通过 HTTP 服务器向玩家提供资源包 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-sideload/resource-pack-loader.html)

### starlight-display:
- Player Title: 在玩家名称前显示头衔 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/player-name-header.html)
- Custom Welcome Message: 玩家首次加入时显示欢迎信息 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/welcome-message.html)
- Dropped Item Highlight Information: 掉落在地面时显示物品信息 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/drop-item-info.html)
- Custom Scoreboard Information: 为玩家渲染自定义计分板 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/custom-scoreboard.html)
- TAB Menu Information: 提供 TAB 列表显示 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/tab-menu.html)
- Chat Announcements: 定时向玩家广播公告 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/chat-announce.html)
- Chat Line Formatting: 重新格式化聊天消息 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/chat-format.html)
- AFK Detection: 玩家进入/离开挂机状态时广播通知 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/afk.html)
- Custom MOTD Information: 自定义服务器列表消息 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/custom-motd.html)
- Custom Death Message Format: 格式化玩家死亡消息 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/custom-death-message.html)
- WorldEdit Selection Renderer: 可视化渲染玩家的 WorldEdit 选区 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/we-session-renderer.html)
- Player Join Notification: 玩家加入/离开时显示提示 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/player-join-message.html)
- Custom Kick Message: 自定义踢出和封禁消息 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/custom-kick-message.html)
- Action Bar HUD Information Display: 在快捷栏位置创建 HUD 显示 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/action-bar-hud.html)

### starlight-utilities:
- Hitokoto: 从 hitokoto API 获取随机励志语句 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/hitokoto.html)
- tick-manager: 冻结、解冻和步进服务器刻
- Free Camera: 允许玩家将摄像机从身体上分离 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/freecam.html)
- Flight Control Commands: 控制玩家飞行速度和开关 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/fly-command.html)
- Block Update Locker: 锁定活塞、红石等方块更新 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/block-update-locker.html)
- Particle Text Rendering: 使用粒子效果在世界中渲染文字 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/particle-font.html)
- Menu Item Trigger: 提供用于快速打开菜单的物品 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/menu-item.html)
- Client Environment Settings: 允许玩家设置本地天气和时间 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/client-environment-setting.html)
- Position Lock Command: 防止被锁定的玩家移动 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/position-lock.html)
- inventory-profile: 加载和检查玩家物品栏 NBT 数据 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/inventory-profile.html)
- Custom View Distance: 根据玩家数量动态调整服务端视野距离 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/dynamic-view-distance.html)
- Calculator: 通过聊天指令计算数学表达式 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/calculator.html)
- Surrounding Block Refresh: 刷新玩家周围的方块和区块 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/surrounding-refresh.html)
- Custom Camera Path: 沿预设路径移动玩家镜头 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/camera-movement.html)
- Modern Minecart Movement Compatibility: 为旧版客户端提供新版协议信息 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/modern-minecart-sync.html)
- Player Ping Query: 显示玩家的延迟/Ping 信息 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/player-ping-command.html)
- Position Alignment Command: 将玩家位置对齐到最近的方块中心 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/position-align.html)

### starlight-management:
- Maintenance Mode: 开启维护模式以限制玩家进入 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/maintenance.html)
- Chat Report: 通过哈希验证处理聊天举报 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/chat-report.html)
- Ban Commands: 管理基于时长的玩家封禁 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/ban.html)
- Chat Mute: 管理基于时长的玩家禁言 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/mute.html)
- Server Information Commands: 显示服务器信息和性能统计 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/server-info.html)
- Auto-Kick on Reload: 服务器重载时踢出所有玩家以防问题 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/kick-on-reload.html)
- Automatic Garbage Collection: 定时运行垃圾回收释放内存 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/vm-garbage-cleaner.html)
- Command-Line Plugin Manager: 提供插件列表和开关等管理指令 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/plugin-manager-command.html)
- Chat Filter: 过滤聊天和告示牌中的不当词汇 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/chat-filter.html)
- TPS Visual Bar: 通过 Boss 血条显示 TPS 和 MSPT [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/tps-bar.html)

### starlight-console:
- [BETA] Custom Log Format: 配置自定义日志格式模式 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-console/custom-log-format.html)
- Stop Confirmation: 关闭服务器前要求确认 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-console/stop-confirm.html)
- Console Proxy Execution: 从服务端控制台执行指令 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-console/console-execute.html)
- [BETA] Log Color Patch: 使用 ANSI 颜色修复日志输出 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-console/log-color-patch.html)
- Console Cleaner: 清除控制台屏幕 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-console/clear-console.html)

### starlight-core:
- [Core] Custom Plugin Language Loader: 从外部 zip 档案加载第三方语言资源包 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/custom-language-pack-loader.html)
- [Core] PlaceHolderAPI Interaction Support: 提供占位符 API 支持 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/papi-support.html)
- [Core] Player View Customization : 自定义玩家视角和 UI 设置 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/player-view-customization.html)
- [Core] Installation Integrity Check: 验证插件安装并检测配置问题 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/installation-check.html)
- [Core] Modrinth Update Service: 在 Modrinth 上检查插件更新 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/modrinth-version-check.html)
- [Core] Platform Difference Patcher: 为特定平台提供修复 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/platform-patcher.html)
- [Core] ProtocolLib Platform Proxy: 通过 ProtocolLib 实现更兼容的消息发送 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/protocol-lib-injector.html)
- [Core] Update Log Viewer: 向玩家展示版本更新日志 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/version-log-viewer.html)

### starlight-worldguard:
- wg-region-hud: 创建显示 WorldGuard 领地信息的 HUD [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-worldguard/wg-region-hud.html)
- wg-custom-name: 允许为 WorldGuard 区域设置自定义显示名称 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-worldguard/wg-custom-name.html)
- wg-we-check: 验证 WorldEdit 编辑是否在领地权限范围内 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-worldguard/wg-we-check.html)
- wg-claim: 提供 WorldGuard 区域认领和放弃指令 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-worldguard/wg-claim.html)

### starlight-music:
- Music Player: 在服务器上为玩家播放自定义音乐 [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-music/music-player.html)

</details>



## Compatibility

> **强烈建议**使用 Paper 系服务端以启用全部功能。
> 在其他平台上也能工作，但谁知道会发生什么呢？（毕竟懒鬼 GrassBlock2022 从没测试过）

> 版本页面显示的版本仅为占位，所有版本均可在 [1.13-1.20] 范围内工作。

| Platform | Features | Interactive Text | Support | Description                    |
|----------|----------|------------------|---------|--------------------------------|
| Paper    | 完整     | 完整             | 完整    | 推荐                           |
| Spigot   | 大部分   | 完整             | 仅修Bug |                                |
| Bukkit   | 少量     | 无               | 测试    |                                |
| Folia    | 大部分   | 完整             | 仅修Bug | 不支持热重载                    |
| Mohist   | 少量     | 无               | 测试    | UnexpectedBlockChange 不同步   |

## Used Third-party libraries:

| Dependency                                          | Scope         | Usage                     |
|-----------------------------------------------------|---------------|---------------------------|
| com.sk89q.worldedit:worldedit-bukkit:7.2.0-SNAPSHOT | reference     | 插件相关扩展               |
| com.sk89q.worldedit:worldedit-core:7.2.0-SNAPSHOT   | reference     | 插件相关扩展               |
| me.clip:placeholderapi:2.11.6                       | reference     | 占位符支持                 |
| org.ahocorasick:ahocorasick:0.6.3                   | downloaded    | 聊天过滤算法               |
| net.bytebuddy:byte-buddy:1.17.8                     | downloaded    | 字节码扫描                 |
| net.kyori:adventure-api:4.17.0                      | auto-complete | Adventure API              |
| net.kyori:adventure-text-serializer-gson:4.17.0     | auto-complete | Adventure 序列化           |
| net.kyori:adventure-text-serializer-legacy:4.17.0   | auto-complete | Adventure 序列化           |
| net.kyori:adventure-text-serializer-plain:4.17.0    | auto-complete | Adventure 序列化           |
| net.kyori:adventure-text-minimessage:4.17.0         | auto-complete | MiniMessage 支持           |
| com.h2database:h2:2.3.232                           | reference     | 数据库支持                 |
| com.baomidou:mybatis-plus:3.5.15                    | reference     | ORM 框架集成               |
| io.netty:netty-codec-http:4.1.128.Final             | reference     | HTTP 服务                  |
| adventure-platform-api-4.4.1                        | packaged      | Adventure 平台桥接         |
| adventure-platform-bukkit-4.4.1                     | packaged      | Adventure Bukkit 支持      |
| adventure-platform-facet-4.4.1                      | packaged      | Adventure 平台切面         |
| :lib-starlight-shared                               | packaged      | 共享内部库                 |
| me.gb2022.commons:commons-nbt                       | packaged      | NBT 工具                   |
| me.gb2022.commons:commons-math                      | packaged      | 数学工具                   |
| me.gb2022.commons:commons-container                 | packaged      | 容器工具                   |
| me.gb2022.commons:commons-general                   | packaged      | 通用工具                   |
| me.gb2022.commons:commons-event                     | packaged      | 事件框架                   |
| me.gb2022.commons:commons-reflection                | packaged      | 反射工具                   |
| me.gb2022.commons:commons-compatibility             | packaged      | 兼容层                     |
| me.gb2022.apm:apm-remote                            | packaged      | APM 远程模块               |
| me.gb2022.apm:apm-plugin                            | packaged      | APM 插件模块               |
| org.atcraftmc.qlib:qlib-bukkit                      | packaged      | QLib Bukkit 集成           |
| me.gb2022:gluon-main                                | packaged      | Gluon 框架运行时           |
| me.gb2022.pluginsX-lib                              | packaged      | 内部插件库                 |

## FAQ

### What relationship does it have to that `Starlight` mod?

基本没有 :D<br/>
Starlight 模组与此制品功能完全不同，它重写了光照引擎，仅提供优化。

### Why I can't see any changes after installing `starlight-core.jar`?

兄弟，我能说什么？看看 **Installation** 部分吧 :D

### Why it is renamed?

这个项目最初叫做 `quark-plugin`。为了避免和那个大家都爱的模组撞名，我决定改名。

但原因不仅如此。这个项目实际上是在很久很久很久以前从一个小插件开始的。
最糟糕的是，我刚开始这个项目时，对 Java 实际上**毫无**理解。

所以现在，当这个插件获得更多认可时，从零开始重构**整个**架构以确保可扩展性是非常重要的。

### What will happen since this project is a continuation?

不幸的是，为了彻底改造架构，我**不得不**牺牲很多，尤其是兼容性。
从下一个版本开始，本项目将进入 Alpha 阶段。插件将使用完全不同的命名空间和 ID。

一旦重构完成，将添加自动迁移代码，允许您从旧版插件数据文件夹升级**玩家数据和模块数据**，新版 Starlight 插件可以与旧版共存。

### Help us test

Alpha 阶段伴随着不稳定性，现在正是您提供帮助的时候。请将任何 Bug 或功能请求提交到上面的 GitHub 链接，非常感谢！

### Working with quark-plugin

新版 Starlight 内核现在以不同的命名空间注册内容，但功能冲突仍然存在。您需要手动禁用旧版插件中与 Starlight 功能重复的模块。

### Upgrading and migration

不幸的是，由于大部分 ID 和值已更改，无法迁移旧的语言或配置文件，所以准备就绪后请重新配置。

核心版本 25.3.10 之后，我们提供了数据迁移功能。您可以使用指令：

```
/starlight upgrade-data [category]
```

将您想要的数据从旧版 quark-plugin 迁移过来。

目前支持导航点数据、音乐文件和禁言数据。如果有其他需求，请在 issue 中提出。

### Why I can't disable some module?

部分模块（主要在 `starlight-core` 中）提供基础功能或被其他系统依赖。
启用它们不会消耗任何资源。

### Why I can't see some of the features after downloading this?

有两种可能。

#### 1. Some features are not automatically enabled.

这是手动定义的，因为我们不希望在安装**整个**整合包后它们全部加载。
例如，日志颜色修复模块在某些服务器上运行良好，但在其他服务器上会崩溃。
很难说原因，因为它像是注入式工作，且没有证据指向平台 API 问题。
解决方法是：在您了解后果后，筛选正确的模块 ID 并手动启用。

#### 2. Some features require certain platform.

基于 Bukkit 的服务端有多个分支，大多数在技术上是不同的。
例如，基于 Folia 的服务端目前没有计分板 API，
而混合端（将 Forge/Fabric 与 Bukkit 结合）有着糟糕（并非不好）的类加载设计。
当我们的平台缺少所需 API 时，我们无法期望某些功能能够正常工作。

<hr/>
<div align="center">

#### Starlight -「星辰」

由 GrassBlock2022 发起，@ATCraftMC 2020-2024 拥有
</div>
