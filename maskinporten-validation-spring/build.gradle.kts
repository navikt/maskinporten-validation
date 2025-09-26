import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":maskinporten-validation-core"))
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.20.0")
    implementation("jakarta.servlet", "jakarta.servlet-api", "6.1.0")
    implementation("org.springframework", "spring-webmvc")
    implementation("org.springframework.boot", "spring-boot")
    implementation("org.springframework.boot", "spring-boot-autoconfigure")
    implementation("org.slf4j", "slf4j-api", "2.0.17")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.13.4")
    testImplementation("org.springframework.boot", "spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    kapt("org.springframework.boot", "spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot", "spring-boot-configuration-processor") //IntelliJ does not detect kapt yet.
}

kapt {
    annotationProcessor("org.springframework.boot.configurationprocessor.ConfigurationMetadataAnnotationProcessor")
}

tasks {
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    withType<BootJar> {
        enabled = false
    }
    withType<Jar> {
        enabled = true
    }
    test {
        useJUnitPlatform()
    }
}
