plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
}

val loomVersion = "1.14-SNAPSHOT"

dependencies {
    implementation("net.fabricmc:fabric-loom:${loomVersion}")
}

gradlePlugin {
    plugins {
        create("fabricConventions") {
            id = "fabric-conventions"
            implementationClass = "FabricConventionsPlugin"
        }
    }
}