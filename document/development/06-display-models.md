# 6. 展示模型实验代码

当前工作区在 `org.atcgroup.starlight.bundle.oddities.model` 下新增了模型记录和解码逻辑。它们没有注册为独立模块，也没有玩家命令；[custom-vehicle](../modules/starlight-oddities/custom-vehicle.md) 的测试入口仍使用原 `CustomDisplayModel`。

## 6.1 可调用入口

| 入口 | 输入与结果 |
|----|----|
| `LModelDecoder.decode(Path)` | UTF-8 JSON 文件 → `DisplayModel` |
| `LModelDecoder.decode(String)` / `decode(JsonObject)` | JSON 文本 / 对象 → `DisplayModel` |
| `LModelDecoder.decodeMinecraftCubes(Path)` / `decodeMinecraftCubes(JsonObject)` | Java 模型 `elements` → `List<MinecraftCube>` |
| `DisplayModel.spawn(Location)` | 在锚点创建 `BlockDisplay`，返回实体列表；调用方负责生命周期管理 |

`model.decoder.BedrockModelDecoder` 当前为空类，`model.decoder.ModelDecoder` 当前为空接口；不要将它们当作已经实现的统一解码 API。

## 6.2 DisplayModel

`decode` 接受 `minecraft:geometry` 的 bones / cubes，也接受顶层 `elements`。坐标、枢轴和 `BasePos` 以像素输入，按 16 像素 = 1 方块换算；旋转和 `BaseRot` 使用角度。cube 的 `origin`、`size` 或 element 的 `from`、`to` 为几何输入。

方块 ID 可直接写在 cube 的 `blockId`、`block_id` 或 `material` 字段中；也可由模型 `textures` 数组和 face 的纹理引用解析。当前实现把纹理名称规范化为方块 ID，并不加载图片纹理、资源包父模型或完整骨骼动画。不能解析方块 ID 时抛出异常。

`DisplayBlockModel` 保存单个长方体的方块 ID、边界、枢轴和旋转。`DisplayModel.spawn` 用 `Bukkit.createBlockData` 解析方块状态，在每个长方体上设置变换与零阴影，调用方须在服务端允许的世界 / 实体调度上下文使用。

最小解码输入示例：

```json
{
  "BasePos": [0, 0, 0],
  "BaseRot": [0, 0, 0],
  "elements": [
    {"from": [0, 0, 0], "to": [16, 16, 16], "blockId": "minecraft:stone"}
  ]
}
```

## 6.3 Java 模型方块分类

`decodeMinecraftCubes` 使用 Java 模型的 `textures` 对象和 `elements[].faces`。当前 face 的纹理引用在该对象中查找，例如 `#0`；并不递归解析资源包父模型或纹理别名链。

所有已定义面的纹理 ID 相同时产生 `DirectCube`，否则产生 `FaceBasedCube`，后者保存各方向的 `CubeFace` 与 UV。缺失或不合法的 UV 转为空数组。返回的分类记录与 `DisplayModel.spawn` 是两条不同路径，后者不会直接把 `FaceBasedCube` 生成为带不同面纹理的展示实体。
