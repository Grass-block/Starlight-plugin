# AI 命令聊天 <Badge>starlight-ai:ai-command-chat</Badge>

通过 `/ai` 向配置的默认 AI 服务发送消息。

## 基本信息

- 命名空间id: `starlight-ai:ai-command-chat`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

属于 `starlight-ai` 包，依赖同包注册的 `AIChatService`。命令异步请求默认服务，先发送思考提示，然后向执行者显示回复和耗时；服务返回失败时显示错误。

当前请求使用执行者名称作为 user 和 contextId。OpenAI 兼容实现每次仅发送当前用户输入，不能据此认定支持多轮历史；AstrBot 的上下文由其服务端处理。此模块不会自动回复普通聊天。

## 可配置项目

插件数据目录中的 `config/ai-models.yml`，保留根节点 `ai-models:`。`default` 指向 `services` 下的条目名。内置模板默认选择 `example-astrbot`，其中凭据为占位值，使用前需修改。

```yaml
ai-models:
  default: my-service
  services:
    my-service:
      service: openai
      base-url: "https://api.openai.com"
      api-key: "替换为自己的密钥"
      model: "填写服务提供的模型名"
      max-tokens: 256
      temperature: 0.7
```

| 字段 | 说明 |
|----|----|
| `service` | `openai` 或 `astrbot` |
| `base-url` | 服务基础地址；`openai` 实现会在末尾追加 `/v1/chat/completions`，因此不要重复填写该路径 |
| `api-key` | 服务 API 密钥 |
| `model` | `openai` 必填的模型名称 |
| `max-tokens` | `openai` 的最大输出 token 数，省略时 `256` |
| `temperature` | `openai` 的采样温度，省略时 `0.7` |

至少配置一个有效服务，并确保 `default` 名称存在。修改后重载插件以重新初始化服务。

## 命令

| 命令 | 权限节点 | 说明 |
|----|----|----|
| `/ai <消息...>` | `starlight.ai.chat` | 请求默认 AI 服务 |
