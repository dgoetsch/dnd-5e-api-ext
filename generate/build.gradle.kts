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


application {
    mainClassName = "dandd.character.automation.AppKt"
}
dependencies {
    implementation(project(":core"))
}
