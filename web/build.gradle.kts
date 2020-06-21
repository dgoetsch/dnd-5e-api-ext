plugins {
    application
}

val ktor_version = "1.3.1"

application {
    mainClassName = "dandd.character.automation.AppKt"
}
dependencies {
    implementation(project(":core"))
    implementation(project(":generated"))
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
}
