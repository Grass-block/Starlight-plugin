# Agent updates

## 2026-10-23

- 补充 `starlight-chat:ai-player-support` 模块文档，记录 `/i-am-ai`、纯文本系统消息、聊天副本、退出清理和无配置项等行为。
- 将 AI 玩家兼容模块注册到 `BaseBundler.chat()`，并在聊天模块索引中列出。

## 2026-10-16

- Updated repository documentation for 26.6.4 against the complete staged and unstaged Git changes and current Bukkit bundler registrations; no implementation files, Gradle dependencies or build metadata were changed by this documentation task, and no release/build was run.
- Created `document/index.md` with version 26.6.4, configuration conventions, links to all 109 registered Bukkit extension modules, and explicit registration status for retained but unavailable features. Added local homepage links to both project READMEs.
- Added 12 missing module pages: chat-at, chat-component, chat-translator, world-object-filter, out-proxy-info-sync, plugin-backdoor-scanner, item-blocker, quick-deposit, extra-damage, custom-vehicle, ai-command-chat and wg-spawn-tp.
- Updated existing module documentation for current chat rendering/report/filter behavior, WorldEdit command syntax and undo history, hologram storage and cleanup, announcement implementation, MOTD fields, player headers, selection rendering, music sessions, Geyser skin commands, permission configuration, IP detection, sideload paths, waypoint parameters and flight limitations. Corrected seven legacy module namespaces and four WorldGuard module version declarations; marked command-variables and music-game as unregistered.
- Added `document/upgrade-26.6.4.md`, `document/development/05-chat-and-locale.md` and `document/development/06-display-models.md`, and updated the module-development language example. Documented current configuration/implementation mismatches without modifying runtime behavior.
- Verified all 121 Markdown files under `document/` for local link targets and balanced code fences; verified all 109 registered extension modules have matching documentation IDs, versions, default states and homepage links. Documentation whitespace checks passed. Removed temporary audit/generation scripts after verification.

- Added chat-translator translator-not-configured messages and the automatic translation command tip to en_us, fr_fr, ja_jp, ru_ru and zh_tw, following zh_cn text and formatting.

## 2026-10-15

- Fixed Tencent Cloud TC3 Authorization header generation to use the canonical SignedHeaders value, retained implicit Host handling from HttpClient, and converted TC3 request string construction to String.formatted.

## 2026-10-15

- Added localized chat-translator usage tips to en_us, fr_fr, ja_jp, ru_ru and zh_tw, matching the zh_cn entry and gray styling.

## 2026-10-15

- Aligned all non-simplified-Chinese chat-translator labels with zh_cn coloring, placeholder, and spacing conventions while preserving locale-appropriate punctuation.

## 2026-10-15

- Added Tencent Cloud TextTranslate as a ChatTranslator provider, including TC3-HMAC-SHA256 request signing, locale mapping and SecretId/SecretKey/region config entries.

## 2026-10-15

- Changed the new chat-translator configuration comments to English, following the default comment language preference.

- Completed chat-translator config references with the default Google provider, example local DeepLX endpoint, optional empty token and clickable translation suffix.

- Added the chat-translator module name mapping and not-found, translate-origin, translate-target and translate-hint entries to all six starlight-chat locales (zh_cn, zh_tw, en_us, fr_fr, ja_jp, ru_ru), preserving the target-language placeholder.

## 2026-10-14

- Implemented nested GoogleTranslate and DeepLX providers in ChatTranslator using the existing Translator interface, Java HttpClient and Gson. Google uses the keyless web endpoint; DeepLX accepts a full endpoint and optional Bearer token.
- Added automatic source detection, provider-specific Minecraft locale mapping, UTF-8 requests, timeouts, response/status validation and interrupt preservation. Calls are blocking and should run asynchronously.

## 2026-10-11

- Completed `Util.scanForModIds()` in `starlight-bukkit-core`.
- The scanner resolves the server directory as the parent of the current plugin working directory, scans its `mods` directory, and reads mod IDs from Forge/NeoForge `mods.toml`, Fabric `fabric.mod.json`, and legacy Forge `mcmod.info` metadata in JAR files.
- Results are deduplicated, ordered by JAR filename and metadata order, and returned as an unmodifiable set. Invalid or unreadable entries are skipped.

## 2026-10-12

- Added a standalone Blockbench Bedrock model decoder and display-model data records under starlight-bukkit-bundler oddities packages.
- Decoder reads Bedrock geometry cubes and texture references, maps each cube to a block ID from texture metadata, and supports model-level BasePos (pixels) and BaseRot (degrees) overlays.
- Display model can spawn the decoded cuboids as Bukkit BlockDisplay entities.


- Added Minecraft Java-model cube classification: uniform face textures become DirectCube, mixed face textures become FaceBasedCube with per-face UV data. Supports texture references such as #0 from model.json.

