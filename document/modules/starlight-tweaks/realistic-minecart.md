# 真实矿车 <Badge>starlight-tweaks:realistic-minecart</Badge>

更加真实的矿车物理系统——支持加速、减速、重力模拟和速度控制。

## 基本信息

- 命名空间id: `starlight-tweaks:realistic-minecart`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

该模块为矿车引入了更真实的物理模拟，包括推力加速、阻力减速和重力影响。矿车具有不同的推力档位（-4 到 4），每个档位有不同的加速度值。矿车行驶时会显示速度、加速度等信息在操作界面（ActionBar）上。支持自动对齐轨道和限速机制，当速度超过安全值时自动应用刹车。

## 可配置项目

| 配置项 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| `ui` | `str` | `'{run-mode}{#white}({msg#level}{level}) {#yellow}\|{#white} {msg#speed}{speed} {#yellow}\|{#white} {msg#acceleration}{acceleration}m/s^2'` | 矿车操作界面的显示格式 |
| `max-speed` | `double` | `1.5` | 矿车最高速度 |
| `safety-speed` | `double` | `0.5` | 安全速度阈值，超过后自动刹车 |
| `auto-align` | `bool` | `false` | 是否自动对齐轨道 |
| `thrust--4-acceleration` | `double` | `-0.5` | 推力档位 -4 的加速度 |
| `thrust--3-acceleration` | `double` | `-0.32` | 推力档位 -3 的加速度 |
| `thrust--2-acceleration` | `double` | `-0.29` | 推力档位 -2 的加速度 |
| `thrust--1-acceleration` | `double` | `-0.08` | 推力档位 -1 的加速度 |
| `thrust-0-acceleration` | `double` | `0` | 推力档位 0 的加速度（惰行） |
| `thrust-1-acceleration` | `double` | `0.08` | 推力档位 1 的加速度 |
| `thrust-2-acceleration` | `double` | `0.16` | 推力档位 2 的加速度 |
| `thrust-3-acceleration` | `double` | `0.27` | 推力档位 3 的加速度 |
| `thrust-4-acceleration` | `double` | `0.32` | 推力档位 4 的加速度 |

## 命令

无。
