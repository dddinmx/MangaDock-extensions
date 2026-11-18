#!/usr/bin/env bash
set -euo pipefail

: "${ANDROID_BUILD_TOOLS:?Set ANDROID_BUILD_TOOLS to the Android SDK build-tools directory}"
: "${ANDROID_PLATFORM_JAR:?Set ANDROID_PLATFORM_JAR to the Android platform android.jar}"
: "${KALA_BUILD_DIR:?Set KALA_BUILD_DIR to an absolute build output directory}"

cd -- "$(dirname -- "$0")"
mkdir -p "$KALA_BUILD_DIR/deps" "$KALA_BUILD_DIR/host" "$KALA_BUILD_DIR/classes" "$KALA_BUILD_DIR/dex"
maven=https://repo.maven.apache.org/maven2
download() {
    local path="$1"
    local name="$2"
    if [[ ! -s "$KALA_BUILD_DIR/deps/$name" ]]; then
        curl --fail --silent --show-error --location "$maven/$path" -o "$KALA_BUILD_DIR/deps/$name.tmp"
        mv "$KALA_BUILD_DIR/deps/$name.tmp" "$KALA_BUILD_DIR/deps/$name"
    fi
}

download com/squareup/okhttp3/okhttp/3.14.9/okhttp-3.14.9.jar okhttp.jar
download com/squareup/okio/okio/1.17.5/okio-1.17.5.jar okio.jar
download org/jsoup/jsoup/1.17.2/jsoup-1.17.2.jar jsoup.jar

deps="$KALA_BUILD_DIR/deps"
classpath="$ANDROID_PLATFORM_JAR:$deps/okhttp.jar:$deps/okio.jar:$deps/jsoup.jar"
find host-api -name '*.java' | sort > "$KALA_BUILD_DIR/host-sources.txt"
javac --release 8 -encoding UTF-8 -cp "$classpath" \
    -d "$KALA_BUILD_DIR/host" @"$KALA_BUILD_DIR/host-sources.txt"
javac --release 8 -encoding UTF-8 -cp "$classpath:$KALA_BUILD_DIR/host" \
    -d "$KALA_BUILD_DIR/classes" src/main/java/Kalamanhua.java
jar cf "$KALA_BUILD_DIR/extension.jar" -C "$KALA_BUILD_DIR/classes" .
jar cf "$KALA_BUILD_DIR/host.jar" -C "$KALA_BUILD_DIR/host" .
"$ANDROID_BUILD_TOOLS/d8" --release --min-api 21 \
    --lib "$ANDROID_PLATFORM_JAR" --classpath "$KALA_BUILD_DIR/host.jar" \
    --classpath "$deps/okhttp.jar" --classpath "$deps/okio.jar" --classpath "$deps/jsoup.jar" \
    --output "$KALA_BUILD_DIR/dex" "$KALA_BUILD_DIR/extension.jar"

"$ANDROID_BUILD_TOOLS/aapt2" compile --dir src/main/res -o "$KALA_BUILD_DIR/resources.zip"
"$ANDROID_BUILD_TOOLS/aapt2" link -I "$ANDROID_PLATFORM_JAR" \
    --manifest src/main/AndroidManifest.xml -o "$KALA_BUILD_DIR/unaligned.apk" "$KALA_BUILD_DIR/resources.zip"
(
    cd "$KALA_BUILD_DIR/dex"
    zip -q -X "$KALA_BUILD_DIR/unaligned.apk" classes.dex
)
"$ANDROID_BUILD_TOOLS/zipalign" -f -p 4 "$KALA_BUILD_DIR/unaligned.apk" "$KALA_BUILD_DIR/unsigned.apk"
