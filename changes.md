## 26.2.6

- Fixed component builder crashing issue(upstream:qlib-core)

## 26.2.5

- [1.20.1] Fixed task tracking issue(upstream:qlib-bukkit)
- Fixed BackToDeath null issue.
- Added smart-cancel(clear blocks) to ExplosionDefender.
- Fixed exploded block id record issue on ExplosionDefender.
- Override-explosion option is now false due to smart-cancel option.
- Music player will now try to bind sound on player.

## 26.2.3

- PlayerKickMessage will no longer be modified.
- Custom item can now be re-rendered while changing locale.
- Player's camera will no longer mounted to minecart direction.
- Local fixed time of player is now actively updated and synced.(need protocol-lib)
- Added Menu-item.

## 26.2.1

- BuildTime and Version are now correct.
- Fixed chat error on reloading.
- Add module `sit-on-player`.
- Add module `player-join-message`
- Add `en_us` translation for `starlight-display`
- Add `en_us` translation for `starlight-core`
- Add `en_us` translation for `starlight-warps`
- Add `en_us` translation for `starlight-tweaks`
- Add `en_us` translation for `starlight-oddities`
- Fixed crashing by duplicate reloading.
- Fixed custom block rendering on pickup.

## 26.2.0

- Fixed library loading exception in some paths.
- Elevator can now recognize legacy blocks.
- Dispenser can now interact correctly.
- Fixed LevelDB loading issue.
- Fixed pack loading on hot reload.

## 26.1.10

- Updated modular framework.
- Added ParticleFontRenderer.
- Added LocalEnvironmentSetting.
- Supported player data import.
- Supported legacy custom block recognition.
- Now provide bundler for all packs.