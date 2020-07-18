val kotlinWrapperVersion = "16.13.1-pre.110-kotlin-1.3.72"

dependencies {
    implementation(project(":web:core"))
    implementation(project(":web:generated"))

    implementation("org.jetbrains:kotlin-react:$kotlinWrapperVersion")
    implementation("org.jetbrains:kotlin-react-dom:$kotlinWrapperVersion")
    implementation(npm("react", "16.13.1"))
    implementation(npm("react-dom", "16.13.1"))

    //Kotlin Styled (chapter 3)
    implementation("org.jetbrains:kotlin-styled:1.0.0-pre.94-kotlin-1.3.70")
    implementation(npm("styled-components"))
    implementation(npm("inline-style-prefixer"))
}


