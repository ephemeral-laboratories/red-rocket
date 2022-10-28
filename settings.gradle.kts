rootProject.name = "red-rocket"

pluginManagement {
    repositories {
        gradlePluginPortal()
        // antlr-kotlin currently not publishing on plugin portal :(
        // https://github.com/Strumenta/antlr-kotlin/issues/66
        maven("https://jitpack.io") {
            content {
                includeGroup("com.strumenta.antlr-kotlin")
            }
        }
    }

    resolutionStrategy {
        eachPlugin {
            // antlr-kotlin apparently not published with proper plugin metadata, so forcing the right module
            if (requested.id.id == "com.strumenta.antlr-kotlin") {
                useModule("com.strumenta.antlr-kotlin:antlr-kotlin-gradle-plugin:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        // antlr-kotlin currently not publishing on plugin portal :(
        // https://github.com/Strumenta/antlr-kotlin/issues/66
        maven("https://jitpack.io") {
            content {
                includeGroup("com.strumenta.antlr-kotlin")
            }
        }
    }
}
