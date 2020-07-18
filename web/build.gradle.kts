plugins {
    kotlin("js")
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.js")
    }

    repositories {
        // Use jcenter for resolving dependencies.
        // You can declare any Maven/Ivy/file repository here.
        maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
        mavenCentral()
        jcenter()
    }

    kotlin.target.browser { }
    
    val ktor_version = "1.3.1"
    val kotlinWrapperVersion = "16.13.1-pre.110-kotlin-1.3.72"

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
        testImplementation("org.jetbrains.kotlin:kotlin-test-js")
        implementation("io.ktor:ktor-client-js:$ktor_version")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.7")
    }
}








