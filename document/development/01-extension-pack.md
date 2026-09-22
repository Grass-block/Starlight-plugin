# 1. 导入依赖并创建扩展包

Starlight 的扩展包（Pack）本质上是一个独立的 Bukkit 插件，通过 `MultiPackageProvider` 接入 Starlight 核心。本页覆盖：导入依赖、创建扩展包、元数据（`plugin.yml` 与 `product-meta.json`）。

## 1.1 导入依赖

从 Release 页面下载 `starlight-bukkit-core.jar`，作为 `compileOnly` 依赖加入你的工程：

```groovy
dependencies {
    compileOnly files("libs/starlight-bukkit-core.jar")
}
```

引入后，核心服务与框架 API 即可用（`org.atcraftmc.starlight.framework.*`、`org.atcraftmc.starlight.core.*`，以及 gluon / qlib 等）。

> 运行时这些类由服务器上安装的 `starlight-core` 插件提供，因此打包时**不要**把它们打进你的插件（`compileOnly`）。

## 1.2 创建扩展包

新建一个继承 `MultiPackageProvider` 的类：

```java
package com.example.mypack;

import org.atcraftmc.starlight.framework.pack.MultiPackageProvider;

public final class MyPack extends MultiPackageProvider {
    // 在这里声明静态 @ApplicationPackageProvider 方法，见「2. 注册内容」
}
```

`MultiPackageProvider` 已替你实现插件生命周期：

- `onLoad`：扫描早期加载（`@EarlyLoading`）。
- `onEnable`：准备库环境、读取元数据、调用 `createPackages()` 注册所有 `@ApplicationPackageProvider` 内容。
- `onDisable`：卸载本包注册的所有 package。

> 类形式**不需要**在类上添加 `@SLPackageProvider`。

## 1.3 元数据

### plugin.yml

```yaml
name: my-pack
version: '${version}'
main: com.example.mypack.MyPack
api-version: 1.16
folia-supported: true
load: POSTWORLD
depend: [ "starlight-core" ]
softdepend: [ "WorldEdit", "ProtocolLib" ]
authors: [ "you" ]
website: https://example.com
prefix: MyPack
description: |
  你的扩展包描述。
```

| 字段 | 说明 |
|------|------|
| `name` | 插件名，**决定 `product-meta.json` 的文件名** |
| `main` | 扩展包类（`MultiPackageProvider` 子类） |
| `api-version` | Bukkit API 版本 |
| `depend` | 必须包含 `starlight-core`，保证核心先加载 |
| `softdepend` | 可选的软依赖插件 |

### product-meta.json

文件名必须是 **`<plugin.yml 的 name>.product-meta.json`**，放在 jar 根目录（即 `src/main/resources/`）。

`ProductMetadata.createFromResource` 通过 `plugin.name() + ".product-meta.json"` 读取该文件，缺失会直接抛异常导致插件加载失败。

```json
{
  "build-time": "${build_time}",
  "version": "${version}",
  "api-version": 30,
  "libraries": [
    "org.ahocorasick:ahocorasick:0.6.3"
  ]
}
```

| 字段 | 说明 |
|------|------|
| `version` | 版本号 |
| `build-time` | 构建时间 |
| `api-version` | Starlight API 版本 |
| `libraries` | 需要核心在运行时下载/加载的依赖，gradle 坐标 `group:name:version`；无则填 `[]` |

`libraries` 中的依赖会在 `onEnable` 阶段由核心的 `LibraryManager` 自动解析并加载。
