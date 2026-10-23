# 自定义MOTD信息 <Badge>starlight-display:custom-motd</Badge>

自定义服务器列表 MOTD 的两行文本和服务器图标。

## 基本信息

- 命名空间id: `starlight-display:custom-motd`
- 版本: `1.0.2`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

处理服务器列表 Ping 和查询 Ping，替换 MOTD 文本与图标。存在 ProtocolLib 时还会注册协议子组件替换 MOTD 文本。模块没有修改在线人数、人数上限或玩家列表的配置。

## 可配置项目

文本来自插件数据目录的 `config/motd.yml`，图标为 `config/motd.png`。图标应符合 Bukkit 服务器图标要求（64×64 PNG）。当前不读取 `line1`、`line2`、`max-player`、`online-player` 或 `icon` 字段。

| 配置路径 | 类型 | 说明 |
|----|----|----|
| `motd.motd-title` | String | MOTD 第一行 |
| `motd.motd-subtitle` | String | MOTD 第二行，前面自动插入换行和样式重置 |
| `motd.<自定义键>` | String / List&lt;String&gt; | 用 `{键名}` 插入文本；列表每次渲染随机取一项。模板中的例子有 `splash`、`feature`、`version`、`tag` |

自定义键替换仅匹配小写英文字母键名。保留最外层 `motd:`。

## 命令

| 命令      | 权限                    | 说明         |
|---------|-----------------------|------------|
| `/motd refresh-text` | `-quark.motd.command` | 从文件重新读取 MOTD 文本 |
| `/motd refresh-icon` | 同上 | 从文件重新读取图标 |
| `/motd text` | 同上 | 预览当前 MOTD 文本 |
