plugins {
    `java-library`
    alias(libs.plugins.protobuf)
    alias(libs.plugins.spotless)
}

spotless {
    java {
       target("*/src/*/java/**/*.java")
        googleJavaFormat(libs.versions.googleJavaFormat.get()).aosp()
    }
    format("misc") {
        target("*.gradle.kts", "*/*.gradle.kts", "*/src/**/*.proto", ".gitignore")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

dependencies {
    api(libs.protobuf.java)
    api(libs.netty.all)   // FrameCodec dùng chung cho server / bot / client
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}" }
}
