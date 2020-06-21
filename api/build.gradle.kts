plugins {
    application
}

application {
    mainClassName = "dandd.character.automation.AppKt"
}
val ktor_version = "1.3.1"

dependencies {
    implementation(project(":core"))
    implementation("io.ktor:ktor-server-netty:$ktor_version")
}