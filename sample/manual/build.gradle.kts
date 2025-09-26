import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":maskinporten-validation-core"))
    testImplementation(project(":maskinporten-validation-test"))
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
