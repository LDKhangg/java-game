plugins { application }

dependencies {
    implementation(project(":common"))
    implementation(libs.gdx)
    implementation(libs.gdx.backend.lwjgl3)
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:${libs.versions.gdx.get()}:natives-desktop")
}

val jvmFlags = buildList {
    add("--enable-native-access=ALL-UNNAMED") // tránh cảnh báo native access trên JDK mới
    if (System.getProperty("os.name").lowercase().contains("mac")) add("-XstartOnFirstThread")
}

application {
    mainClass.set("dao.client.DesktopLauncher")
    applicationDefaultJvmArgs = jvmFlags
}
