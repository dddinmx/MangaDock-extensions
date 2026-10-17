# 中文漫画源扩展指南

本仓库提供面向 **Tachimanga、Tachiyomi 和 Mihon** 的中文漫画源扩展插件。

Chinese manga / manhua source APK extensions for Tachimanga, Tachiyomi and compatible Mihon versions.

## 目录

1. [使用前准备](#使用前准备)
2. [安装扩展](#安装扩展)
   - [添加仓库](#添加仓库)
   - [手动安装](#手动安装)
3. [漫画源列表](#漫画源列表)
4. [客户端兼容](#客户端兼容)
5. [常见问题](#常见问题)
6. [相关文档](#相关文档)

## 使用前准备

- 已安装 Tachimanga / Tachiyomi / Mihon 版本。
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

不同客户端版本的入口名称可能略有不同。

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
| 包子漫画.cn | [cn.bzmgcn.com](https://cn.bzmgcn.com) | 1.4.3 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockbaozimhcn-v1.4.3.apk) | **国外网络** |
| 番茄漫画 | [fanqienovel.com](https://fanqienovel.com) | 1.4.2 | [下载](https://raw.githubusercontent.com/dddinmx/MangaDock-extensions/main/apk/tachiyomi-zh.mangadockfanqie-v1.4.2.apk) | 国内网络 |
<!-- sources:end -->

## 客户端兼容

| 阅读器 | 适用范围 |
| --- | --- |
| [Tachimanga](https://tachimanga.app/) | 当前主要测试客户端. |
| Tachiyomi | 适用于仍支持 API 1.4 扩展的版本. |
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
- [当前仓库索引](index.json)
- [问题反馈](https://github.com/dddinmx/MangaDock-extensions/issues)

## 更新记录

### 2026-10-16 · 漫小肆 1.4.3

- 修复手机版作品页进入后提示“未解析到漫画详情”的问题。
- 补齐手机版标题、简介、作者、封面、分类与连载状态解析，兼容桌面版页面。
- 通过 6 项回归测试，并实测多部作品及首章图片请求。可在扩展仓库中刷新更新。

### 2026-10-16 · 包子漫画.org 1.4.3

- 修复列表将封面图片的拼音文件名当作漫画标题的问题，改为读取页面显示的中文书名。
- 修复热门、更新列表分页，支持继续加载后续漫画；同步修正搜索地址及搜索分页。
- 简介、作者和封面直接从作品页读取，减少重复拉取整本章节数据的请求。
- 通过 9 项回归测试，可在扩展仓库中刷新更新。

### 2026-10-16 · 漫画柜 1.4.3

- 修复手机浏览器请求被跳转到不可用的 HTTP 手机版，导致首页打不开的问题。
- 漫画柜请求改用桌面浏览器身份，实测首页、更新、搜索与作品详情均直接返回 200。
- 通过 3 项回归测试，可在扩展仓库中刷新更新。

### 2026-10-16 · 包子漫画.cn 1.4.1

- 新增独立的包子漫画 cn 扩展，当前站点为 `www.baozimh.com`，与 org 扩展分别安装。
- 支持列表分页、搜索、最新上架、作品简介、完整章节目录和漫画图片。
- 自动完成源站浏览器验证并复用会话，减少重复验证。
- 通过 10 项回归测试；实测《三体》《一人之下》《绍宋》的详情、目录、封面及章节图片。客户端导入与阅读仍需设备实测。

### 2026-10-16 · 番茄漫画 1.4.1

- 新增独立的 API 1.4 扩展，支持按漫画名、作品 ID 或作品链接搜索，以及简介、目录、封面和正文图片读取。
- 本地直接读取，无需额外配置服务器；首页展示漫画检索结果，暂不提供独立的最近更新页。
- 发布包启用代码混淆、协议字符串保护与调试信息清理；仅发布 APK，不公开番茄算法源码或混淆映射。
- 通过 15 项回归测试，并实测《同居只为攻略你》《叛逆小厨娘》《我的右眼是神级计算机》的目录、封面和章节首尾图片；Tachimanga 设备导入与阅读仍待实测。

### 2026-10-16 · 番茄漫画 1.4.2

- 针对 Tachimanga 首页空白，统一采用标准 HttpSource 请求与解析流程，覆盖首页、搜索、详情、目录和正文图片。
- 加密运算改为扩展内纯 Java 实现，避免依赖客户端系统的 AES/GCM 支持；保留代码混淆与协议字符串保护。
- 使用编译器直接生成的 DEX，保留客户端调用接口，并恢复主接口不可用时的备用接口请求。
- 通过 18 项番茄回归测试及无系统 AES 环境测试；以标准请求与解析流程实测首页分页、搜索，以及《同居只为攻略你》《叛逆小厨娘》《我的右眼是神级计算机》的简介、目录、封面和章节首尾图片。Tachimanga 实际导入与阅读仍需设备验证。

### 2026-10-17 · 包子漫画.com 1.4.2

- 显示名称由“包子漫画.cn”改为“包子漫画.com”，对应实际使用的 `www.baozimh.com`；旧入口 `cn.baozimhcn.com` 当前会跳转到该地址。保持扩展包名和漫画源 ID，已有书架无需迁移。
- 针对扩展列表 HTTP 403，补齐验证 Cookie 的保存、有效期检查和请求复用，兼容客户端不提供 Cookie 存储的情况。
- 通过 11 项回归测试；在禁用客户端 Cookie 存储的环境下，实测列表两页、搜索、最新列表，以及《三體》《一人之下》《紹宋》的详情、目录、封面和章节首尾图片。Tachimanga 实机表现仍需设备验证。

### 2026-10-17 · 包子漫画.cn 1.4.3

- 按指定入口改用 `https://cn.bzmgcn.com`，列表、搜索及新解析的章节请求均从该入口发起；显示名称同步改为“包子漫画.cn”。包名、签名和漫画源 ID 保持一致。
- 源站目前会将该入口重定向到 `www.baozimh.com`；修正跳转后验证请求的目标地址，使验证 Cookie 在最终站点生效，并继续复用会话。
- 通过 12 项回归测试，包含禁用客户端 Cookie 存储时的跨域跳转验证；从 cn 入口实测列表分页、搜索及《三體》《一人之下》《紹宋》的详情、目录、封面和章节首尾图片。Tachimanga 实机效果仍需设备验证。
