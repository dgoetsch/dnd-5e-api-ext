plugins {
    kotlin("jvm")
    application
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    mavenCentral()
    jcenter()
}

val ktor_version = "1.3.1"
val arrow_version = "0.10.4"

application {
    mainClassName = "dandd.character.automation.AppKt"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":generated"))
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
}
