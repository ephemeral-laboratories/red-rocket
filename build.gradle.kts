import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
    id("com.diffplug.spotless") version "6.9.0"
    antlr
    id("nebula.facet") version "9.4.0"
    id("utf8-workarounds")
}

group = "garden.ephemeral.rocket"
version = "0.1.0"

repositories {
    mavenCentral()
}

facets {
    @Suppress("UNUSED_VARIABLE")
    val samples by registering
}

dependencies {
    antlr(libs.antlr)

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.antlr4.runtime)

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
    val ktlintVersion = "0.46.1"
    kotlin {
        ktlint(ktlintVersion)
            .setUseExperimental(true)
            .editorConfigOverride(mapOf("disabled_rules" to "filename"))
    }
    kotlinGradle {
        ktlint(ktlintVersion)
    }
}
