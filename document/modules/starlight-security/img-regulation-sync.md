# 广电联合封禁同步 <Badge>starlight-security:img-regulation-sync</Badge>

接入 IMG（无线广播电视总台）封禁数据库，在玩家加入时自动查询其 UUID 是否在封禁列表中，触发额外封禁。

## 基本信息

- 命名空间id: `starlight-security:img-regulation-sync`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `否`
- 是否为未完成[beta]阶段: `否`

## 描述

监听 `PlayerExtraBanCheckEvent`，通过 `IMGRegulationService` 查询玩家 UUID 是否存在于广电联合封禁数据库中。若查询到封禁记录，则对玩家设置持续至 9999 年的封禁。

封禁消息会获取对应语言环境的算子名称（`ban-operator`）显示。查询结果会被缓存 5 分钟以减少重复请求。

## 可配置项目

无独立配置项。
