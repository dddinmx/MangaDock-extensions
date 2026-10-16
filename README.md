# 中文漫画源扩展指南

本仓库提供面向 **Tachimanga、Tachiyomi 和兼容版本 Mihon** 的中文漫画源扩展 APK。每个扩展对应一个漫画站点，可通过固定仓库地址分别安装和更新。

Chinese manga / manhua source APK extensions for Tachimanga, Tachiyomi and compatible Mihon versions.

## 目录

1. [使用前准备](#使用前准备)
2. [安装扩展](#安装扩展)
   - [添加仓库](#添加仓库)
   - [选择漫画源](#选择漫画源)
   - [手动安装](#手动安装)
3. [漫画源列表](#漫画源列表)
4. [客户端兼容](#客户端兼容)
5. [更新扩展](#更新扩展)
6. [常见问题](#常见问题)
7. [测试状态](#测试状态)
8. [相关文档](#相关文档)

## 使用前准备

- 已安装 Tachimanga，或支持 API 1.4 扩展的 Tachiyomi / Mihon 版本。
- 设备可以访问 GitHub 下载地址和所选漫画源站。

当前发布包使用 **API 1.4**。安装前请确认阅读器仍支持这一扩展规范，具体范围见[客户端兼容](#客户端兼容)。

## 安装扩展

### 添加仓库

复制以下仓库地址：

```text
https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/index.min.json
```

在 Tachimanga 中：

1. 打开「浏览」页面，进入「扩展」。
2. 找到「扩展仓库」，添加上面的地址。
3. 返回扩展列表并刷新。

不同客户端版本的入口名称可能略有不同。Tachiyomi / Mihon 若支持自定义扩展仓库，可添加同一地址。

### 选择漫画源

1. 在扩展列表中找到要使用的漫画源并安装。
2. 返回漫画源列表，打开对应站点。
3. 搜索漫画或浏览列表，进入作品后加载简介和章节。

各漫画源独立安装，按需要选择即可。

### 手动安装

也可从下方[漫画源列表](#漫画源列表)下载单个 APK。在 Tachimanga 的「浏览 → 扩展」页面点击右上角「+」，选择下载的文件进行导入。

## 漫画源列表

<!-- sources:start -->
| 漫画源 | 站点 | 扩展版本 | APK |
| --- | --- | --- | --- |
| 瓜子漫画 | [www.guazimanhua.com](https://www.guazimanhua.com) | 1.4.1 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockguazimanhua-v1.4.1.apk) |
| 包子漫画.org | [baozimh.org](https://baozimh.org) | 1.4.1 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockbaozimhorg-v1.4.1.apk) |
| 漫画柜 | [www.manhuagui.com](https://www.manhuagui.com) | 1.4.1 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockmanhuagui-v1.4.1.apk) |
| 嬉皮漫画 | [m.hipmh.com](https://m.hipmh.com) | 1.4.4 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockhipmh-v1.4.4.apk) |
| 漫小肆 | [mxs12.cc](https://mxs12.cc) | 1.4.1 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockmxs-v1.4.1.apk) |
| 卡拉漫画 | [www.kalamanhua.com](https://www.kalamanhua.com) | 1.4.1 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockkalamanhua-v1.4.1.apk) |
<!-- sources:end -->

包子漫画扩展对应 **baozimh.org**；嬉皮漫画也称 **嬉皮漫畫 / Hipmh**，漫画柜对应 **Manhuagui**，漫小肆对应 **MXS**。扩展直接请求各自源站的数据。

## 客户端兼容

| 阅读器 | 适用范围 |
| --- | --- |
| Tachimanga | 当前主要测试客户端，提供 API 1.4 扩展 APK。 |
| Tachiyomi | 适用于仍支持 API 1.4 扩展的版本。 |
| Mihon | 适用于仍兼容 API 1.4 扩展的版本。要求 TachiyomiX 1.6 的版本不能使用当前 APK。 |

本仓库当前只发布 **1.4 测试版**，尚未发布 1.6 安装包。

## 更新扩展

1. 在客户端刷新扩展列表。
2. 如果对应漫画源有新版本，选择更新。
3. 更新后进入作品页面，下拉刷新简介和章节。

仓库地址保持固定，后续版本继续使用已有包名、签名和源 ID。通常不需要重新添加仓库或逐次导入文件。

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

## 测试状态

| 项目 | 当前情况 |
| --- | --- |
| APK 发布检查 | 已核对签名、包名、实际版本、下载链接及 SHA-256。 |
| 嬉皮漫画 | 已进行真实接口和解析回归测试，Tachimanga 实机表现仍在反馈与修复中。 |
| 卡拉漫画 | 源站实测返回 HTTP 403，目前只通过解析约定测试。 |
| 其他漫画源 | 已发布独立测试包，仍需逐站验证手机端安装和阅读。 |

发布 APK 不代表所有站点已在手机端验证可读。可查看[构建检查记录](verification.json)和 [SHA-256 校验清单](SHA256SUMS)。

## 相关文档

- [Tachimanga 扩展开发指南](https://tachimanga.app/help/guides/extensions_guide.html)
- [Tachimanga 扩展仓库说明](https://tachimanga.app/help/guides/repositories.html)
- [当前仓库索引](index.json)
- [问题反馈](https://github.com/dddinmx/MangaDock-extensions/issues)
