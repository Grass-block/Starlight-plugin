# 自动化垃圾清理 <Badge>starlight-management:vm-garbage-cleaner</Badge>

定时调用JVM垃圾回收，自动清理内存并广播回收结果。

## 基本信息

- 命名空间id: `starlight-management:vm-garbage-cleaner`
- 版本: `1.3.0`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

VMGarbageCleaner 模块按固定周期调用 `System.gc()` 触发JVM垃圾回收。回收前后分别记录可用内存，计算并展示回收量。支持在回收前后广播提示消息，管理员也可通过指令手动执行垃圾回收。

## 可配置项目

| 配置项 | 类型 | 默认值 | 描述 |
|--------|------|--------|------|
| `vm-garbage-cleaner.broadcast` | Boolean | `false` | 是否向全服广播垃圾回收开始和结束信息 |
| `vm-garbage-cleaner.period` | Integer | `1000` | 自动垃圾回收的执行间隔（单位：tick） |

## 命令

| 命令 | 权限 | 描述 |
|------|------|------|
| `/system-gc` | `starlight.management.gc` | 手动执行一次JVM垃圾回收 |
