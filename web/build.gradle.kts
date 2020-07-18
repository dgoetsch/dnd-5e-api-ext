plugins {
    kotlin("js")
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    mavenCentral()
    jcenter()
}

val ktor_version = "1.3.1"
val arrow_version = "0.10.4"

kotlin.target.browser {
}

val kotlinWrapperVersion = "16.13.1-pre.110-kotlin-1.3.72"

dependencies {
    implementation(project(":generated"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
    testImplementation("org.jetbrains.kotlin:kotlin-test-js")
    implementation("io.ktor:ktor-client-js:$ktor_version")
//    implementation("io.ktor:ktor-server-netty:$ktor_version")
//    implementation("io.ktor:ktor-html-builder:$ktor_version")
    implementation("org.jetbrains:kotlin-react:$kotlinWrapperVersion")
    implementation("org.jetbrains:kotlin-react-dom:$kotlinWrapperVersion")
    implementation(npm("react", "16.13.1"))
    implementation(npm("react-dom", "16.13.1"))

    //Kotlin Styled (chapter 3)
    implementation("org.jetbrains:kotlin-styled:1.0.0-pre.94-kotlin-1.3.70")
    implementation(npm("styled-components"))
    implementation(npm("inline-style-prefixer"))


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.7")

}


