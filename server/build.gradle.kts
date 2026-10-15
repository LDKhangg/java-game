plugins { application }

dependencies {
    implementation(project(":common"))
    implementation(project(":protocol"))
    implementation(libs.jctools.core)
    implementation(libs.slf4j.api)
    runtimeOnly(libs.logback.classic)
}

application { mainClass.set("dao.server.Main") }
