# MangaDock Extensions

MangaDock 漫画源的独立 APK 仓库，目前提供 6 个适用于 Tachimanga / Tachiyomi 1.4 的扩展测试版本。

## 在 Tachimanga 添加一次

在「浏览 → 扩展 → 扩展仓库」添加以下地址（不同客户端版本的入口名称可能略有不同）：

```text
https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/index.min.json
```

添加后刷新扩展列表，即可安装各漫画源。以后本仓库推送新版 APK 和索引后，在客户端刷新并更新对应扩展即可，无需反复导入本地文件。

如已有手动安装的 MangaDock 扩展，包名、签名和源 ID 保持一致，可识别为同源更新。嬉皮最新版为 **1.4.4**，安装名及源名带版本标记。

## 当前 APK

| 漫画源 | 版本 | APK |
| --- | --- | --- |
| 瓜子漫画 | 1.4.1 | [下载](apk/tachiyomi-zh.mangadockguazimanhua-v1.4.1.apk) |
| 漫小肆 | 1.4.1 | [下载](apk/tachiyomi-zh.mangadockmxs-v1.4.1.apk) |
| 包子漫画.org | 1.4.1 | [下载](apk/tachiyomi-zh.mangadockbaozimhorg-v1.4.1.apk) |
| 嬉皮漫画 | 1.4.4 | [下载](apk/tachiyomi-zh.mangadockhipmh-v1.4.4.apk) |
| 漫画柜 | 1.4.1 | [下载](apk/tachiyomi-zh.mangadockmanhuagui-v1.4.1.apk) |
| 卡拉漫画 | 1.4.1 | [下载](apk/tachiyomi-zh.mangadockkalamanhua-v1.4.1.apk) |

每个 APK 对应一个漫画站点。此仓库暂不发布 1.6 包。

## 验证与测试状态

- 已核对全部 APK 的签名、包名、实际版本和 SHA-256，索引与文件一致。
- 嬉皮漫画 1.4.4 已通过 15 项回归测试，并验证真实接口可取得简介及章节；Tachimanga 实机表现仍在测试。
- 卡拉漫画的源站实测返回 HTTP 403，目前只通过解析约定测试。
- 测试版不代表所有站点已在手机端验证可读。欢迎通过 Issues 反馈客户端版本、漫画名称及错误信息。

`SHA256SUMS` 用于核对下载文件，`verification.json` 记录构建检查结果。APK 直接访问各自漫画源站。

## 后续更新

仓库保留稳定的安装索引地址。更新扩展时递增 versionCode，保留既有签名与源 ID，同步提交 APK、图标、索引与校验清单。签名私钥和本地配置不会上传。

目录约定参考 [Tachimanga 扩展仓库文档](https://tachimanga.app/help/guides/repositories.html)。
