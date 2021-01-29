import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
    id("se.patrikerdes.use-latest-versions") version "0.2.14"
    id("net.researchgate.release") version "2.8.1"
    `maven-publish`
    `java-library`
}

repositories {
    mavenCentral()
}

subprojects {
    group = "no.nav.pensjonsamhandling"
    version = "0.3.7"
}



//release {
//    newVersionCommitMessage = "[Release Plugin] - next version commit: "
//    tagTemplate = "release-\${version}"
//}
//
//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            from(components["java"])
//        }
//    }
//    repositories {
//        maven {
//            name = "GitHubPackages"
//            url = uri("https://maven.pkg.github.com/navikt/${rootProject.name}")
//            credentials {
//                username = System.getenv("GITHUB_ACTOR")
//                password = System.getenv("GITHUB_TOKEN")
//            }
//        }
//    }
//}