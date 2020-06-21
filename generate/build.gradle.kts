plugins {
    application
}

application {
    mainClassName = "dandd.character.automation.AppKt"
}
dependencies {
    implementation(project(":core"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
}
