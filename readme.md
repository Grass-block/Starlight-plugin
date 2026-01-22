<div align="center" id="readme-top">

<img alt="logo" width="160" style="border-radius: 1rem" src="https://raw.githubusercontent.com/Grass-block/Starlight/refs/heads/main/starlight-logo.png">

<h2 align="center">Starlight -「星辰」</h2>

An 'Atomic' designed server plugin aimed to cover anything you need.

[Explore docs](https://wiki.atforever.world/quark/) | [Report issue](https://github.com/Grass-block/Quark-Plugin/issues)

![MCVersion](https://img.shields.io/badge/minecraft-1.17.1_--_1.20.4-3366CC?style=for-the-badge&logoColor=blue&labelColor=29355F)
![Java17+](https://img.shields.io/badge/java-17+-009B98?style=for-the-badge&logoColor=blue&labelColor=29355F)
![MohistCompat](https://img.shields.io/badge/Mohist-Compatible-AD3333?style=for-the-badge&logoColor=blue&labelColor=29355F)

</div>

**This project is still under development and inconsistency.**

> **This project is a continuation of [quark-plugin](https://modrinth.com/plugin/quark-plugin/).<br>**
> for more information and legacy downloads, please go there, also.
> For data upgrade and migration, see content below.

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

<summary>starlight-core.jar</summary>

### `starlight-core`: fixes, services and patches [INTERNAL]

- `custom-language-pack-loader`: load third-party language packs.
- `intiallation-check`: warn users if they installed this plugin incorrectly.
- `modrinth-version-check`: modrinth api supported version check system. [BETA]
- `papi-support`: use PlaceHolder API for text formatting, and inject configured vars in PAPI.
- `platform-patcher`: provide multiple fixes for non-standard bukkit platform.
- `protocol-lib-injector`: replace text sending with protocol-lib.
- `version-log-viewer`: allow users to view version log. [BETA]

</details>

<details>

<summary>starlight-basic.jar</summary>

### `starlight-commands`: command shortcuts and additions

- `entity-motion`: provide a command that set the velocity of any entity.
- `command-exec`: provide a simplex version of `/execute`, but works with **any** commands.
- `item-command`: provide a binding to execute command when using specific item.
- `worldedit-commands`: provide multiple shortcut for worldedit commands.(need WorldEdit)

### `starlight-console`: console appearance tweaks and utilities

- `clear-console`: create a command that can clear console.(honestly, i think it's useless)
- `console-execute`: enable user to execute command **as** console.
- `custom-log-format`: customize console style by replacing Log4j config. [EXPERIMENTAL]
- `log-color-patch`: bring ANSI color support on specific platform. [EXPERIMENTAL]
- `stop-confirm`: let you use `/stop confirm` just as what `/reload confirm` do.

### `starlight-display`: create client's visual look, but not only UI.

- `action-bar-hud`: render a HUD displaying basic info on actionbar title position.
- `afk`: just as it's name, announce when player start afk and end afk.
- `chat-format`: re-format chat-line for a more advance look.
- `custom-death-message`: modify death message by adding prefix.
- `custom-motd`: integrated **basic** motd provider.
- `custom-scoreboard`: create a scoreboard information display.
- `drop-item-info`: add label representing type and count on dropped item stacks.
- `hover-display`: just as hologram does.
- `inventory-menu`: creating customized inventory menu by writing yml.
- `player-name-header`: add a **header** before player name, like"[OWNER]Player1145"
- `tab-menu`: customize information displayed on TAB.
- `welcome-message`: send player some test when player first joined server.
- `we-session-renderer`: visualize player's worldedit selection.

### `starlight-management`: let server moderator to manage their server easily.

- `ban`: a more advanced `/ban` command that supports broadcast and temp-ban.
- `chat-filter`: automatically filter bad words in player's chat.
- `chat-report`: a simple chat report system that send triggers when chat is reported.
- `kick-on-reload`: kick all player out of server when server reloading.
- `maintenance`: provide a maintenance mode when only ops and allowed player can join.
- `mute`: mute system.(yep that's it)
- `plugin-manager-command`: integrated plugin management system.
- `server-info`: query server information by a single command.
- `tps-bar`: create a boss bar that displays TPS and MSPT.

### `starlight-security`: server security and protections.

- `advaced-permission-control`: create permission for more world-interaction actions.
- `explosion-defender`: defend explosion easily.
- `ip-defender`: check player's ip-address and do actions.
- `permission-manager`: a simple and easy permission manager.(definitely worse than LuckPerms)

### `starlight-sideload`: sideload some contents without restart.

- `recipe-loader`: load recipe configured by yml.

### `starlight-utilities`: some utilities and oddities.

- `block-update-locker`: lock most block updates, like redstone or water.
- `calculator`: provide a command-line calculator.
- `camera-movement`: simulate camera smooth moves on server side.
- `chat-at`: allow you to @someone or @all.
- `chat-component`: allow you to write text components in chat and commands.
- `dynamic-view-distance`: allow you to customize view distance.
- `hitokoto`: get random beautiful and inspiring sentences online.
- `player-ping-command`: allow player to query their ping with command.
- `player-position-lock`: allow player to **LOCK** their position.
- `position-align`: allow player to align their position and facing.
- `surrounding-refresh`: allow players to forcibly refresh their blocks which could fix fake-blocks issue.
- `tick-manager`: wrap of `/tick` command.

### `starlight-warps`: a warping system support.

- `back-to-death`: allow player to go back to their latest death position.
- `rtp`: randomly teleport player to a safe position.
- `tpa`: allow players to request to teleport to others or reversed.
- `waypoints`: provide a waypoint system for private or public waypoints.

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
