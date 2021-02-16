import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.12.1")
    api("com.nimbusds", "nimbus-jose-jwt", "9.5")
    api("net.minidev", "json-smart", "2.3")
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