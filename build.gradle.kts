plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
}

allprojects {
    repositories {
        // Use jcenter for resolving dependencies.
        // You can declare any Maven/Ivy/file repository here.
        mavenCentral()
        jcenter()
    }
}

subprojects {
    apply(plugin = "kotlin")
    val arrow_version = "0.10.4"

    dependencies {
        // Align versions of all Kotlin components
        implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
        // Use the Kotlin JDK 8 standard library.
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
        implementation("io.arrow-kt:arrow-core:$arrow_version")
        implementation("khttp:khttp:1.0.0")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")

        // Use the Kotlin test library.
        testImplementation("org.jetbrains.kotlin:kotlin-test")
        // Use the Kotlin JUnit integration.
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    }
}



