plugins {
    application
}

application {
    mainClassName = "dandd.character.automation.AppKt"
}
dependencies {
    implementation(project(":core"))
    implementation(project(":generate"))
}
