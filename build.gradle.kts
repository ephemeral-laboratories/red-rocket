import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    id("com.diffplug.spotless") version "6.0.0"
    antlr
}

group = "garden.ephemeral.rocket"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:${project.property("antlr.version")}")

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${project.property("kotlinx-coroutines-core.version")}")
    implementation("org.antlr:antlr4-runtime:${project.property("antlr.version")}")

    testImplementation("io.cucumber:cucumber-java8:${project.property("cucumber.version")}")
    testImplementation("io.cucumber:cucumber-junit:${project.property("cucumber.version")}")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:${project.property("assertk.version")}")
}

tasks.compileKotlin {
    doFirst {
        println("Inputs: ${inputs.files.asPath}")
    }
}

extensions.configure<JavaPluginExtension> {
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<Test> {
    jvmArgs("--add-modules=jdk.incubator.vector")
}

tasks.generateGrammarSource {
    // ANTLR docs say the default encoding is UTF-8 but that appears to be a lie
    this.arguments.addAll(listOf("-encoding", "UTF-8"))
}

spotless {
    val ktlintVersion = "0.43.2"
    kotlin {
        ktlint(ktlintVersion)
    }
    kotlinGradle {
        ktlint(ktlintVersion)
    }
}
