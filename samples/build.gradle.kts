
import kotlinx.benchmark.gradle.benchmark
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kotlinx.benchmark)
    alias(libs.plugins.kotlin.plugin.allopen)
    id("utf8-workarounds")
}

group = "garden.ephemeral.rocket"
version = "0.1.0"

kotlin {
    jvm()
    mingwX64 {
        compilerOptions {

        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.benchmark.runtime)
            implementation(libs.multik.core)
        }
        jvmMain.dependencies {
            implementation(rootProject)
        }
    }
}

benchmark {
    targets {
        register("jvm")
//        register("wasmJs")
        register("mingwX64")
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

java {
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xadd-modules=jdk.incubator.vector")
    }
}

tasks.withType<JavaExec> {
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
