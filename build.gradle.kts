import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id ("org.jetbrains.kotlin.jvm") version "1.5.31"
}

group = "garden.ephemeral.rocket"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.6")

    testImplementation("io.cucumber:cucumber-java8:${project.property("cucumber.version")}")
    testImplementation("io.cucumber:cucumber-junit:${project.property("cucumber.version")}")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:${project.property("assertk.version")}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
