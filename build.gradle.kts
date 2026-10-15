// Cấu hình chung cho mọi module Java
subprojects {
    plugins.withType<JavaPlugin>().configureEach {
        extensions.configure<JavaPluginExtension> {
            toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }
        }
        tasks.withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }
        tasks.withType<Test>().configureEach { useJUnitPlatform() }

        dependencies {
            add("testImplementation", platform(libs.junit.bom))
            add("testImplementation", libs.junit.jupiter)
            add("testRuntimeOnly", libs.junit.platform.launcher) // Gradle 9 yêu cầu khai báo rõ
        }
    }
}
