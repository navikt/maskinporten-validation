plugins {
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.spring") version "1.7.21"
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.1.0"
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
        api("com.nimbusds", "nimbus-jose-jwt", "9.25.6") {
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
