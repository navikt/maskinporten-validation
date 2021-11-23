plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"
    id("org.springframework.boot") version "2.5.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
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

    dependencies {
        api("com.nimbusds", "nimbus-jose-jwt", "9.15.2") {
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