import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
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
    implementation("javax.servlet", "javax.servlet-api", "4.0.1")
    implementation("org.springframework", "spring-webmvc")
    implementation("org.springframework.boot", "spring-boot")
    implementation("org.slf4j", "slf4j-api", "1.7.30")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.7.0")
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
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "15"
        dependsOn(processResources)
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