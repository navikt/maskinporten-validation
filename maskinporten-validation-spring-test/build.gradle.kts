plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":maskinporten-validation-spring"))
    implementation(project(":maskinporten-validation-test"))
    implementation(kotlin("stdlib"))
    implementation("javax.servlet", "javax.servlet-api", "4.0.1")
    implementation("org.springframework", "spring-webmvc")
    implementation("org.springframework.boot", "spring-boot")
    implementation("org.springframework.boot", "spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.7.0")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "15"
        dependsOn(processResources)
    }
    test {
        useJUnitPlatform()
    }
}