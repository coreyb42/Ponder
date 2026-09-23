plugins {
    alias(libs.plugins.mdg)
    alias(libs.plugins.configure.platform)
}

neoForge {
    version = libs.versions.neoforge.get()

    accessTransformers.from(file("src/main/resources/META-INF/accesstransformer.cfg"))

    runs {
        create("client") {
            client()
        }

        create("server") {
            server()

            gameDirectory = project.file("run/server")
        }

        configureEach {
            jvmArgument("-Dmixin.debug.export=true")
            jvmArgument("-XX:+IgnoreUnrecognizedVMOptions")
            jvmArgument("-XX:+AllowEnhancedClassRedefinition")
        }
    }

    mods.register("ponder") {
        sourceSet(sourceSets.main.get())
    }
}

dependencies {
    // The published Ponder module exposes Catnip as an API dependency. In a
    // parent composite, Create resolves Catnip directly to its merged jar;
    // keeping this edge compile-only prevents Gradle from selecting Catnip's
    // incomplete classes-directory secondary variant first.
    if (gradle.parent == null) {
        api(project(":catnip:neoforge"))
    } else {
        compileOnly(project(":catnip:neoforge"))
    }
    api(libs.flywheel.neoforge.api)
    runtimeOnly(libs.flywheel.neoforge.asProvider())
}
