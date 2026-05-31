<div align="center" id="readme-top">

<img alt="logo" width="160" style="border-radius: 1rem" src="https://raw.githubusercontent.com/Grass-block/Starlight/refs/heads/main/starlight-logo.png">

<h2 align="center">Starlight -「星辰」</h2>

An 'Atomic' designed server plugin aimed to cover anything you need.

[Explore docs](https://dev.atcraftmc.cn/starlight/) | [Report issue](https://github.com/Grass-block/Quark-Plugin/issues)

![MCVersion](https://img.shields.io/badge/minecraft-1.17.1_--_1.20.4-3366CC?style=for-the-badge&logoColor=blue&labelColor=29355F)
![Java17+](https://img.shields.io/badge/java-17+-009B98?style=for-the-badge&logoColor=blue&labelColor=29355F)
![MohistCompat](https://img.shields.io/badge/Mohist-Compatible-AD3333?style=for-the-badge&logoColor=blue&labelColor=29355F)

</div>

> **This project is a continuation of [quark-plugin](https://modrinth.com/plugin/quark-plugin/).<br>**
> for more information and legacy downloads, please go there, also.
> For data upgrade and migration, see content below.
> Please join our qq group for better communication：1093970869

## Description

This is an essential plugin suite for Spigot/Paper/Folia/Mohist servers,
including more than 90 modules that provides countless features for servers of any scale!
This plugin also includes several performance enhancements and fixes.
From management to display,game features to security,
these contents will improve your server from every aspect.

### Possible Usages

- create a SMP server.
- create a CMP server with WorldEdit and CoreProtect.
- create a Lobby server with Citizens and sign-in plugins.

All the features are independently and composable.
you can customize its function by enable or disable modules,
add or remove extension packs,
or even make your own pack through sdk pack.

### Goal and reason of this

We are not likely to focus on those massive contents or doing things to extreme on single function.
What we are doing is to create a liquid that fills the unmodified details on the server.
So please do not expect we will do something as a mod——at least not in primary packages.
Starlight only provide a basic and usable implementation, so if you find dedicated plugin to replace part of function
provided, just use them without doubts——They are 100% better.

### Core Features

- MultiPlatform support: supports basically every Bukkit-Implemented Server(see below)
- Fast: initialize on less than 600ms, support command-line hot reload.
- Modularized: all features are separately toggleable.

## DISCLAIMER / 免责声明

本项目中的语言代码仅用于标识语言及本地化内容，以兼容 Minecraft 及相关国际化标准。
部分语言代码可能包含地区标识，其用途仅为区分不同语言习惯与翻译分支，不代表任何政治立场或地区主张。

部分外部服务（如 IP 属地、系统 Locale 或第三方 API）返回的信息由其提供方决定，本项目不对相关数据的准确性或地区划分方式作额外定义。<br>

Language codes used in this project are intended solely for localization and compatibility purposes,
following Minecraft and common internationalization conventions where applicable.
Some language tags may include regional identifiers used only to distinguish language variants or localization differences,
and do not imply any political position or territorial claim.

Certain external information (such as IP geolocation, system locale, or third-party API results) is provided by external services.
This project does not define or endorse any regional classification returned by such services.

## Installation

### 1. Download the bundler pack from page.

### 2. install them.

- Stop server or type `/starlight reload prepare`
- Throw them into your jar.

### 3. reload.

- Start your server or type `/starlight reload action`
- Wait.

### 4. Stuck on library download?

For better performance and smaller jars, starlight will use OTA mode
to download all libraries needed.

You can change maven mirror if you got stuck on library loading in config.yml:

```yaml
config:
  #...
  dependency:
    maven-repo: ALICLOUD
    # accept any maven repo URLS, or:
    #  - 'ALICLOUD' (AliCloud maven central)
    #  - 'HUAWEI' (Huawei maven repo)
    #  - 'TENCENT' (TencentCloud maven repo)
    #  - 'TSINGHUA' (Tsinghua University maven repository)
    #  - 'CENTRAL' (Maven Central repo, Capable for non-China users.)
```

## Contents

> **Basic concepts**
> - Module: The minimum unit of functions this artifact provides.
> - Package: The smallest group of modules, share value configurations and i18n files.
    > A package or module can be enabled or disable by commands.<br>
    > A jar will contain multiple modules.

Click on each tabs and expand them to see what is included.<br>
This readme will become to loooooooooong if I don't do this.

<details>
<summary>Content: </summary>

### starlight-commands:
- Entity Proxy Execute Command: Executes commands as other entities or players [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/command-exec.html)
- Send Message: Sends formatted messages to the sender only [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/self-message.html)
- Item Command Trigger: Binds commands and actions to items [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/item-command.html)
- Wear Hat: Equips held items as wearable head gear [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/hat.html)
- Block Animation: Animates blocks with falling block effects [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/animate-block-command.html)
- WorldEdit Command Supplement: Provides WorldEdit utility brush and editing commands [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/world-edit-commands.html)
- Entity Motion Command: Controls entity motion with velocity commands [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/entity-motion.html)
- Command Tab Completion Fix: Fixes tab completion for server commands [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-commands/command-tab-fix.html)

### starlight-warps:
- Waypoint: Creates and manages teleport waypoints for players [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-warps/waypoint.html)
- Back to Death Point: Teleports players back to their death location [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-warps/back-to-death.html)
- Teleport Request: Handles teleport request and acceptance between players [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-warps/tpa.html)
- Random teleport: Teleports players to a random location in the world [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-warps/rtp.html)

### starlight-security:
- WorldEdit Operation Monitor: Defends against unauthorized WorldEdit operations with confirmation [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/we-operation-defender.html)
- IP Address Detection: Defends against malicious IP connections and provides IP query commands [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/ip-defender.html)
- Explosion Defender: Defends against block and entity explosions with whitelist support [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/explosion-defender.html)
- Advanced Permission Control: Controls advanced player permissions for chat, interact, and break actions [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/advanced-permission-control.html)
- plugin-backdoor-scanner: Scans plugins for malicious backdoor code patterns [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/plugin-backdoor-scanner.html)
- Guest Mode: Restricts actions for guest players in specified worlds [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/guest-mode.html)
- IMG Regulation Sync: Syncs player ban records from IMG regulation service [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/img-regulation-sync.html)
- end-protect: Protects the End dimension from crystal placement [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/end-protect.html)
- Simple Permission Control: Manages player permissions with JDBC-backed storage and commands [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-security/permission-manager.html)

### starlight-oddities:
- Elevator Block: Create a OpenBlock mod styled elevator block. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-oddities/elevator.html)

### starlight-tweaks:
- Sit on players: Enable player to sit on others by right-click. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/sit-on-player.html)
- Portable Functional Blocks: Opens functional block GUIs from hand-held items [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/portable-functional-blocks.html)
- Stair Seat: Allows players to sit on stair blocks [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/stair-seat.html)
- Realistic Minecart: Enhances minecart behavior for realistic physics and control [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/realistic-minecart.html)
- Quick Open Shulker Box: Opens shulker boxes directly from the inventory [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/portable-shulker-box.html)
- Crop Click Harvest: Allows harvesting crops by right-clicking them [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/crop-click-harvest.html)
- Double Door Synchronization: Synchronizes adjacent wooden doors to open together [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/double-door-sync.html)
- Dispenser Interaction Imporovement: Enhances dispenser interaction with custom behavior [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/dispenser-interaction.html)
- Realistic Sleep: Requires most players to sleep for night skip [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/realistic-sleep.html)
- Vein Miner: Mines entire connected ore veins with one break [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/vein-miner.html)
- Drop Prevention: Prevents accidental dropping of secured items [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-tweaks/item-drop-secure.html)

### starlight-proxy:
- Cluster Chat Sync: Syncs chat messages across proxy server instances [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-proxy/chat-sync.html)
- Cluster Ping Metrics Fix: Monitors and broadcasts player ping across proxy [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-proxy/proxy-ping.html)
- Geyser Skin Remapping: Redirects Bedrock player skins via Geyser integration [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-proxy/geyser-skin-redirect.html)
- BungeeCord Cluster Protection: Protects against unauthorized legacy forwarding connections [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-proxy/legacy-forwarding-protect.html)
- Client Transfer Mod Support: Supports transferring players between proxy servers [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-proxy/client-transfer-support.html)

### starlight-chat:
- Rich Text Chat Support: Processes chat components and sign text formatting [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-chat/chat-component.html)
- Chat Mention: Handles @mentions and player completion in chat [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-chat/chat-at.html)

### starlight-sideload:
- inventory-menu: Provides custom inventory GUI menus for players [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-sideload/inventory-menu.html)
- recipe-loader: Loads custom crafting recipes from configuration files [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-sideload/recipe-loader.html)
- resource-pack-loader: Serves resource packs to players via HTTP server [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-sideload/resource-pack-loader.html)

### starlight-display:
- Player Title: Provide a Header before player's name. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/player-name-header.html)
- Custom Welcome Message: Present a welcome message when player join a server at first time. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/welcome-message.html)
- Dropped Item Highlight Information: Shows item info when dropped on ground [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/drop-item-info.html)
- Custom Scoreboard Information: Renders a custom scoreboard for players [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/custom-scoreboard.html)
- TAB Menu Information: Provide a tab board display. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/tab-menu.html)
- Chat Announcements: Broadcasts periodic announcements to players [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/chat-announce.html)
- Chat Line Formatting: Re-format your chat line. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/chat-format.html)
- AFK Detection: Announce when player starting AFK. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/afk.html)
- Custom MOTD Information: Customizes server list message of the day [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/custom-motd.html)
- Custom Death Message Format: Re-format your death message. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/custom-death-message.html)
- WorldEdit Selection Renderer: Renders WorldEdit selection session visuals [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/we-session-renderer.html)
- Player Join Notification: Display message on player join and quit. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/player-join-message.html)
- Custom Kick Message: Customizes kick and ban messages [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/custom-kick-message.html)
- Action Bar HUD Information Display: Create a HUD display on actionbar title. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-display/action-bar-hud.html)

### starlight-utilities:
- Hitokoto: Displays random inspirational quotes from hitokoto API [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/hitokoto.html)
- tick-manager: Freezes, unfreezes, and steps the server tick
- Free Camera: Allows players to detach camera from their body [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/freecam.html)
- Flight Control Commands: Controls player flight speed and toggling [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/fly-command.html)
- Block Update Locker: Locks block updates such as pistons and redstone [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/block-update-locker.html)
- Particle Text Rendering: Renders text in the world using particle effects [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/particle-font.html)
- Menu Item Trigger: provides an item for accessing menu quicker [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/menu-item.html)
- Client Environment Settings: Allows players to set local weather and time [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/client-environment-setting.html)
- Position Lock Command: Prevents locked players from moving from their position [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/position-lock.html)
- inventory-profile: Loads and examines player inventory NBT data [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/inventory-profile.html)
- Custom View Distance: Dynamically adjusts server view distance based on player count [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/dynamic-view-distance.html)
- Calculator: Evaluates mathematical expressions via chat command [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/calculator.html)
- Surrounding Block Refresh: Refreshes surrounding blocks and chunks for a player [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/surrounding-refresh.html)
- Custom Camera Path: Moves player camera along a predefined path [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/camera-movement.html)
- Modern Minecart Movement Compatibility: provide information to legacy clients of newer protocol. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/modern-minecart-sync.html)
- Player Ping Query: Shows player latency/ping information [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/player-ping-command.html)
- Position Alignment Command: Aligns player position to the nearest block center [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-utilities/position-align.html)

### starlight-management:
- Maintenance Mode: Enables maintenance mode to restrict player access [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/maintenance.html)
- Chat Report: Handles chat reporting with hash verification [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/chat-report.html)
- Ban Commands: Manages player bans with time-based durations [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/ban.html)
- Chat Mute: Manages player muting with time-based durations [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/mute.html)
- Server Information Commands: Displays server information and performance statistics [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/server-info.html)
- Auto-Kick on Reload: Kicks players on server reload to prevent issues [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/kick-on-reload.html)
- Automatic Garbage Collection: Periodically runs garbage collection to free memory [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/vm-garbage-cleaner.html)
- Command-Line Plugin Manager: Provides plugin management commands like list and toggle [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/plugin-manager-command.html)
- Chat Filter: Filter player's bad words in chat and signs. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/chat-filter.html)
- TPS Visual Bar: Shows TPS and MSPT via a boss bar overlay [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-management/tps-bar.html)

### starlight-console:
- [BETA] Custom Log Format: Configures custom log format patterns [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-console/custom-log-format.html)
- Stop Confirmation: Requires confirmation before server stop [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-console/stop-confirm.html)
- Console Proxy Execution: Executes commands from the server console [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-console/console-execute.html)
- [BETA] Log Color Patch: Patches log output with ANSI colors [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-console/log-color-patch.html)
- Console Cleaner: Clears the console screen [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-console/clear-console.html)

### starlight-core:
- [Core] Custom Plugin Language Loader: Load custom third-party language resource packs from external zip archives. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/custom-language-pack-loader.html)
- [Core] PlaceHolderAPI Interaction Support: Provide papi support. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/papi-support.html)
- [Core] Player View Customization : Customizes the player view and UI settings [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/player-view-customization.html)
- [Core] Installation Integrity Check: Verifies plugin installation and detects configuration issues [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/installation-check.html)
- [Core] Modrinth Update Service: Checks for plugin updates on Modrinth [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/modrinth-version-check.html)
- [Core] Platform Difference Patcher: Provide fixes for certain platform. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/platform-patcher.html)
- [Core] ProtocolLib Platform Proxy: Create more compatible message sending via ProtocolLib. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/protocol-lib-injector.html)
- [Core] Update Log Viewer: Displays version update logs to players [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-core/version-log-viewer.html)

### starlight-worldguard:
- wg-region-hud: Create an HUD displaying WorldGuard region info. [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-worldguard/wg-region-hud.html)
- wg-custom-name: Allows custom display names for WorldGuard regions [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-worldguard/wg-custom-name.html)
- wg-we-check: Validates WorldEdit edits against WorldGuard region permissions [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-worldguard/wg-we-check.html)
- wg-claim: Provides WorldGuard region claiming and unclaiming commands [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-worldguard/wg-claim.html)

### starlight-music:
- Music Player: Plays custom music tracks for players on the server [doc↗](https://dev.atcraftmc.cn/starlight/content/starlight-music/music-player.html)

</details>



## Compatibility

> We STRONGLY recommend you to use paper based server to enable full features.
> it could work on other platform, but who knows what will happen? (since lazy GrassBlock2022 never test them)

> Versions showed in versions page are just for placeholder. all versions can work in [1.13-1.20]

| Platform | Features | Interactive Text | Support | Description                    |
|----------|----------|------------------|---------|--------------------------------|
| Paper    | Full     | Full             | Full    | Recommended                    |
| Spigot   | Most     | Full             | BugFix  |                                |
| Bukkit   | Few      | No               | Test    |                                |
| Folia    | Most     | Full             | BugFix  | No hot reload                  |
| Mohist   | Few      | No               | Test    | UnexpectedBlockChange not sync |

## Used Third-party libraries:

| Dependency                                          | Scope         | Usage                     |
|-----------------------------------------------------|---------------|---------------------------|
| com.sk89q.worldedit:worldedit-bukkit:7.2.0-SNAPSHOT | reference     | plugin-related-extension  |
| com.sk89q.worldedit:worldedit-core:7.2.0-SNAPSHOT   | reference     | plugin-related-extension  |
| me.clip:placeholderapi:2.11.6                       | reference     | placeholder support       |
| org.ahocorasick:ahocorasick:0.6.3                   | downloaded    | chat-filtering algorithm  |
| net.bytebuddy:byte-buddy:1.17.8                     | downloaded    | bytecode scanning         |
| net.kyori:adventure-api:4.17.0                      | auto-complete | Adventure API             |
| net.kyori:adventure-text-serializer-gson:4.17.0     | auto-complete | Adventure serializer      |
| net.kyori:adventure-text-serializer-legacy:4.17.0   | auto-complete | Adventure serializer      |
| net.kyori:adventure-text-serializer-plain:4.17.0    | auto-complete | Adventure serializer      |
| net.kyori:adventure-text-minimessage:4.17.0         | auto-complete | MiniMessage support       |
| com.h2database:h2:2.3.232                           | reference     | Database support          |
| com.baomidou:mybatis-plus:3.5.15                    | reference     | ORM framework integration |
| io.netty:netty-codec-http:4.1.128.Final             | reference     | HTTP service              |
| adventure-platform-api-4.4.1                        | packaged      | Adventure platform bridge |
| adventure-platform-bukkit-4.4.1                     | packaged      | Adventure Bukkit support  |
| adventure-platform-facet-4.4.1                      | packaged      | Adventure platform facet  |
| :lib-starlight-shared                               | packaged      | Shared internal library   |
| me.gb2022.commons:commons-nbt                       | packaged      | NBT utilities             |
| me.gb2022.commons:commons-math                      | packaged      | Math utilities            |
| me.gb2022.commons:commons-container                 | packaged      | Container utilities       |
| me.gb2022.commons:commons-general                   | packaged      | General utilities         |
| me.gb2022.commons:commons-event                     | packaged      | Event framework           |
| me.gb2022.commons:commons-reflection                | packaged      | Reflection utilities      |
| me.gb2022.commons:commons-compatibility             | packaged      | Compatibility layer       |
| me.gb2022.apm:apm-remote                            | packaged      | APM remote module         |
| me.gb2022.apm:apm-plugin                            | packaged      | APM plugin module         |
| org.atcraftmc.qlib:qlib-bukkit                      | packaged      | QLib Bukkit integration   |
| me.gb2022:gluon-main                                | packaged      | Gluon framework runtime   |
| me.gb2022.pluginsX-lib                              | packaged      | Internal plugin library   |

## FAQ

### What relationship does it have to that `Starlight` mod?

Basically, nope :D<br/>
Starlight mod has a totally different function from this artifact.It rewrites light engine and simply provide optimizes.

### Why I can't see any changes after installing `starlight-core.jar`?

Man! What can I say? see **Installation** section :D

### Why it is renamed?

This project is originally name `quark-plugin`. To avoid being collided with that mod——which everybody loves it, I
decide to change its name.

But the reason is not only for this. This project actully starts from long long long long ago, from a small plugin. The
most bad thing is that when I started this project, I actually have **NO**
understandings to java.

So now, when this plugin is getting more appreciation, start off from the blank and refactor the WHOLE archtecture to
ensure extandability is important.

### What will happen since this project is a continuation?

Unfortunately, to wholly frick the architecture away, I HAVE to sacrifice A lot, especially, compatibility.
from the following version, This project will enter alpha stages.The plugin will use a completely different namespace
and ID.

As soon as the refactor done, the auto-migaration code will be added, to allow you to upgrade **Player Data and module
data** from old plugin data folder, and the new starlight plugin can work with the old one together.

### Help us test

Alpha stage goes with inconsistentcy, so it's now for you to offer help. Please submit any bugs or feature requests to
github link above, thank you sooooo much.

### Working with quark-plugin

The new starlight kernal now register things in a different namespace, but feature conflicts still exists. You have to
manually disable modules that has duplicated feature with starlight in your older plugin.

### Upgrading and migration

Unfortunately, since most of ids and values are changed, it's not possible to move or migrate legacy language or config
file, so please re-configure them once it's ready.

After core version 25.3.10, we provide data migration. You could use command:

```
/starlight upgrade-data [category]
```

to migrate data u would like to in legacy quark-plugin.

Currently, we support waypoint data, music files and mute data. For any other request, leave it in issue.

### Why I can't disable some module?

Some modules(mostly in `starlight-core`) provides basic features or relied by other systems.
Enable them will not cost any resources.

### Why I can't see some of the features after downloading this?

There are two possibilities.

#### 1. Some features are not automatically enabled.

This is manually defined, since we won't expect them to load after we install the WHOLE bundler.
For example, the log color patching module work fine on some server, but crash on others.
It's hard to say why since it works like injecting, and there is no evidence points to platform api issue.
To resolve this, after you KNOW what will happen, filter the correct module ID and enable it manually.

#### 2. Some features require certain platform.

Bukkit-based server has multiple branches, and most of them are technically different.
For example, Folia based server does NOT have scoreboard API(currently),
and a MIXED server(combine forge/fabric with bukkit) has a horrible(not bad) class loading design.
We just can't expect some features to work when our platform is missing needed API.

<hr/>
<div align="center">

#### Starlight -「星辰」

A open project by GrassBlock2022, owning by @ATCraftMC 2020-2024
</div>
