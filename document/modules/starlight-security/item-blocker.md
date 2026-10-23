# 物品行为拦截 <Badge>starlight-security:item-blocker</Badge>

按物品 ID 匹配规则，对使用行为发出提示、取消操作或移除物品。

## 基本信息

- 命名空间id: `starlight-security:item-blocker`
- 版本: `1.2.2`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

本模块取代旧的 `item-defender`。当前检测物品消耗、交互、方块放置和玩家直接攻击；攻击检测使用玩家主手物品。不会定期扫描背包，也没有仅因持有物品而触发的检测。物品按 Bukkit 材质键（如 `minecraft:tnt`）精确匹配，模组物品是否可匹配取决于服务端暴露的材质键。

具有 `starlight.security.item.bypass` 权限的玩家直接跳过检查。每个匹配规则都会执行，因此同一物品可命中多条规则。

## 可配置项目

`config/values/starlight-security.yml` 的 `config.item-blocker` 中保留以下模板字段：

| 配置项 | 模板默认值 | 当前行为 |
|----|----|----|
| `broadcast` | `false` | 是否向管理员广播 `WARNING` / `ILLEGAL` 提示 |
| `record` | `true` | 当前实现未读取此开关；不要依赖它控制记录 |
| `op-ignore` | `true` | 当前实现未读取此开关；实际豁免由权限判断决定 |

拦截规则在插件数据目录的 `config/item-blocker-rules.yml` 中，保留根节点 `item-blocker-rules:`：

```yaml
item-blocker-rules:
  block-tnt:
    name: "TNT"
    level: WARNING
    cancel-action: true
    remove-item: false
    filter-id:
      - "minecraft:tnt"
```

| 规则字段 | 缺省值 | 说明 |
|----|----|----|
| `name` | 规则 ID | 规则显示名称；目前未用于提示文本 |
| `level` | `WARNING` | `WARNING` / `ILLEGAL`（区分大小写）决定提示与广播类型 |
| `cancel-action` | `false` | 取消命中的事件 |
| `remove-item` | `false` | 从玩家背包移除本次匹配的物品堆；不代表清空所有同类物品 |
| `filter-id` | 空列表 | 完整物品 ID 列表；不支持通配符或正则表达式 |

`level` 与取消、移除是独立设置：`ILLEGAL` 不会自动没收物品。模块还会发送 `item:access` 本地插件消息。

升级时将模块开关和配置从 `item-defender` 改为 `item-blocker`，并将旧 `illegal-list`、`warning-list` 改写为上述规则。当前代码未提供旧列表的自动迁移。注意核对默认规则中的物品 ID；例如岩浆桶应填写 `minecraft:lava_bucket`。

## 命令

本模块无独立命令。
