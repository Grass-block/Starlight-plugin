# WorldEdit 操作监测 <Badge>starlight-security:we-operation-defender</Badge>

监测并限制 WorldEdit 操作，防止过大范围的选区与编辑导致服务器性能问题或意外破坏。

## 基本信息

- 命名空间id: `starlight-security:we-operation-defender`
- 版本: `1.3`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

通过拦截 WorldEdit 指令和执行事件，限制玩家的选区大小和编辑范围：

- **指令拦截**：对超标的 `//` 指令（超长 Stack、超大选区）暂缓执行，要求玩家输入 `//confirm` 确认
- **选区限制**：对超过 `max-selection-size` 的选区自动取消（有 bypass 权限的玩家仅收到警告）
- **编辑半径限制**：为 Stack 操作的编辑范围添加以选区中心为原点的 `max-edit-size` 立方体半径限制，超出的方块不会被修改

所有限制值在有 `-starlight.worldedit.size` 权限时可得到提升（仅警告不取消）。

## 可配置项目

| id                       | 描述             | 可接受的输入值    |
|--------------------------|----------------|------------|
| `confirm-time`           | 指令确认的有效时间（秒）   | 整数（默认 15）  |
| `warn-stack-length`      | Stack 指令次数警告阈值 | 整数（默认 60）  |
| `warn-stack-effect-size` | Stack 效果区域警告阈值 | 整数（默认 256） |
| `warn-selection-size`    | 选区大小警告阈值       | 整数（默认 128） |
| `max-selection-size`     | 选区大小硬限制        | 整数（默认 128） |
| `max-edit-size`          | 编辑操作半径硬限制      | 整数（默认 384） |

## 命令

| 命令          | 权限                             | 描述                 |
|-------------|--------------------------------|--------------------|
| `//confirm` | `+starlight.worldedit.confirm` | 确认暂缓的 WorldEdit 操作 |
