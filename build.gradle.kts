plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.spring") version "2.0.20"
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    `maven-publish`
    `java-library`
}

repositories {
    mavenCentral()
}

subprojects {
    group = "no.nav.pensjonsamhandling"
    version = "0.0.1"

    apply(plugin = "org.gradle.maven-publish")
    apply(plugin = "org.gradle.java-library")

    java {
        withSourcesJar()
        withJavadocJar()
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        api("com.nimbusds", "nimbus-jose-jwt", "10.5") {
            endorseStrictVersions()
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                version = System.getenv("RELEASE_VERSION")
                from(components["java"])
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/navikt/${rootProject.name}")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}
