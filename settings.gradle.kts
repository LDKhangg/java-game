// Tự tải JDK 25 nếu máy chưa có (toolchain resolver)
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "java-game"

dependencyResolutionManagement {
    repositories { mavenCentral() }
}

include(
    "common",    // hằng số, bảng Cảnh Giới (Realm) - dùng chung client/server
    "protocol",  // file .proto + FrameCodec - dùng chung client/server/bot
    "server",    // game server (Netty + GameLoop)
    "client",    // client libGDX
    "bot"        // client headless để smoke-test / load-test
)
