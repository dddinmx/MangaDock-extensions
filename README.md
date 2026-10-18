# Tachimanga ｜ Mihon ｜ Aidoku 中文漫画源扩展指南

本仓库提供面向 **Tachimanga、Tachiyomi 和 Mihon** 的中文漫画源扩展插件（APK），以及面向 **Aidoku** 的扩展包（`.aix`）。

## 使用前准备

APK 发布包使用 **API 1.4**。安装前请确认阅读器仍支持这一扩展规范，具体范围见[客户端兼容](#客户端兼容)。

Aidoku 使用独立的 `.aix` 扩展包，通过 [Aidoku 源列表](#aidoku-源列表)添加，与 APK 版本对应但完全独立。

## 安装扩展

### 添加仓库

**Tachimanga | Mihon 源列表地址：**

```text
https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/index.min.json
```

**Aidoku 专用源列表地址：**

```text
https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/aidoku/index.min.json
```

### 手动安装

也可从下方[漫画源列表](#漫画源列表)下载单个 APK。在 Tachimanga 的「浏览 → 扩展」页面点击右上角「+」，选择下载的文件进行导入。

## 漫画源列表

<!-- sources:start -->
| 漫画源 | 站点 | 扩展版本 | APK | 网络 |
| --- | --- | --- | --- | --- |
| 瓜子漫画 | [www.guazimanhua.com](https://www.guazimanhua.com) | 1.4.1 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockguazimanhua-v1.4.1.apk) | 国内网络 |
| 包子漫画.org | [baozimh.org](https://baozimh.org) | 1.4.3 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockbaozimhorg-v1.4.3.apk) | 国内网络 |
| 漫画柜 | [www.manhuagui.com](https://www.manhuagui.com) | 1.4.3 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockmanhuagui-v1.4.3.apk) | **国外网络** |
| 嬉皮漫画 | [m.hipmh.com](https://m.hipmh.com) | 1.4.4 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockhipmh-v1.4.4.apk) | 国内网络 |
| 漫小肆 | [mxs12.cc](https://mxs12.cc) | 1.4.3 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockmxs-v1.4.3.apk) | 国内网络 |
| 卡拉漫画 | [www.kalamanhua.com](https://www.kalamanhua.com) | 1.4.1 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockkalamanhua-v1.4.1.apk) | 国内网络 |
| 包子漫画.app | [appcn.baozimh.com](https://appcn.baozimh.com) | 1.4.5 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockbaozimhapp-v1.4.5.apk) | 国内网络 |
| 番茄漫画 | [fanqienovel.com](https://fanqienovel.com) | 1.4.2 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockfanqie-v1.4.2.apk) | 国内网络 |
<!-- sources:end -->

> 包子漫画.app 走的是官方 App 接口（`/baozimhapp/*`），与包子漫画.org 的网页解析是两个独立源，包名与数据互不相通，可以同时安装。

## 客户端兼容

| 阅读器 | 适用范围 |
| --- | --- |
| [Aidoku](https://aidoku.app) | IOS自签客户端. |
| [Tachimanga](https://tachimanga.app/) | IOS客户端. |
| [Mihon](https://mihon.app/) | 适用于仍兼容 API 1.4 扩展的版本。要求 TachiyomiX 1.6 的版本不能使用扩展. |

## 常见问题

### 安装后仍显示旧版本

先核对已安装版本与[漫画源列表](#漫画源列表)是否一致。刷新仓库列表并重新打开客户端后，再检查更新。
如果版本仍未变化，可在扩展页移除旧扩展，再从仓库重新安装。书架作品保留即可。

### 简介、章节或图片加载失败

先在浏览器中检查对应源站是否可以访问，再回到作品页面下拉刷新。源站可访问情况可能随网络、地区和网站结构变化。
仍无法加载时，请提交 [Issue](https://github.com/dddinmx/MangaDock-extensions/issues/new)，并提供：

- 阅读器名称与版本。
- 漫画源名称和扩展版本。
- 漫画名称或作品链接。
- 失败的位置：搜索、简介、章节列表或图片。
- 页面显示的错误信息；没有提示时也请说明。

## 相关文档

- [Tachimanga 扩展开发指南](https://tachimanga.app/help/guides/extensions_guide.html)
- [Tachimanga 扩展仓库说明](https://tachimanga.app/help/guides/repositories.html)
- [Aidoku 官网](https://aidoku.app/)
- [aidoku-rs 源开发文档](https://aidoku.github.io/aidoku-rs/book/)
- [当前仓库索引](index.json)
- [Aidoku 源列表索引](aidoku/index.json)
- [问题反馈](https://github.com/dddinmx/MangaDock-extensions/issues)
