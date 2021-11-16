import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("com.diffplug.spotless") version "6.0.0"
}

group = "garden.ephemeral.rocket"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${project.property("kotlinx-coroutines-core.version")}")

    testImplementation("io.cucumber:cucumber-java8:${project.property("cucumber.version")}")
    testImplementation("io.cucumber:cucumber-junit:${project.property("cucumber.version")}")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:${project.property("assertk.version")}")
}

extensions.configure<JavaPluginExtension> {
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

spotless {
    val ktlintVersion = "0.43.0"
    kotlin {
        ktlint(ktlintVersion).userData(mapOf("disabled_rules" to "no-wildcard-imports"))
    }
    kotlinGradle {
        ktlint(ktlintVersion)
    }
}
