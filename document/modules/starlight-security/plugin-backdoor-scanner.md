# 插件字节码扫描 <Badge>starlight-security:plugin-backdoor-scanner</Badge>

检查插件 JAR 中命中预设规则的方法调用，并将结果输出到控制台。

## 基本信息

- 命名空间id: `starlight-security:plugin-backdoor-scanner`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

按命令扫描 `plugins/` 下的指定 JAR 或其中单个 class 条目。启用模块本身不会自动扫描所有插件，也不会阻止插件加载、卸载插件或删除文件。

内置规则包括设置 OP、执行 Bukkit 命令、执行外部进程、脚本求值、加载本地库、反射和动态类加载等调用。控制台输出规则 ID、等级、位置和方法信息。命中仅表示存在相应调用，需要结合插件用途判断；未命中也不能证明插件不存在后门。

## 可配置项目

无独立配置项；扫描规则由 `CommonScanners` 提供。

## 命令

| 命令 | 权限 | 说明 |
|----|----|----|
| `/scan-plugins` | OP（命令注解 `op = true`） | 查看帮助 |
| `/scan-plugins plugin <文件名.jar>` | 同上 | 扫描整个 JAR |
| `/scan-plugins plugin-class <文件名.jar> <路径/类名.class>` | 同上 | 扫描 JAR 内的指定条目，例如 `org/example/Main.class` |
