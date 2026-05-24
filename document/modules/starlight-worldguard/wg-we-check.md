# WorldEdit领地检查 <Badge>starlight-worldguard:wg-we-check</Badge>

限制WorldEdit编辑操作仅允许在当前领地范围内进行。

## 基本信息

- 命名空间id: `starlight-worldguard:wg-we-check`
- 版本: `26.5.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

监听WorldEdit的`EditSessionEvent`，在玩家执行编辑操作时检查其所在位置的领地归属。若玩家位于无领地区域，或尝试编辑非自己拥有的领地，则拒绝操作并提示警告（`region-warn`）。

通过`RegionBasedExtent`包装原有的Extent，将编辑范围限制在当前领地边界内，超出部分自动跳过。支持WorldGuard的`__global__`全局区域标识，若全局区域`BUILD`标志为`ALLOW`则放行所有操作。

## 可配置项目

无独立配置项。

## 命令

无。
