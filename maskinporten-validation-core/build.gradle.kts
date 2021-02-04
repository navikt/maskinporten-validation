import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("se.patrikerdes.use-latest-versions")
    id("net.researchgate.release")
    `maven-publish`
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.11.3")
    api("com.nimbusds", "nimbus-jose-jwt", "9.1.2")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.7.0")
}
tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "15"
    }
    test {
        useJUnitPlatform()
    }
}