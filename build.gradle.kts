
import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotest.multiplatform)
    alias(libs.plugins.spotless)
    alias(libs.plugins.antlr.kotlin) apply false
    id("utf8-workarounds")
}

group = "garden.ephemeral.rocket"
version = "0.1.0"

configurations {
    val antlr by registering
}

kotlin {
    jvm()
    linuxX64()
    macosX64()
    mingwX64()
    sourceSets {
        commonMain.dependencies {
            implementation(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.antlr.kotlin.runtime)
            implementation(libs.multik.core)
        }
        commonTest.dependencies {
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.property)
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }
        jvmMain.dependencies {
            implementation(libs.multik.default)
        }
        jvmTest.dependencies {
            implementation(libs.junit.platform.suite)
            implementation(libs.cucumber.java8)
            implementation(libs.cucumber.junit.platform.engine)
            implementation(libs.cucumber.picocontainer)
            implementation(libs.kotest.runner.junit5)
            implementation(libs.assertk.jvm)
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xadd-modules=jdk.incubator.vector")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
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

// Then a bunch of stuff that the antlr-kotlin plugin should be doing for us, but is not yet doing...

val generatedGrammarSourceDir = layout.buildDirectory.file("generated-src/antlr/commonMain/kotlin")
val generateKotlinGrammarSource by tasks.registering(AntlrKotlinTask::class) {
    // Works around default encoding not being UTF-8
    // https://github.com/Strumenta/antlr-kotlin/issues/85
    arguments = arguments + listOf("-encoding", "UTF-8")

    antlrClasspath = configurations["antlr"]

    source = project.objects
        .sourceDirectorySet("antlr", "antlr")
        .srcDir("src/commonMain/antlr").apply {
            include("**/*.g4")
        }
    outputDirectory = generatedGrammarSourceDir.get().asFile
    packageName = "garden.ephemeral.rocket.util"

    doLast {
        val outputDirectory = outputDirectory!!

        // Flatten the directory structure. The new version seems to create package directories for us.
        outputDirectory.resolve("garden/ephemeral/rocket/util").renameTo(outputDirectory.resolve("util"))

        // Workaround for generated code containing warnings
        // https://github.com/Strumenta/antlr-kotlin/issues/82
        outputDirectory.walk()
            .filter(File::isFile)
            .filter { f -> f.extension == "kt" }
            .forEach { file ->
                val text = file.readText()
                file.writeText("@file:Suppress(\"UNCHECKED_CAST\", \"UNUSED_PARAMETER\", \"USELESS_CAST\")\n$text")
            }
    }
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir(generatedGrammarSourceDir)
}

tasks.withType<KotlinCompile>().configureEach {
    dependsOn(generateKotlinGrammarSource)
}
tasks.withType<KotlinNativeCompile>().configureEach {
    dependsOn(generateKotlinGrammarSource)
}
