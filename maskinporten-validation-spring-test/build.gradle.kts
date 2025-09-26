import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":maskinporten-validation-spring"))
    api(project(":maskinporten-validation-test"))
    implementation(kotlin("stdlib"))
    implementation("jakarta.servlet", "jakarta.servlet-api", "6.1.0")
    implementation("org.springframework", "spring-webmvc")
    implementation("org.springframework.boot", "spring-boot")
    implementation("org.springframework.boot", "spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.13.4")
}

tasks {
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
//            dependsOn(processResources)
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
