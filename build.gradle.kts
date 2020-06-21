plugins {
    kotlin("jvm") version "1.3.72" apply false
    kotlin("js") version "1.3.72" apply false
}

allprojects {

}

val jsProjects = emptyList<String>()//listOf("generated", "web")
subprojects {

    if(jsProjects.contains(name)) {
//        apply(plugin = "kotlin")
//        dependencies {
//            implementation(kotlin("stdlib-js"))
//
//        }
    } else {

//        apply(plugin = "org.jetbrains.kotlin.jvm")

        repositories {
            // Use jcenter for resolving dependencies.
            // You can declare any Maven/Ivy/file repository here.
            mavenCentral()
            jcenter()
        }
    }
}



