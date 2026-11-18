# 卡拉漫画 APK

## 构建

使用 JDK 17 或更高版本及 Android SDK。设置 `ANDROID_BUILD_TOOLS` 为含有 `aapt2`、`d8` 和 `zipalign` 的 SDK 目录，设置 `ANDROID_PLATFORM_JAR` 为 Android 34 或更新版本的 `android.jar`，并设置 `KALA_BUILD_DIR` 为绝对输出路径。运行 `./build.sh`，产物为 `$KALA_BUILD_DIR/unsigned.apk`。

使用 Android SDK 的 `apksigner` 和卡拉漫画 1.4.2 的发布 keystore 签名。后续更新沿用这一签名。签名材料独立保管。构建产物只包含扩展实现，`host-api` 下的类型由阅读器在运行时提供。

## 联网回归测试

`test/LiveCheck.java` 检查热门列表、关键字分页、旧作品链接、作品详情、章节列表和页图。用 Enjarify 从已签名 APK 转换 `classes.dex` 后，将转换所得 JAR、构建目录的 `host.jar`、Android 平台 JAR、构建目录的三个联网依赖和 JVM 版 `org.json` 放到类路径。编译并运行 `LiveCheck`，传入用于保存抽样图片的目录，再解码输出的图片。
