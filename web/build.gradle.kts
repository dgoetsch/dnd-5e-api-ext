plugins {
    kotlin("js")
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    mavenCentral()
    jcenter()
}

val ktor_version = "1.3.1"
val arrow_version = "0.10.4"

kotlin.target.browser {
}

dependencies {
    implementation(project(":generated"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
//    implementation("io.ktor:ktor-server-netty:$ktor_version")
//    implementation("io.ktor:ktor-html-builder:$ktor_version")
}
