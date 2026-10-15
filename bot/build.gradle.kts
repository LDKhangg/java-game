plugins { application }

dependencies {
    implementation(project(":common"))
    implementation(project(":protocol"))
}

application {
    mainClass.set("dao.bot.SmokeBot")
    applicationDefaultJvmArgs = listOf("-Dstdout.encoding=UTF-8")
}
