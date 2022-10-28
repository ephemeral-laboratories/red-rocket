import com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    alias(libs.plugins.spotless)
    alias(libs.plugins.antlr.kotlin) apply false
    alias(libs.plugins.nebula.facet)
    id("utf8-workarounds")
}

group = "garden.ephemeral.rocket"
version = "0.1.0"

facets {
    @Suppress("UNUSED_VARIABLE")
    val samples by registering
}

configurations {
    @Suppress("UNUSED_VARIABLE")
    val antlr by registering
}

dependencies {
    @Suppress("UnstableApiUsage")
    "antlr"(libs.antlr.kotlin.target)

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.antlr.kotlin.runtime)

    testImplementation(libs.junit.platform.suite)
    testImplementation(libs.cucumber.java8)
    testImplementation(libs.cucumber.junit.platform.engine)
    testImplementation(libs.cucumber.picocontainer)
    testImplementation(libs.assertk.jvm)
}

extensions.configure<JavaPluginExtension> {
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<Test> {
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

val generatedGrammarSourceDir = file("$buildDir/generated-src/antlr/main/kotlin")
val generateKotlinGrammarSource by tasks.registering(AntlrKotlinTask::class) {
    // Works around default encoding not being UTF-8
    // https://github.com/Strumenta/antlr-kotlin/issues/85
    arguments = arguments + listOf("-encoding", "UTF-8")

    antlrClasspath = configurations["antlr"]

    source = project.objects
        .sourceDirectorySet("antlr", "antlr")
        .srcDir("src/main/antlr").apply {
            include("**/*.g4")
        }
    outputDirectory = generatedGrammarSourceDir

    doLast {
        // Workaround for generated code containing warnings
        // https://github.com/Strumenta/antlr-kotlin/issues/82
        generatedGrammarSourceDir.walk()
            .filter(File::isFile)
            .filter { f -> f.extension == "kt" }
            .forEach { file ->
                val text = file.readText()
                file.writeText("@file:Suppress(\"UNCHECKED_CAST\", \"UNUSED_PARAMETER\", \"USELESS_CAST\")\n$text")
            }
    }
}

kotlin.sourceSets.main {
    kotlin.srcDir(generatedGrammarSourceDir)
}

tasks.compileKotlin {
    dependsOn(generateKotlinGrammarSource)
}
