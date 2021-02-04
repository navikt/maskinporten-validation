plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":maskinporten-validation-core"))
    implementation(kotlin("stdlib"))
    implementation("javax.servlet", "javax.servlet-api", "4.0.1")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.7.0")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "15"
    }
    test {
        useJUnitPlatform()
    }
}