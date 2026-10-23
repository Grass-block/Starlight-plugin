# 聊天翻译 <Badge>starlight-chat:chat-translator</Badge>

在聊天行后添加翻译按钮，将消息翻译为执行者在 Starlight 中使用的语言。

## 基本信息

- 命名空间id: `starlight-chat:chat-translator`
- 版本: `-`
- 是否为内部模块: `否`
- 是否默认开启: `是`
- 是否为未完成[beta]阶段: `否`

## 描述

模块通过 `ForwardedChatEvent` 保存聊天正文，并附加可点击的 `[🌏]` 按钮。点击后异步请求翻译，结果只发给执行者。源语言由服务自动检测，目标语言取执行者的 Starlight 语言设置，可用 `/locale <语言代码>` 修改。

聊天记录保存在内存中，距离上次访问 5 分钟后过期；同一记录按目标语言缓存译文。记录不存在或已过期时会提示未找到。当前没有自动翻译模式。

模块默认开启，但翻译服务模板的 `active` 默认为 `NONE`。必须先配置服务，否则执行翻译命令会提示未配置翻译服务。

## 可配置项目

显示配置位于 `config/values/starlight-chat.yml` 的 `config.chat-translator` 下。

| 配置项 | 类型 | 默认值 / 说明 |
|----|----|----|
| `append` | String | ` {click(command,/translate-chat {});color(aqua)}[🌏]{;}`，`{}` 替换为记录 ID |
| `substring-raw` | Boolean | `true`，原文预览取前 15 个 Java 字符并附加 `...`；关闭后显示完整原文 |
| `template` | List&lt;String&gt; | 多行结果模板，格式参数依次为原文预览、目标语言、译文 |

服务配置是插件数据目录下的 `config/translate-service.yml`，保留最外层 `translate-service:`。例如启用内置 Google 公共接口：

```yaml
translate-service:
  active: default-google-public
  default-google-public:
    type: google-public
```

`active` 填写同层服务条目的名称，`type` 使用下表中的精确值。

| `type` | 服务 | 条目中的配置项 |
|----|----|----|
| `google-public` | Google 网页翻译接口 | 无密钥配置；使用 `translate.googleapis.com` 公共接口 |
| `deepl` | DeepLX | `deepl-endpoint` 为完整地址（如 `http://127.0.0.1:1188/translate`）；`deepl-token` 为可留空的 Bearer token |
| `tencent` | 腾讯云 TextTranslate | `tencent-secret-id`、`tencent-secret-key` 必填；`tencent-region` 模板值为 `ap-guangzhou` |

DeepLX 的类型值是 `deepl`，并非 `deep-lx`；腾讯云的类型值是 `tencent`。修改配置后重新启用模块或重载插件。请求连接超时为 10 秒，请求超时为 30 秒。

## 命令

| 命令 | 权限节点 | 说明 |
|----|----|----|
| `/translate-chat <记录ID>` | `starlight.translate.chat` | 翻译已记录的聊天正文，通常通过点击按钮执行 |
| `/translate <文本...>` | `starlight.translate.custom` | 翻译自行输入的文本 |

两个权限在命令注解中的声明分别为 `+starlight.translate.chat` 和 `-starlight.translate.custom`。
