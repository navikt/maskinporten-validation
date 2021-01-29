plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":maskinporten-validation-core"))
    implementation("org.springframework", "spring-webmvc", "5.3.3")
    implementation("javax.servlet","javax.servlet-api","4.0.1")
}
tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "15"
    }
    test {
        useJUnitPlatform()
    }
}