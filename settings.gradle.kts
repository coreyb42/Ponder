pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "ponder"

for (platform in listOf("common", "fabric", "neoforge")) {
    include(platform)

    include(":catnip:$platform")
    include(":testmod:$platform")
}

includeBuild("build-logic")

// During the 26.2 port, consume the sibling Flywheel fork rather than an
// unpublished 26.2 Maven artifact. Published coordinates remain authoritative
// for normal downstream consumers.
val flywheelDirectory = file("../Flywheel")
if (flywheelDirectory.isDirectory) {
    includeBuild(flywheelDirectory) {
        dependencySubstitution {
            substitute(module("dev.engine-room.flywheel:flywheel-neoforge-api-26.2")).using(project(":neoforge"))
            substitute(module("dev.engine-room.flywheel:flywheel-neoforge-26.2")).using(project(":neoforge"))
            substitute(module("dev.engine-room.flywheel:flywheel-fabric-api-26.2")).using(project(":fabric"))
            substitute(module("dev.engine-room.flywheel:flywheel-fabric-26.2")).using(project(":fabric"))
        }
    }
}
