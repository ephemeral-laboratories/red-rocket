
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.spotless)
    id("utf8-workarounds")
}

group = "garden.ephemeral.rocket"
version = "0.1.0"

kotlin {
    jvm()
    linuxX64()
    macosX64()
    mingwX64()
    sourceSets {
        commonMain.dependencies {
            implementation(rootProject)
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xadd-modules=jdk.incubator.vector")
    }
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs("--add-modules=jdk.incubator.vector")
}

spotless {
    val ktlintVersion = libs.versions.ktlint.get()
    kotlin {
        ktlint(ktlintVersion)
            .setUseExperimental(true)
            .editorConfigOverride(mapOf("disabled_rules" to "filename"))
        targetExclude("build/**")
    }
    kotlinGradle {
        ktlint(ktlintVersion)
    }
}
